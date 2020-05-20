package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.*;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.net.EntityData;
import com.cyberbot.bomberman.core.models.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.tiles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main gameplay controller.
 * Handles all contact detection, player actions, entity behaviour.
 */
public final class GameStateController implements Disposable, Updatable, PlayerActionController.Listener, ContactListener {
    private final World world;

    private final TileMap map;

    // Entities, split into separate lists for convenience
    private final List<BombEntity> bombs;
    private final List<PlayerEntity> players;
    private final List<CollectibleEntity> collectibles;

    private final List<WorldChangeListener> listeners;

    public GameStateController(World world, TileMap map) {
        this.world = world;
        this.map = map;
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.listeners = new ArrayList<>();

        world.setContactListener(this);
    }

    @Override
    public void update(float delta) {
        // Update all entities
        entityStream().forEach(entity -> entity.update(delta));

        // Handle bomb explosion
        bombs.forEach(bomb -> {
            if (bomb.isBlown()) {
                onBombExploded(bomb);
                bomb.markToRemove();
            }
        });

        // Remove any entities that have been marked to be removed
        Stream.of(players, collectibles, bombs)
            .forEach(it -> it.removeIf(Entity::isMarkedToRemove));

        // Update players
        players.forEach(player -> {
            Vector2 position = player.getPositionRaw();
            int x = (int) Math.floor(position.x);
            int y = (int) Math.floor(position.y);

            Tile tile = map.getFloor().getTile(x, y);
            if (tile instanceof FloorTile) {
                FloorTile.Properties properties = ((FloorTile) tile).getProperties();
                player.setDragModifier(properties.dragMultiplier);
                player.setMaxSpeedModifier(properties.maxSpeedMultiplier);
            } else {
                player.setDragModifier(1);
                player.setMaxSpeedModifier(1);
            }
        });
    }

    @Override
    public void dispose() {
        map.dispose();
        bombs.forEach(Entity::dispose);
        players.forEach(PlayerEntity::dispose);
    }

    public void addPlayers(Collection<PlayerEntity> players) {
        this.players.addAll(players);
        players.forEach(this::onEntityAdded);
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

    public GameSnapshot createSnapshot() {
        List<EntityData<?>> entities = entityStream().map(Entity::getData).collect(Collectors.toList());
        return new GameSnapshot(entities);
    }

    @Override
    public void onBombPlaced(BombDef bombDef, PlayerEntity executor) {
        BombEntity bomb = new BombEntity(world, bombDef, generateEntityId());
        bombs.add(bomb);
        onEntityAdded(bomb);

        Vector2 position = executor.getPositionRaw();
        float x = (float) Math.floor(position.x) + 0.5f;
        float y = (float) Math.floor(position.y) + 0.5f;

        // Place the bomb on the tile the player's currently at
        bomb.setPositionRaw(new Vector2(x, y));
    }

    @Override
    public void beginContact(Contact contact) {
        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();

        if (a instanceof PhysicalTile || b instanceof PhysicalTile) {
            return;
        }

        PlayerEntity player;
        Entity other;

        if (a instanceof PlayerEntity && b instanceof Entity) {
            player = (PlayerEntity) a;
            other = (Entity) b;
        } else if (b instanceof PlayerEntity && a instanceof Entity) {
            player = (PlayerEntity) b;
            other = (Entity) a;
        } else {
            throw new RuntimeException("Contact detected between non-player entities");
        }

        if (other instanceof PlayerEntity) {
            return; //throw new RuntimeException("Contact detected between two PlayerEntities");
        }

        handleContact(player, other);
    }

    @Override
    public void endContact(Contact contact) {
        // Unused
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Unused
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Unused
    }

    private Stream<Entity> entityStream() {
        return Stream.of(players, collectibles, bombs).flatMap(Collection::stream);
    }

    private void onEntityAdded(Entity entity) {
        listeners.forEach(listener -> listener.onEntityAdded(entity));
    }

    private void onEntityRemoved(Entity entity) {
        listeners.forEach(listener -> listener.onEntityRemoved(entity));
    }

    private void onBombExploded(BombEntity bomb) {
        onEntityRemoved(bomb);
        bomb.dispose();

        int range = (int) bomb.getRange();
        Vector2 position = bomb.getPositionRaw();
        int x = (int) position.x;
        int y = (int) position.y;

        float powerRight = bomb.getPower();
        float powerLeft = bomb.getPower();
        float powerUp = bomb.getPower();
        float powerDown = bomb.getPower();

        TileMapLayer walls = map.getWalls();

        // Right
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x + i, y);
            powerRight = damageTile(tile, powerRight);
            if (powerRight == 0) {
                break;
            }
        }

        // Left
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x - i, y);
            powerLeft = damageTile(tile, powerLeft);
            if (powerLeft == 0) {
                break;
            }
        }

        // Up
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x, y + i);
            powerUp = damageTile(tile, powerUp);
            if (powerUp == 0) {
                break;
            }
        }

        // Down
        for (int i = 1; i <= range; i++) {
            Tile tile = walls.getTile(x, y - i);
            powerDown = damageTile(tile, powerDown);
            if (powerDown == 0) {
                break;
            }
        }
    }

    /**
     * Damages a given tile and destroys it when needed.
     *
     * @param tile  Affected tile
     * @param power The power that's used to damage the tile.
     * @return The power left after the action
     */
    private float damageTile(Tile tile, float power) {
        if (tile instanceof WallTile) {
            WallTile.Properties props = ((WallTile) tile).getProperties();
            float durability = props.durability;
            if (durability == WallTile.Properties.DURABILITY_INFINITE) {
                return 0;
            }

            if (power >= durability) {
                destroyTile(tile);
                return power - durability;
            }
        }

        return power - WallTile.Properties.POWER_DROPOFF;
    }

    private void destroyTile(Tile tile) {
        map.removeWall(tile.getX(), tile.getY());

        // Spawn a random collectible in place of the broken tile
        CollectibleEntity collectible = CollectibleFactory.createRandom(world, generateEntityId());
        if (collectible == null) {
            return;
        }
        collectible.setPosition(tile.getPosition());

        collectibles.add(collectible);
        onEntityAdded(collectible);
    }

    private void handleContact(PlayerEntity player, Entity other) {
        if (other instanceof CollectibleEntity) {
            ItemType itemType = ((CollectibleEntity) other).getItemType();
            Inventory inventory = player.getInventory();

            switch (itemType) {
                case SMALL_BOMB:
                    inventory.incrementMaxQuantity(itemType, true);
                default:
                    inventory.addItem(itemType);
            }

            other.markToRemove();
            onEntityRemoved(other);
        }
    }

    private long generateEntityId() {
        long id;

        do {
            id = ThreadLocalRandom.current().nextLong();
        } while (hasEntity(id));

        return id;
    }

    private boolean hasEntity(long id) {
        return entityStream().map(Entity::getId).anyMatch(pId -> pId == id);
    }
}
