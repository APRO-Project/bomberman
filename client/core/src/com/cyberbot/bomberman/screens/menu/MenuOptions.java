package com.cyberbot.bomberman.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.controllers.GameScreenController;
import com.cyberbot.bomberman.screens.ScreenState;
import com.cyberbot.bomberman.utils.Atlas;

public class MenuOptions extends Stage {

    final GameScreenController gameScreenController;
    final Skin skin;
    TextField nicknameField;
    TextField lobbyIdField;

    public MenuOptions(Viewport viewport, GameScreenController gameScreenController) {
        super(viewport);
        this.gameScreenController = gameScreenController;
        skin = new Skin(Gdx.files.internal("skins\\clean-crispy\\skin\\clean-crispy-ui.json"));
        skin.getFont("font").getData().setScale(5);
    }

    private final float buttonHeight = 150;
    private final float spaceHeight = 10;

    public void createMenuOptions() {
        Table options = new Table();
        options.setDebug(true);

        float worldWidth = getViewport().getWorldWidth();
        float worldHeight = getViewport().getWorldHeight();

        float tableWidth = worldWidth / 3;

        options.setPosition((worldWidth - tableWidth) / 2, 1);
        options.setWidth(tableWidth);
        options.setHeight(worldHeight);
        addActor(options);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        Skin skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));


        TextButton createLobby = new TextButton("Create Lobby", skin2);
        setupButton(options, tableWidth, createLobby);
        createLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nickname = nicknameField.getText();
                //TODO add server request
                gameScreenController.setScreen(ScreenState.LOBBY, nickname, true);
            }
        });

        TextButton joinLobby = new TextButton("Join Lobby", skin2);
        setupButton(options, tableWidth, joinLobby);
        joinLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nickname = nicknameField.getText();
                String lobbyId = lobbyIdField.getText();
                //TODO add server request
                gameScreenController.setScreen(ScreenState.LOBBY);
            }
        });

        nicknameField = new TextField("Player" + (int) (Math.random() * 9999), skin);
        nicknameField.setAlignment(1);
        nicknameField.setMaxLength(10);

        lobbyIdField = new TextField("lobby id", skin);
        lobbyIdField.setAlignment(1);

        options.add(nicknameField).width(tableWidth).height(buttonHeight).row();
        options.add().height(spaceHeight).row();
        options.add(lobbyIdField).width(tableWidth).height(buttonHeight).row();
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        button.getLabel().setFontScale(4);
        table.add(button).width(tableWidth).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }
}
