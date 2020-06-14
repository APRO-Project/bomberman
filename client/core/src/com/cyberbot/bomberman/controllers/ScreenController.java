package com.cyberbot.bomberman.controllers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.cyberbot.bomberman.core.models.net.packets.*;
import com.cyberbot.bomberman.core.models.tiles.MapLoadException;
import com.cyberbot.bomberman.core.utils.Utils;
import com.cyberbot.bomberman.net.ClientControlListener;
import com.cyberbot.bomberman.net.ControlService;
import com.cyberbot.bomberman.screens.AbstractScreen;
import com.cyberbot.bomberman.screens.GameScreen;
import com.cyberbot.bomberman.screens.finish.FinishInteraction;
import com.cyberbot.bomberman.screens.finish.FinishScreen;
import com.cyberbot.bomberman.screens.lobby.LobbyInteraction;
import com.cyberbot.bomberman.screens.lobby.LobbyScreen;
import com.cyberbot.bomberman.screens.login.LoginInteraction;
import com.cyberbot.bomberman.screens.login.LoginScreen;
import com.cyberbot.bomberman.screens.menu.MenuInteraction;
import com.cyberbot.bomberman.screens.menu.MenuScreen;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public final class ScreenController implements MenuInteraction, LobbyInteraction,
    LoginInteraction, FinishInteraction, ClientControlListener {
    private final Game game;
    private final LobbyScreen lobby;
    private final MenuScreen menu;
    private final LoginScreen login;
    private final FinishScreen finish;
    private InetSocketAddress serverAddress;
    private ControlService controlService;
    private final int defaultPort;

    private String username;
    private String password;

    private AbstractScreen currentScreen;

    public ScreenController(final Game game) {
        this(game, 8038);
    }

    public ScreenController(final Game game, int defaultPort) {
        this.game = game;
        this.defaultPort = defaultPort;

        this.username = null;
        this.password = null;

        this.menu = new MenuScreen(this);
        this.lobby = new LobbyScreen(this);
        this.login = new LoginScreen(this);
        this.finish = new FinishScreen(this);

        setScreen(login);
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
        setScreen(menu);
        controlService.sendPacket(new LobbyLeaveRequest());
    }

    @Override
    public void startGame() {
        controlService.sendPacket(new GameStartRequest());
    }

    @Override
    public void login(String username, String password, String host) {

        if (username.equals("")) {
            showError("Nickname can't be empty");
            return;
        }

        try {
            serverAddress = Utils.parseServerString(host, defaultPort);
            controlService = new ControlService(serverAddress);
            controlService.getListeners().add(this);
            new Thread(controlService).start();

            this.username = username;
            this.password = password;
        } catch (URISyntaxException e) {
            Gdx.app.log("ControlService", "Invalid uri: " + e.getMessage());
            showError("Login unsuccessful");
        }
    }

    @Override
    public void onClientConnected() {
        Gdx.app.log("ControlService", "Client connected to the server");
        ClientRegisterRequest packet = new ClientRegisterRequest(username, Utils.hashPassword(password));
        controlService.sendPacket(packet);
    }

    @Override
    public void onConnectionError(@NotNull IOException e) {
        Gdx.app.log("ControlService", "Unable to connect: " + e.getMessage());
        showError("No server connection");
    }

    @Override
    public void onClientDisconnected() {
        Gdx.app.log("ControlService", "Disconnected");
        Gdx.app.postRunnable(() -> setScreen(login));
        showError("Client disconnected");
    }

    @Override
    public void onRegisterResponse(@NotNull ClientRegisterResponse payload) {
        if (payload.getSuccess() != null && payload.getSuccess()) {
            Long id = payload.getClient() != null ? payload.getClient().getId() : null;
            Gdx.app.log("ControlService", "Client registered, assigned id: " + id);

            Gdx.app.postRunnable(() -> setScreen(menu));
        } else {
            Gdx.app.log("ControlService", "Register failed");
            showError("Failed to register");
        }
    }

    @Override
    public void onLobbyCreate(@NotNull LobbyCreateResponse payload) {
        if (payload.getSuccess() != null && payload.getSuccess()) { // Well, it's Java so null handling is BAD...
            sendLobbyJoinRequest(payload.getId());
        } else {
            Gdx.app.log("ControlService", "Unable to create lobby");
            showError("Failed to create lobby");
        }
    }

    @Override
    public void onLobbyJoin(@NotNull LobbyJoinResponse payload) {
        if (payload.getSuccess() != null && payload.getSuccess()) { // No comment needed, one word - Java
            Gdx.app.postRunnable(() -> setScreen(lobby));
        } else {
            Gdx.app.log("ControlService", "Unable to join lobby");
            showError("Failed to join to lobby");
        }
    }

    @Override
    public void onLobbyUpdate(@NotNull LobbyUpdate payload) {
        Gdx.app.log("ControlService", "Lobby updated");
        Gdx.app.postRunnable(() -> {
            Boolean owner = payload.isOwner();
            if (owner != null) {
                lobby.updateLobby(payload.getLobby(), owner);
            }
        });
    }

    @Override
    public void onGameStart(@NotNull GameStart payload) {
        Gdx.app.log("ControlService", "Starting the game");

        if(payload.getPort() == null) {
            showError("Server has sent null port");
            return;
        }

        Gdx.app.postRunnable(() -> {
            try {

                final GameScreen gameScreen = new GameScreen(
                    payload.getPlayerInit(),
                    "./map/bomberman_main.tmx",
                    new InetSocketAddress(serverAddress.getAddress(), payload.getPort()),
                    payload.getLobby()
                );
                setScreen(gameScreen);

            } catch (MapLoadException e) {
                e.printStackTrace();
                showError("Failed to start game");
            }
        });
    }

    @Override
    public void onGameEnd(@NotNull GameEnd payload) {
        // TODO: Display end game screen with leaderboard
        String leaderboard = payload.getLeaderboard().stream().map(Client::getNick).collect(Collectors.joining());
        Gdx.app.log("ControlService", leaderboard);
    }

    @Override
    public void onError(@NotNull ErrorResponse payload) {
        Gdx.app.log("ControlService", payload.getError());
    }

    private void showError(String message) {
        currentScreen.showError(message);
    }

    private void sendLobbyJoinRequest(String lobbyId) {
        controlService.sendPacket(new LobbyJoinRequest(lobbyId));
    }

    private void setScreen(AbstractScreen screen) {
        currentScreen = screen;
        game.setScreen(screen);
    }

    @Override
    public void leaveFinish() {
        setScreen(menu);
    }
}
