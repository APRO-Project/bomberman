package com.cyberbot.bomberman.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.models.net.packets.Client;
import com.cyberbot.bomberman.core.models.net.packets.Lobby;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.ArrayList;

public class LobbyLayout extends Stage {

    final Skin skin;
    private final float spaceHeight = 0;

    private final Label[] playerLabels;
    private final LobbyInteraction delegate;

    private final float worldWidth = getViewport().getWorldWidth();
    private final float worldHeight = getViewport().getWorldHeight();
    private final float tableWidth = worldWidth / 3;
    private final Skin skin2;

    private Label lobbyId;
    private TextButton startGameButton;

    public LobbyLayout(Viewport viewport, LobbyInteraction delegate) {
        super(viewport);
        this.delegate = delegate;
        this.playerLabels = new Label[4];
        skin = new Skin(Gdx.files.internal("skins/clean-crispy/skin/clean-crispy-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        this.skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));
    }

    public void createLobbyUi() {
        Table ui = new Table();
        ui.setDebug(false);

        ui.setPosition((worldWidth - tableWidth) / 2, 1);
        ui.setWidth(tableWidth);
        ui.setHeight(worldHeight);
        addActor(ui);

        Label title = new Label("Players", skin2);
        title.setWidth(tableWidth);
        title.setAlignment(1);
        title.setFontScale(8);
        title.setWrap(false);
        float playerLabelHeight = 150;
        ui.add(title).width(tableWidth).height(playerLabelHeight).row();

        for (int i = 0; i < 4; i++) {
            Label label = new Label("Empty", skin2);
            label.setWidth(tableWidth);
            label.setAlignment(1);
            label.setFontScale(4);
            label.setWrap(false);
            playerLabels[i] = label;
            ui.add(label).width(tableWidth).height(playerLabelHeight).row();
            ui.add().height(spaceHeight).row();
        }

        Table ui3 = new Table();
        ui3.setDebug(false);

        ui3.setPosition(worldWidth - tableWidth, 1);
        ui3.setWidth(tableWidth);
        ui3.setHeight(worldHeight);
        addActor(ui3);

        TextButton leaveLobbyButton = new TextButton("Leave", skin2);
        setupButton(ui3, tableWidth, leaveLobbyButton);
        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.leaveLobby();
            }
        });

        Table ui2 = new Table();
        ui2.setDebug(false);

        ui2.setPosition(1, 1);
        ui2.setWidth(tableWidth);
        ui2.setHeight(worldHeight);
        addActor(ui2);

        startGameButton = new TextButton("Start Game", skin2);
        setupButton(ui2, tableWidth, startGameButton);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.startGame();

            }
        });

        startGameButton.setVisible(false);

        ui.add().height(30).row();

        Label lobbyIDText = new Label("Lobby ID", skin2);
        lobbyIDText.setWidth(tableWidth);
        lobbyIDText.setAlignment(1);
        lobbyIDText.setFontScale(4);
        lobbyIDText.setWrap(false);
        ui.add(lobbyIDText).width(tableWidth).height(playerLabelHeight).row();

        lobbyId = new Label("", skin2);
        lobbyId.setWidth(tableWidth);
        lobbyId.setAlignment(1);
        lobbyId.setFontScale(3);
        lobbyId.setWrap(false);
        ui.add(lobbyId).width(tableWidth).height(playerLabelHeight).row();
    }

    public void addPlayer(String playername, int number) {
        playerLabels[number].setText(playername);
    }

    public void removePlayer(int number) {
        playerLabels[number].setText("Empty");
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        button.getLabel().setFontScale(4);
        float buttonHeight = 150;
        table.add(button).width(tableWidth - 50).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }

    public void updateLobby(Lobby lobby, boolean isOwner) {
        lobbyId.setText(lobby.getId());

        if (isOwner) {
            startGameButton.setVisible(true);
        }

        ArrayList<Client> clients = lobby.getClients();
        for (int i = 0; i < clients.size(); i++) {
            addPlayer(clients.get(i).getNick(), i);
        }
        for (int i = 3; i > clients.size() - 1; i--) {
            removePlayer(i);
        }
    }

    public void showError(String msg) {
        Dialog dialog = new Dialog("Error", skin);
        dialog.text(msg);
        dialog.setMovable(false);
        dialog.button("Ok");
        dialog.key(Input.Keys.ENTER, true);
        dialog.show(this);
    }
}
