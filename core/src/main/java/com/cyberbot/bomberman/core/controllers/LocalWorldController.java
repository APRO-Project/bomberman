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
import com.cyberbot.bomberman.core.models.net.data.PhysicalTileData;
import com.cyberbot.bomberman.core.models.net.data.PlayerData;
import com.cyberbot.bomberman.core.models.net.packets.GameSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.packets.PlayerSnapshotPacket;
import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.net.snapshots.PlayerSnapshot;
import com.cyberbot.bomberman.core.models.tiles.PhysicalTile;
import com.cyberbot.bomberman.core.models.tiles.Tile;
import com.cyberbot.bomberman.core.models.tiles.TileMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the hearth of the hybrid world simulation. It handles:
 * <ul>
 *     <li>Local player movement simulation - to achieve the best on screen response,
 *     the local player's movement is simulated locally and on the server.</li>
 *
 *     <li>World snapshot interpolation - the interpolation delay of {@value INTERPOLATION_DELAY}
 *     has been determined to be optimal in terms of packet loss compensation and perceived delay.</li>
 *
 *     <li>Local player replay and interpolation - in case the local player and it's
 *     representation on the server gets out of sync, the player has to be moved to where
 *     the server perceived the player at the time of the snapshot.
 *     Then all user actions have to be replied on a simulated player and interpolated between
 *     it's current state and the state resulting from the simulation, which should result
 *     in the same state as the server will perceive after receiving the current {@link PlayerSnapshot}.</li>
 *
 *     <li>World delta - used to communicate any changes to the corresponding
 *     {@link WorldChangeListener} and {@link TileMap.ChangeListener}.</li>
 * </ul>
 */
public class LocalWorldController implements Updatable, Disposable, GameSnapshotListener, ActionListener {
    private final static int INTERPOLATION_DELAY = 2;
    private final static float REPLY_INTERP_TIME = 0.05f;
    private final static float MAX_PLAYER_OFFSET = 0.5f;

    private final int tickRate;
    private final int simRate;

    private final World world;
    private final TileMap map;
    private final HashMap<Long, Entity> entities;
    private final HashMap<Integer, PhysicalTile> walls;

    private final Queue<GameSnapshotPacket> gameSnapshots;

    private final PlayerActionController playerActionController;
    private final SnapshotQueue snapshotQueue;
    private final PlayerStateQueue playerStateQueue;

    private final PlayerEntity localPlayer;
    private boolean playerAlive;

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

    private int snapshotLength;

    private float interpFraction;
    private PlayerData playerToInterpStart;

    private PlayerData playerToInterpEnd;
    private float replayInterpFraction;

    public LocalWorldController(World world, TileMap map, int tickRate, int simRate, PlayerData playerData) {
        this(world, map, tickRate, simRate, playerData, tickRate * 4);
    }

    public LocalWorldController(World world, TileMap map, int tickRate, int simRate, PlayerData playerData, int bufferSize) {
        this.tickRate = tickRate;
        this.simRate = simRate;
        this.world = world;
        this.map = map;
        this.localPlayer = playerData.createEntity(world);

        this.playerActionController = new PlayerActionController(localPlayer);
        this.snapshotQueue = new SnapshotQueue(localPlayer.getId(), bufferSize);
        this.playerStateQueue = new PlayerStateQueue(bufferSize);

        this.entities = new HashMap<>();
        this.walls = map.getWalls().stream()
            .filter(it -> it instanceof PhysicalTile)   /* Y U do dis Java... */
            .collect(Collectors.toMap(Tile::hashCode, it -> (PhysicalTile) it, (prev, next) -> next, HashMap::new));

        this.gameSnapshots = new ArrayDeque<>();
        this.listeners = new ArrayList<>();
        this.interpFraction = 0;
        this.snapshotLength = 0;
        this.playerToInterpStart = null;
        this.playerToInterpEnd = null;
        this.playerAlive = true;
    }

