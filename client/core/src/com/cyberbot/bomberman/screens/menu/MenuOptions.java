package com.cyberbot.bomberman.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.utils.Atlas;

public class MenuOptions extends Stage {

    final MenuInteraction delegate;
    final Skin skin;
    TextField lobbyIdField;

    public MenuOptions(Viewport viewport, MenuInteraction delegate) {
        super(viewport);
        this.delegate = delegate;
        skin = new Skin(Gdx.files.internal("skins/clean-crispy/skin/clean-crispy-ui.json"));
    }

    private final float buttonHeight = 30;

    public void createMenuOptions() {
        Table options = new Table();
        options.setDebug(false);

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

        Label title = new Label("Boomerman", skin2);
        title.setWidth(tableWidth);
        title.setAlignment(1);
        title.setFontScale(1.8f);
        options.add(title).width(tableWidth).height(6).row();
        options.add().height(30).row();

        TextButton createLobby = new TextButton("Create Lobby", skin2);
        createLobby.getLabel().setFontScale(0.85f);
        setupButton(options, tableWidth, createLobby);
        createLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.createLobby();
            }
        });

        TextButton joinLobby = new TextButton("Join Lobby", skin2);
        joinLobby.getLabel().setFontScale(0.85f);
        setupButton(options, tableWidth, joinLobby);
        joinLobby.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.joinLobby(lobbyIdField.getText());
            }
        });

        lobbyIdField = new TextField("", skin);
        lobbyIdField.setMessageText("<Input id to join>");
        lobbyIdField.setAlignment(1);
        addEnterKeyListener(lobbyIdField);

        options.add(lobbyIdField).width(tableWidth).height(buttonHeight).row();
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        table.add(button).width(tableWidth).height(buttonHeight).row();
        float spaceHeight = 2;
        table.add().height(spaceHeight).row();
    }

    private void addEnterKeyListener(TextField textField){
        textField.setTextFieldListener((field, c) -> {
            if (c == '\n' || c == '\r'){
                delegate.joinLobby(lobbyIdField.getText());
            }
        });
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
