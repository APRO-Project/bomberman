package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.PlayerState;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.actions.Action;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.net.GameSnapshotListener;
import com.cyberbot.bomberman.core.models.net.data.EntityData;
import com.cyberbot.bomberman.core.models.net.data.EntityDataPair;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.cyberbot.bomberman.core.utils.Constants.SIM_RATE;

public class LocalWorldController implements Updatable, Disposable, GameSnapshotListener {
    private final int tickRate;
    private final int interpolationDelay = 3;
    private final float replyInterTime = 0.05f;
    private final float maxPlayerOffset = 0.15f;
    private final World world;
    private final Map<Long, Entity> entities;
    private final Queue<GameSnapshotPacket> gameSnapshots;
    private final SnapshotQueue interactionQueue;
    private final PlayerStateQueue playerStateQueue;
    private final List<WorldChangeListener> listeners;

    /**
     * Latest snapshot applied to the world.
     */
    private GameSnapshotPacket currentPacket;
    /**
     * Next snapshot to be applied to the world, and the one we interpolate towards.
     */
    private GameSnapshotPacket nextPacket;
    /**
     * The latest snapshot received from the server
     */
    private GameSnapshotPacket newestPacket;

    private PlayerEntity localPlayer;

    private int snapshotLength;
    private float interpFraction;

    private PlayerData playerToInterpStart;
    private PlayerData playerToInterpEnd;
    private float replayInterpFraction;

    public LocalWorldController(World world, int tickRate, SnapshotQueue interactionQueue,
                                PlayerStateQueue playerStateQueue, PlayerEntity localPlayer) {
        this.tickRate = tickRate;
        this.world = world;
        this.interactionQueue = interactionQueue;
        this.playerStateQueue = playerStateQueue;
        this.localPlayer = localPlayer;
        this.entities = new HashMap<>();
        this.gameSnapshots = new ArrayDeque<>();
        this.listeners = new ArrayList<>();
        this.interpFraction = 0;
        this.snapshotLength = 0;
        this.playerToInterpStart = null;
        this.playerToInterpEnd = null;
    }

    @Override
    public void update(float delta) {
        world.step(delta, 6, 2);

        // Update all entities
        entities.forEach((key, entity) -> entity.update(delta));

        // Remove any entities marked for removal
        entities.entrySet().removeIf(it -> it.getValue().isMarkedToRemove());

        if (newestPacket != null) {
            validateWorld(newestPacket);
        }

        if (playerToInterpStart != null) {
            interpolateReply(delta);
        }

        interpolate(delta);
    }

