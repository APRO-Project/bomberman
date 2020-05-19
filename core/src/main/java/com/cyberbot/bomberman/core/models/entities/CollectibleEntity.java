package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.items.ItemType;
import com.cyberbot.bomberman.core.models.net.CollectibleData;

/**
 * A world entity that can be collected by a player.
 *
 * @see ItemType
 */
public class CollectibleEntity extends Entity {
    private final ItemType itemType;

    public CollectibleEntity(World world, ItemType itemType, long id) {
        super(world, id);
        this.itemType = itemType;
    }

    @Override
    public void createBody(World world) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(0, 0);
        def.fixedRotation = true;

        body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.325f);

        Fixture fixture = body.createFixture(shape, 1);
        fixture.setSensor(true);
        body.setUserData(this);
        shape.dispose();
    }

    @Override
    public CollectibleData getData() {
        return new CollectibleData(id, getPosition(), itemType);
    }

    public ItemType getItemType() {
        return itemType;
    }
}
