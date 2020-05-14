package com.cyberbot.bomberman.models.items;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.models.entities.BombEntity;

public class SmallBombItem extends EntityItem<BombEntity> {

    public SmallBombItem(String atlasPath) {
        super(atlasPath);
    }

    @Override
    public BombEntity createEntity(World world, String atlasPath) {

        BombEntity bombEntity = new BombEntity(world, atlasPath);
        return bombEntity;
    }
}
