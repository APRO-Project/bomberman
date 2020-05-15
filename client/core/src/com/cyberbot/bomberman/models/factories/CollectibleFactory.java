package com.cyberbot.bomberman.models.factories;

import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.models.entities.CollectibleEntity;
import com.cyberbot.bomberman.models.items.ItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CollectibleFactory {
    private static final float DROP_CHANCE = 0.25F;
    private static final HashMap<ItemType, Float> ITEM_WEIGHTS = new HashMap<>();

    static {
        ITEM_WEIGHTS.put(ItemType.SMALL_BOMB, 1F);
        ITEM_WEIGHTS.put(ItemType.UPGRADE_MOVEMENT_SPEED, 1F);
        ITEM_WEIGHTS.put(ItemType.UPGRADE_REFILL_SPEED, 1F);
        ITEM_WEIGHTS.put(ItemType.UPGRADE_ARMOR, 1F);
    }

    public static CollectibleEntity createRandom(World world) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        float drop = random.nextFloat();
        if (drop > DROP_CHANCE) {
            return null;
        }

        float weightSum = ITEM_WEIGHTS.values().stream().reduce(0F, Float::sum);

        float pick = random.nextFloat() * weightSum;

        for (Map.Entry<ItemType, Float> entry : ITEM_WEIGHTS.entrySet()) {
            pick -= entry.getValue();
            if (pick < 0) {
                return new CollectibleEntity(world, entry.getKey());
            }
        }

        throw new RuntimeException("Random picked float out of bounds: " + pick);
    }
}
