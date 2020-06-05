package com.cyberbot.bomberman.screens.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.models.net.packets.Client;
import com.cyberbot.bomberman.core.models.net.packets.Lobby;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.ArrayList;

public class LobbyLayout extends Stage {

    private final float playerLabelHeight = 150;
    private final float spaceHeight = 10;

    private final Label[] playerLabels;

    private final LobbyInteraction delegate;


    private final float worldWidth = getViewport().getWorldWidth();
    private final float worldHeight = getViewport().getWorldHeight();
    private final float tableWidth = worldWidth / 3;

    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter fontParams;
    BitmapFont font;

    Skin skin2;

    Lobby lobby;


    public LobbyLayout(Viewport viewport, LobbyInteraction delegate) {
        super(viewport);
        this.delegate = delegate;
        this.playerLabels = new Label[4];

        generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        font = generator.generateFont(fontParams);

        skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));
        lobby = new Lobby();
    }

    public void createLobbyUi() {
        Table ui = new Table();
        ui.setDebug(false);


        ui.setPosition((worldWidth - tableWidth) / 2, 1);
        ui.setWidth(tableWidth);
        ui.setHeight(worldHeight);
        addActor(ui);


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        Skin skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));

        Label title = new Label("Players", skin2);
        title.setWidth(tableWidth);
        title.setAlignment(1);
        title.setFontScale(8);
        title.setWrap(false);
        ui.add(title).width(tableWidth).height(playerLabelHeight).row();
        ui.add().height(50).row();

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

        Table ui2 = new Table();
        ui2.setDebug(false);

        ui2.setPosition(1, 1);
        ui2.setWidth(tableWidth);
        ui2.setHeight(worldHeight);
        addActor(ui2);

        if (isOwner) {

            TextButton startGameButton = new TextButton("Start Game", skin2);
            setupButton(ui2, tableWidth, startGameButton);
            startGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    delegate.startGame();
                }
            });

            ui2.add().height(10).row();
        }
        Label lobbyID = new Label(lobby.getId(), skin2);
        lobbyID.setWidth(tableWidth);
        lobbyID.setAlignment(1);
        lobbyID.setFontScale(8);
        lobbyID.setWrap(false);
        ui2.add(lobbyID).width(tableWidth).height(playerLabelHeight).row();

        ArrayList<Client> clients = lobby.getClients();
        for (int i = 0; i < clients.size(); i++) {
            addPlayer(clients.get(i).getNick(), i);
        }
        for (int i = 3; i > clients.size() - 1; i++) {
            removePlayer(i);
        }
    }
}