    public void addListener(WorldChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WorldChangeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    private void interpolate(float delta) {
        interpFraction += delta * tickRate;
        boolean snapshotChanged = false;
        while (interpFraction > snapshotLength) {
            interpFraction -= snapshotLength;

            if (!loadNextSnapshot()) {
                return;
            }

            snapshotChanged = true;
        }

        if (snapshotChanged) {
            applySnapshot(currentPacket.getSnapshot());
        }

        float fraction = interpFraction / snapshotLength;

        List<EntityDataPair> updated = getUpdatedEntities(currentPacket.getSnapshot());
        for (EntityDataPair pair : updated) {
            EntityData<?> nextData = nextPacket.getSnapshot().getEntity(pair.getData().getId());
            if (nextData == null) continue;

            pair.getEntity().updateFromData(pair.getData(), nextData, fraction);
        }
    }

    private void interpolateReply(float delta) {
        localPlayer.updateFromData(playerToInterpStart, playerToInterpEnd, replayInterpFraction);
        replayInterpFraction = Math.min(1, replayInterpFraction + delta / replyInterTime);

        if (replayInterpFraction == 1) {
            playerToInterpStart = null;
            playerToInterpEnd = null;
        }
    }

    private boolean loadNextSnapshot() {
        if (nextPacket == null) {
            if (gameSnapshots.size() > 0) {
                nextPacket = gameSnapshots.remove();
            } else {
                return false;
            }
        }

        if (gameSnapshots.size() < interpolationDelay) {
            return false;
        }

        currentPacket = nextPacket;
        nextPacket = gameSnapshots.remove();
        snapshotLength = nextPacket.getSequence() - currentPacket.getSequence();

        snapshotLength = Math.max(Math.min(snapshotLength, interpolationDelay), 0);

        return true;
    }

    /**
     * Replies any player interactions captured between the latest received snapshot
     * and now to effectively move the player to the same location as the server will perceive at this moment.
     * <p>
     * Creates a new simulated player staring at the <code>startingData</code> to apply the actions to.
     * After the simulation is completed the player is removed and it's new data returned.
     *
     * @param startingData  The data to create the simulated player from.
     * @param startVelocity The staring velocity of the simulated player.
     * @return The resulting player data.
     */
    private PlayerData replayMovement(PlayerData startingData, Vector2 startVelocity) {
        // Disable collision and velocity for the local player, so that the simulated player does not
        // collide with the local player and the local player does not move during the rollback.
        localPlayer.setCollisions(false);
        localPlayer.setVelocity(Vector2.Zero);

        PlayerEntity simulatedPlayer = startingData.createEntity(world);
        simulatedPlayer.setVelocityRaw(startVelocity);
        PlayerActionController movementController = new PlayerActionController(simulatedPlayer);
        playerStateQueue.clear();
        for (PlayerSnapshotPacket packet : interactionQueue) {
            for (List<Action> actions : packet.getSnapshot().actions) {
                float delta = 1f / SIM_RATE;
                movementController.onActions(actions);
                movementController.update(delta);
                world.step(delta, 6, 2);
            }

            PlayerState state = new PlayerState(packet.getSequence(),
                simulatedPlayer.getPositionRaw(),
                simulatedPlayer.getVelocity());

            playerStateQueue.addState(state);
        }

        PlayerData endData = simulatedPlayer.getData();
        Vector2 resultingVelocity = simulatedPlayer.getVelocityRaw();
        simulatedPlayer.dispose();

        // Restore velocity and collisions
        localPlayer.setCollisions(true);
        localPlayer.setVelocity(resultingVelocity);

        return endData;
    }

    /**
     * Applies a given snapshot to the current Box2D world by adding, updating and removing entities as needed.
     * <p>
     * Requires the Box2D world to not be locked, so it has to be called during an update
     * and not asynchronously.
     *
     * @param snapshot Game snapshot to rollback the World to
     * @throws ConcurrentModificationException When the Box2D world is locked.
     * @throws NoSuchElementException          When includeLocalPlayer was set to <code>true</code> and
     *                                         the local player is not present in the snapshot's entity list.
     */
    private void applySnapshot(GameSnapshot snapshot) {
        if (world.isLocked()) {
            throw new ConcurrentModificationException("Cannot rollback while world is locked");
        }

        List<Entity> removed = getRemovedEntities(snapshot);
        List<EntityData<?>> added = getAddedEntityData(snapshot);
        List<EntityDataPair> updated = getUpdatedEntities(snapshot);

        List<Long> removedIds = removed.stream().map(Entity::getId).collect(Collectors.toList());

        // Dispose and remove references to all removed entities
        removed.forEach(Entity::dispose);
        entities.keySet().removeAll(removedIds);

        // Update all updated entities
        updated.forEach(it -> it.getEntity().updateFromData(it.getData()));

        // Create and add references to all added entities
        List<Entity> createdEntities = added.stream()
            .map(it -> it.createEntity(world))
            .collect(Collectors.toList());

        createdEntities.forEach(it -> entities.put(it.getId(), it));

        // Notify any listeners about changes
        listeners.forEach(l -> removed.forEach(l::onEntityRemoved));
        listeners.forEach(l -> createdEntities.forEach(l::onEntityAdded));
    }

    private List<Entity> getRemovedEntities(GameSnapshot snapshot) {
        return entities.entrySet().stream()
            .filter(it -> !snapshot.hasEntity(it.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    private List<EntityData<?>> getAddedEntityData(GameSnapshot snapshot) {
        return snapshot.getEntities().entrySet().stream()
            .filter(it -> it.getKey() != localPlayer.getId() && !hasEntity(it.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    private List<EntityDataPair> getUpdatedEntities(GameSnapshot snapshot) {
        return snapshot.getEntities().entrySet().stream()
            .filter(it -> hasEntity(it.getKey()))
            .map(it -> new EntityDataPair(entities.get(it.getKey()), it.getValue()))
            .collect(Collectors.toList());
    }

    private boolean hasEntity(long id) {
        return entities.containsKey(id);
    }

    private void validateWorld(@NotNull GameSnapshotPacket packet) {
        int sequence = packet.getSequence();
        PlayerState localState = playerStateQueue.removeUntil(sequence);

        if (localState != null) {
            validatePlayerPosition(localState, packet.getSnapshot());
        }

        interactionQueue.removeUntil(sequence);
    }

    private void validatePlayerPosition(@NotNull PlayerState localState, @NotNull GameSnapshot remoteSnapshot) {
        PlayerData remotePlayer = (PlayerData) remoteSnapshot.getEntity(localPlayer.getId());
        if (remotePlayer != null) {
            Vector2 remotePosition = remotePlayer.getPosition().toVector2();
            boolean replay = new Vector2(remotePosition).sub(localState.position).len() > maxPlayerOffset;

            if (replay) {
                playerToInterpStart = localPlayer.getData();
                playerToInterpEnd = replayMovement(remotePlayer, localState.velocity);
                replayInterpFraction = 0;
            }

            int a = 0;
        }
    }

    @Override
    public void onNewSnapshot(GameSnapshotPacket packet) {
        gameSnapshots.add(packet);
        newestPacket = packet;
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}
