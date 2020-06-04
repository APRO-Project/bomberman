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
import com.cyberbot.bomberman.controllers.GameScreenController;
import com.cyberbot.bomberman.screens.ScreenState;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.Arrays;

public class LobbyLayout extends Stage {

    private final float playerLabelHeight = 150;
    private final float spaceHeight = 10;
    private final float buttonHeight = 150;
    private boolean isOwner;

    Label[] playerLabels;
    boolean[] playerEmpty;

    final GameScreenController gameScreenController;

    public LobbyLayout(Viewport viewport, boolean isOwner, GameScreenController gameScreenController) {
        super(viewport);
        this.isOwner = isOwner;
        this.gameScreenController = gameScreenController;
        playerLabels = new Label[4];
        playerEmpty = new boolean[4];
        Arrays.fill(playerEmpty, true);
    }

    public void createLobbyUi() {
        Table ui = new Table();
        ui.setDebug(false);


        float worldWidth = getViewport().getWorldWidth();
        float worldHeight = getViewport().getWorldHeight();
        float tableWidth = worldWidth / 3;

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

        for (int i = 1; i < 5; i++) {
            Label label = new Label("Empty", skin2);
            label.setWidth(tableWidth);
            label.setAlignment(1);
            label.setFontScale(4);
            label.setWrap(false);
            playerLabels[i - 1] = label;
            ui.add(label).width(tableWidth).height(playerLabelHeight).row();
            ui.add().height(spaceHeight).row();
        }

        if (isOwner) {
            Table ui2 = new Table();
            ui2.setDebug(false);

            ui2.setPosition(1, 1);
            ui2.setWidth(tableWidth);
            ui2.setHeight(worldHeight);
            addActor(ui2);

            TextButton createLobby = new TextButton("Start Game", skin2);
            setupButton(ui2, tableWidth, createLobby);
            createLobby.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //TODO add server request
                    gameScreenController.setScreen(ScreenState.GAME);
                }
            });
        }
    }

    //TODO use public addPlayer api in recieving server request
    public void addPlayer(String playerName) {
        for (int i = 0; i < 4; i++) {
            if (playerEmpty[i]) {
                playerEmpty[i] = false;
                playerLabels[i].setText(playerName);
                return;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Too many players added");
    }

    public void addPlayer(String playername, int number){
        playerEmpty[number] = false;
        playerLabels[number].setText(playername);
    }

    public void removePlayer(int number){
        playerEmpty[number] = true;
        playerLabels[number].setText("Empty");
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        button.getLabel().setFontScale(4);
        table.add(button).width(tableWidth - 50).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }
}