    public boolean isWorldLocked() {
        return world.isLocked();
    }

    @Override
    public void update(float delta) {
        world.step(delta, 6, 2);

        if (playerAlive) {
            playerActionController.update(delta);
            localPlayer.updateFromEnvironment(map);
        }

        // Update all entities
        entities.forEach((key, entity) -> entity.update(delta));

        // Remove any entities marked for removal
        entities.values().stream().filter(Entity::isMarkedToRemove).forEach(this::onEntityRemoved);
        entities.entrySet().removeIf(it -> it.getValue().isMarkedToRemove());

        // Only validate every packet once
        if (newestPacket != null) {
            validateWorld(newestPacket);
        }

        // Interpolate after reply
        if (playerToInterpStart != null) {
            interpolateReply(delta);
        }

        interpolate(delta);
    }

    public PlayerEntity getLocalPlayer() {
        return localPlayer;
    }

    @Override
    public void onActions(List<Action> actions) {
        playerActionController.onActions(actions);
        snapshotQueue.onActions(actions);
    }

    @Override
    public void onNewSnapshot(GameSnapshotPacket packet) {
        gameSnapshots.add(packet);
        newestPacket = packet;
    }

    @Override
    public void dispose() {
        entities.values().forEach(Entity::dispose);
        localPlayer.dispose();
        world.dispose();
    }

    public void addListener(WorldChangeListener listener) {
        addListener(listener, false);
    }

    public void addListener(WorldChangeListener listener, boolean sendExisting) {
        listeners.add(listener);
        if (sendExisting) {
            listener.onEntityAdded(localPlayer);
            entities.values().forEach(listener::onEntityAdded);
        }
    }

