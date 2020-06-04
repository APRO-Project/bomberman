package com.cyberbot.bomberman.controllers;

import com.cyberbot.bomberman.screens.ScreenState;

public interface ScreenChangeInterface {
    void setScreen(ScreenState state);
    public void setScreen(ScreenState state, String playerName, boolean isOwner);
}
