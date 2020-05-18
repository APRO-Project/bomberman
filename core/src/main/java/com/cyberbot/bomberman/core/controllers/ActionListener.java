package com.cyberbot.bomberman.core.controllers;

import com.cyberbot.bomberman.core.models.actions.Action;

public interface ActionListener {
    void executeAction(Action action);
}
