package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.*;
import com.cyberbot.bomberman.core.models.items.Inventory;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.net.snapshots.GameSnapshot;
import com.cyberbot.bomberman.core.models.tiles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The main gameplay controller.
 * Handles all contact detection, player actions, entity behaviour.
 */
public final class GameStateController implements Disposable, Updatable, PlayerActionController.Listener, Box2DContactListener {
    private final World world;

    private final TileMap map;

    // Entities, split into separate lists for convenience
    private final List<BombEntity> bombs;
    private final List<PlayerEntity> players;
    private final List<CollectibleEntity> collectibles;
    private final List<ExplosionEntity> explosions;

    private final List<WorldChangeListener> listeners;

    public GameStateController(World world, TileMap map) {
        this.world = world;
        this.map = map;
        this.players = new ArrayList<>();
        this.bombs = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.listeners = new ArrayList<>();

        world.setContactListener(this);
    }

    @Override
    public void update(float delta) {
        // Update all entities
        entityStream().forEach(entity -> entity.update(delta));

        // Handle bomb explosion
        bombs.stream().filter(BombEntity::isBlown).forEach(this::onBombExploded);

        // Handle explosion decay
        explosions.stream().filter(ExplosionEntity::isDecayed).forEach(Entity::markToRemove);

        // Dispose any entities that have not yet been disposed and are marked for removal
        entityStream()
            .filter(Predicate.not(Entity::isRemoved).and(Entity::isMarkedToRemove))
            .forEach(this::onEntityRemoved);

        // Remove any entities that have been marked to be removed
        Stream.of(players, collectibles, bombs, explosions)
            .forEach(list -> list.removeIf(Entity::isMarkedToRemove));

        // Update players
        players.forEach(player -> player.updateFromEnvironment(map));
    }

    @Override
    public void dispose() {
        map.dispose();
        entityStream().filter(Predicate.not(Entity::isRemoved)).forEach(this::onEntityRemoved);
    }

    public void addPlayer(PlayerEntity player) {
        this.players.add(player);
        onEntityAdded(player);
    }

    public void addPlayers(Collection<PlayerEntity> players) {
        players.forEach(this::addPlayer);
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
        return new GameSnapshot(entityStream(), map.getWalls().stream());
    }

    public long generateEntityId() {
        long id;

        do {
            id = ThreadLocalRandom.current().nextLong();
        } while (hasEntity(id));

        return id;
    }

    public boolean hasEntity(long id) {
        return entityStream().map(Entity::getId).anyMatch(pId -> pId == id);
    }

    @Override
    public void onBombPlaced(BombDef bombDef, PlayerEntity executor) {
        BombEntity bomb = new BombEntity(world, bombDef, generateEntityId());
        bombs.add(bomb);
        onEntityAdded(bomb);

        Vector2 position = executor.getPosition();
        float x = (float) Math.floor(position.x) + 0.5f;
        float y = (float) Math.floor(position.y) + 0.5f;

        // Place the bomb on the tile the player's currently at
        bomb.setPosition(new Vector2(x, y));
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
            return;
            // throw new RuntimeException("Contact detected between non-player entities");
        }

        if (other instanceof PlayerEntity) {
            return;
        }

        handleContact(player, other);
    }

    private Stream<Entity> entityStream() {
        return Stream.of(players, collectibles, bombs, explosions).flatMap(Collection::stream);
    }

    private void onEntityAdded(Entity entity) {
        listeners.forEach(listener -> listener.onEntityAdded(entity));
    }

    private void onEntityRemoved(Entity entity) {
        entity.dispose();
        listeners.forEach(listener -> listener.onEntityRemoved(entity));
    }

    @SuppressWarnings("DuplicatedCode")
    private void onBombExploded(BombEntity bomb) {
        bomb.markToRemove();

        final float bombRange = bomb.getRange();
        final int range = (int) bombRange;
        final Vector2 position = bomb.getPosition();
        final int x = (int) position.x;
        final int y = (int) position.y;

        float bombPower = bomb.getPower();
        float powerRight = bombPower;
        float powerLeft = bombPower;
        float powerUp = bombPower;
        float powerDown = bombPower;

        TileMapLayer walls = map.getWalls();

        addExplosion(x, y, 2 * bombPower);

        // Right
        for (int i = 1; i <= range; i++) {
            int x1 = x + i;
            Tile tile = walls.getTile(x1, y);

            powerRight = damageTile(tile, powerRight);
            addExplosion(x1, y, powerRight);
            powerRight = Math.max(0, powerRight - bomb.getPowerDropoff());

            if (powerRight == 0) {
                break;
            }
        }

        // Left
        for (int i = 1; i <= range; i++) {
            int x1 = x - i;
            Tile tile = walls.getTile(x1, y);

            powerLeft = damageTile(tile, powerLeft);
            addExplosion(x1, y, powerLeft);
            powerLeft = Math.max(0, powerLeft - bomb.getPowerDropoff());

            if (powerLeft == 0) {
                break;
            }
        }

        // Up
        for (int i = 1; i <= range; i++) {
            int y1 = y + i;
            Tile tile = walls.getTile(x, y1);

            powerUp = damageTile(tile, powerUp);
            addExplosion(x, y1, powerUp);
            powerUp = Math.max(0, powerUp - bomb.getPowerDropoff());

            if (powerUp == 0) {
                break;
            }
        }

        // Down
        for (int i = 1; i <= range; i++) {
            int y1 = y - i;
            Tile tile = walls.getTile(x, y1);

            powerDown = damageTile(tile, powerDown);
            addExplosion(x, y1, powerDown);
            powerDown = Math.max(0, powerDown - bomb.getPowerDropoff());

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
        float remainingPower = power;
        if (tile instanceof WallTile) {
            remainingPower = ((WallTile) tile).subtractDurability(power);
            if (((WallTile) tile).isDestroyed()) {
                destroyTile(tile);
            }
        }

        return remainingPower;
    }

    private void destroyTile(Tile tile) {
        map.removeWall(tile.getX(), tile.getY());

        // Spawn a random collectible in place of the broken tile
        CollectibleEntity collectible = CollectibleFactory.createRandom(world, generateEntityId());
        if (collectible == null) {
            return;
        }
        collectible.setPositionRaw(tile.getPositionRaw());

        collectibles.add(collectible);
        onEntityAdded(collectible);
    }

    private void addExplosion(int x, int y, float power) {
        if (map.getWalls().getTile(x, y) != null) {
            return;
        }

        ExplosionEntity explosion = new ExplosionEntity(world, generateEntityId(), power, 1);
        explosion.setPosition(new Vector2(x + 0.5f, y + 0.5f));
        explosions.add(explosion);
    }

    private void handleContact(PlayerEntity player, Entity other) {
        if (other instanceof CollectibleEntity) {
            ItemType itemType = ((CollectibleEntity) other).getItemType();
            Inventory inventory = player.getInventory();

            switch (itemType) {
                case SMALL_BOMB:
                case MEDIUM_BOMB:
                case NUKE:
                    inventory.incrementMaxQuantity(itemType, true);
                    break;
                default:
                    inventory.addItem(itemType);
            }

            other.markToRemove();
        } else if (other instanceof ExplosionEntity) {
            // TODO: Better damage handling
            ((ExplosionEntity) other).damagePlayer(player);

            if (player.isDead()) {
                player.markToRemove();
            }
        }
    }
}