    public void removeListener(WorldChangeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public PlayerSnapshotPacket createSnapshot() {
        PlayerSnapshotPacket packet = snapshotQueue.createSnapshot();
        PlayerState state = new PlayerState(
            snapshotQueue.getLatestSequence(),
            localPlayer.getPosition(),
            localPlayer.getVelocity());

        playerStateQueue.addState(state);

        return packet;
    }

    private void interpolate(float delta) {
        interpFraction += delta * tickRate;
        boolean snapshotChanged = false;

        // If the snapshot receive rate is higher then the simulation rate,
        // there's a chance we need to skip a couple snapshots.
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
        replayInterpFraction = Math.min(1, replayInterpFraction + delta / REPLY_INTERP_TIME);

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

        if (gameSnapshots.size() < INTERPOLATION_DELAY) {
            return false;
        }

        currentPacket = nextPacket;
        nextPacket = gameSnapshots.remove();
        snapshotLength = nextPacket.getSequence() - currentPacket.getSequence();

        snapshotLength = Math.max(Math.min(snapshotLength, INTERPOLATION_DELAY), 0);

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
        localPlayer.setVelocityRaw(Vector2.Zero);

        PlayerEntity simulatedPlayer = startingData.createEntity(world);
        simulatedPlayer.setVelocity(startVelocity);
        PlayerActionController movementController = new PlayerActionController(simulatedPlayer);
        playerStateQueue.clear();
        for (PlayerSnapshotPacket packet : snapshotQueue) {
            for (List<Action> actions : packet.getSnapshot().actions) {
                float delta = 1f / simRate;
                movementController.onActions(actions);
                movementController.update(delta);
                simulatedPlayer.updateFromEnvironment(map);
                world.step(delta, 6, 2);
            }

            PlayerState state = new PlayerState(packet.getSequence(),
                simulatedPlayer.getPosition(),
                simulatedPlayer.getVelocityRaw());

            playerStateQueue.addState(state);
        }

        PlayerData endData = simulatedPlayer.getData();
        Vector2 resultingVelocity = simulatedPlayer.getVelocity();
        simulatedPlayer.dispose();

        // Restore velocity and collisions
        localPlayer.setCollisions(true);
        localPlayer.setVelocityRaw(resultingVelocity);

        return endData;
    }

    /**
     * Applies a given snapshot to the current Box2D world by adding,
     * updating and removing entities and tiles as needed.
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
            throw new ConcurrentModificationException("Cannot apply snapshot while world is locked");
        }

        if (playerAlive) {
            applySnapshotToPlayer(snapshot);
        }

        applySnapshotToEntities(snapshot);
        applySnapshotToWalls(snapshot);
    }

    private void applySnapshotToPlayer(GameSnapshot snapshot) {
        PlayerData playerData = (PlayerData) snapshot.getEntity(localPlayer.getId());
        if (playerData == null) {
            playerAlive = false;
            localPlayer.setHp(0);
            listeners.forEach(it -> it.onEntityRemoved(localPlayer));
            return;
        }

        localPlayer.setHp(playerData.getHp());
    }

    private void applySnapshotToEntities(GameSnapshot snapshot) {
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
        removed.forEach(this::onEntityRemoved);
        createdEntities.forEach(this::onEntityAdded);
    }

    private void applySnapshotToWalls(GameSnapshot snapshot) {
        List<PhysicalTile> removed = getRemovedWalls(snapshot);
        List<PhysicalTileData<?>> added = getAddedWallData(snapshot);

        removed.forEach(it -> {
            walls.remove(it.hashCode());
            map.removeWall(it.getX(), it.getY());
        });

        List<PhysicalTile> createdTiles = added.stream()
            .map(it -> it.createTile(world))
            .collect(Collectors.toList());

        createdTiles.forEach(it -> {
            walls.put(it.hashCode(), it);
            map.addWall(it);
        });
    }

    private void onEntityAdded(Entity entity) {
        listeners.forEach(listener -> listener.onEntityAdded(entity));
    }

    private void onEntityRemoved(Entity entity) {
        listeners.forEach(listener -> listener.onEntityRemoved(entity));
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

    private List<PhysicalTile> getRemovedWalls(GameSnapshot snapshot) {
        return walls.entrySet().stream()
            .filter(it -> !snapshot.hasWall(it.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    private List<PhysicalTileData<?>> getAddedWallData(GameSnapshot snapshot) {
        return snapshot.getWalls().entrySet().stream()
            .filter(it -> !walls.containsKey(it.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    // TODO: Create TileDataPair and get updated tiles when variable tile damage is introduced

    private boolean hasEntity(long id) {
        return entities.containsKey(id);
    }

    private void validateWorld(@NotNull GameSnapshotPacket packet) {
        int sequence = packet.getSequence();

        PlayerState localState = playerStateQueue.removeUntil(sequence);

        // Do not attempt to validate player while it's position is still being interpolated from a previous replay
        if (playerAlive && localState != null && playerToInterpEnd == null) {
            validatePlayerPosition(localState, packet.getSnapshot());
        }

        snapshotQueue.removeUntil(sequence);
    }

    private void validatePlayerPosition(@NotNull PlayerState localState, @NotNull GameSnapshot remoteSnapshot) {
        PlayerData remotePlayer = (PlayerData) remoteSnapshot.getEntity(localPlayer.getId());

        if (remotePlayer != null) {
            localPlayer.setInventory(remotePlayer.getInventory());
            Vector2 remotePosition = remotePlayer.getPosition().toVector2();
            float delta = new Vector2(remotePosition).sub(localState.position).len();
            float velocityModifier = Math.max(2.5f, localState.velocity.len()) / PlayerEntity.MAX_VELOCITY;
            boolean replay = delta > MAX_PLAYER_OFFSET * velocityModifier;

            if (replay) {
                playerToInterpStart = localPlayer.getData();
                playerToInterpEnd = replayMovement(remotePlayer, localState.velocity);
                replayInterpFraction = 0;
            }
        }
    }
}
