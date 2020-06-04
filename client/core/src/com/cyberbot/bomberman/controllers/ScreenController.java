package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.core.models.net.Connection;
import com.cyberbot.bomberman.core.models.net.packets.*;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.core.utils.Utils;
import com.cyberbot.bomberman.net.ClientControlListener;
import com.cyberbot.bomberman.net.ControlService;
import com.cyberbot.bomberman.screens.GameScreen;
import com.cyberbot.bomberman.screens.lobby.LobbyInteraction;
import com.cyberbot.bomberman.screens.lobby.LobbyScreen;
import com.cyberbot.bomberman.screens.menu.MenuInteraction;
import com.cyberbot.bomberman.screens.menu.MenuScreen;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class ScreenController implements MenuInteraction, LobbyInteraction, ClientControlListener {
    private final Game game;
    private final ControlService controlService;
    private final LobbyScreen lobby;
    private final MenuScreen menu;
    private final InetAddress serverAddress;

    public ScreenController(final Game game) {
        this.game = game;

        menu = new MenuScreen(this);
        lobby = new LobbyScreen(this);

        game.setScreen(menu);

        // TODO: Get server from options
        try {
            serverAddress = InetAddress.getLocalHost();
            controlService = new ControlService(new Connection(8038, serverAddress));
            controlService.getListeners().add(this);
            new Thread(controlService).start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createLobby() {
        controlService.sendPacket(new LobbyCreateRequest());
    }

    @Override
    public void joinLobby(String lobbyId) {
        sendLobbyJoinRequest(lobbyId);
    }

    @Override
    public void leaveLobby() {
        lobby.hide();
        menu.show();
        game.setScreen(menu);
    }

    @Override
    public void startGame() {
        controlService.sendPacket(new GameStartRequest());
    }

    @Override
    public void onClientConnected() {
        // TODO: Get nick from user settings or show before menu screen
        controlService.sendPacket(new ClientRegisterRequest(Utils.generateLobbyId(10)));
    }

    @Override
    public void onLobbyCreate(@NotNull LobbyCreateResponse payload) {
        if (payload.getSuccess() != null && payload.getSuccess()) { // Well, it's Java so null handling is BAD...
            sendLobbyJoinRequest(payload.getId());
        } else {
            // TODO: Show error to the user
        }
    }

    @Override
    public void onLobbyJoin(@NotNull LobbyJoinResponse payload) {
        if (payload.getSuccess() != null && payload.getSuccess()) { // No comment needed, one word - Java
            Gdx.app.postRunnable(() -> game.setScreen(lobby));
        } else {
            // TODO: Show error to the user
        }
    }

    @Override
    public void onLobbyUpdate(@NotNull LobbyUpdate payload) {
        Gdx.app.postRunnable(() -> lobby.updateLobby(payload.getLobby()));
    }

    @Override
    public void onGameStart(@NotNull GameStart payload) {
        Gdx.app.postRunnable(() -> {
            try {

                final GameScreen gameScreen = new GameScreen(
                    payload.getPlayerInit(),
                    "./map/bomberman_main.tmx",
                    new Connection(payload.getPort(), serverAddress)
                );
                lobby.hide();
                game.setScreen(gameScreen);

            } catch (IOException | MissingLayersException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                // TODO: Show error to the user
            }
        });
    }

    @Override
    public void onError(@NotNull ErrorResponse packet) {
        Gdx.app.log("ControlService", packet.getError());
    }

    private void sendLobbyJoinRequest(String lobbyId) {
        controlService.sendPacket(new LobbyJoinRequest(lobbyId));
    }


}
