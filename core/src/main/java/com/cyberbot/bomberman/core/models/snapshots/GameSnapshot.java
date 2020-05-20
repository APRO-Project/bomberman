package com.cyberbot.bomberman.core.models.snapshots;

import com.cyberbot.bomberman.core.models.net.EntityData;
import com.cyberbot.bomberman.core.models.net.PlayerData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: Add tiles
public class GameSnapshot implements Serializable {
    private Integer sequence;
    private final List<EntityData<?>> entities;
    private PlayerData playerData;

    public GameSnapshot(GameSnapshot snapshot, PlayerData playerData, Integer sequence) {
        this(new ArrayList<>(snapshot.entities));
        this.sequence = sequence;
        // TODO: Uncomment
        //setPlayerData(playerData);
    }

    public GameSnapshot(List<EntityData<?>> entities) {
        this.entities = entities;
        this.sequence = null;
    }

    public boolean hasEntity(long id) {
        return entities.stream().anyMatch(it -> it.getId() == id);
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
        entities.removeIf(it -> it.getId() == playerData.getId());
    }

    public List<EntityData<?>> getEntities() {
        return entities;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Integer getSequence() {
        return sequence;
    }
}
