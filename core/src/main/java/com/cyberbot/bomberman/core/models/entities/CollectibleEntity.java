package com.cyberbot.bomberman.core.models.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.items.ItemType;

public class CollectibleEntity extends Entity {
    private ItemType itemType;

    public CollectibleEntity(World world, ItemType itemType) {
        super(world);
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

    public ItemType getItemType() {
        return itemType;
    }
}
