package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.Updatable;
import com.cyberbot.bomberman.core.models.defs.BombDef;
import com.cyberbot.bomberman.core.models.entities.BombEntity;
import com.cyberbot.bomberman.core.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.core.models.factories.CollectibleFactory;
import com.cyberbot.bomberman.core.models.tiles.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class GameStateController implements Disposable, Updatable, ActionController.Listener, ContactListener {
    private final World world;

    private final TileMap map;
    private final List<BombEntity> bombs;
    private final List<PlayerEntity> players;
    private final List<CollectibleEntity> collectibles;
    private final List<ChangeListener> listeners;

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
        Stream.of(players, collectibles, bombs)
            .flatMap(Collection::stream)
            .forEach(entity -> entity.update(delta));

        bombs.forEach(bomb -> {
            if (bomb.isBlown()) {
                onBombExploded(bomb);
            }
        });

        Stream.of(players, collectibles, bombs)
            .forEach(it -> it.removeIf(Entity::isMarkedToRemove));

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
        players.forEach(player -> listeners.forEach(listener -> listener.onEntityAdded(player)));
    }

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    @Override
    public void onBombPlaced(BombDef bombDef, PlayerEntity executor) {
        BombEntity bomb = new BombEntity(world, bombDef);
        Vector2 position = executor.getPositionRaw();
        float x = (float) Math.floor(position.x) + 0.5f;
        float y = (float) Math.floor(position.y) + 0.5f;

        bomb.setPositionRaw(new Vector2(x, y));

        bombs.add(bomb);
        listeners.forEach(listener -> listener.onEntityAdded(bomb));

    }

    private void onBombExploded(BombEntity bomb) {
        listeners.forEach(listener -> listener.onEntityRemoved(bomb));
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

        CollectibleEntity collectible = CollectibleFactory.createRandom(world);
        if (collectible == null) {
            return;
        }
        collectible.setPosition(tile.getPosition());

        collectibles.add(collectible);
        listeners.forEach(listener -> listener.onEntityAdded(collectible));
    }

    private void handleContact(PlayerEntity player, Entity other) {
        if (other instanceof CollectibleEntity) {
            player.getInventory().collectItem(((CollectibleEntity) other).getItemType());
            other.markToRemove();
            listeners.forEach(listener -> listener.onEntityRemoved(other));
        }
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
            other = (Entity) b;
        } else {
            throw new RuntimeException("Contact detected between non-player entities");
        }

        if (other instanceof PlayerEntity) {
            throw new RuntimeException("Contact detected between two PlayerEntities");
        }

        handleContact(player, other);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public interface ChangeListener {
        void onEntityAdded(Entity entity);

        void onEntityRemoved(Entity entity);
    }
}
