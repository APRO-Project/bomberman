package com.cyberbot.bomberman.controllers;

import com.cyberbot.bomberman.screens.ScreenState;

public interface ScreenChangeInterface {
    void setMenuScreen();
    void setGameScreen();
    public void setLobbyScreen(String playerName, boolean isOwner);
    public void setLobbyScreen(boolean isOwner);
}
