package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;

import java.util.List;

public interface ActionListener {
    void onActions(List<Action> actions);
}
