package com.cyberbot.bomberman.screens.login;

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
import com.cyberbot.bomberman.utils.Atlas;

public class LoginLayout extends Stage {

    final Skin skin;
    TextField nicknameField;
    TextField passwordField;
    TextField ipField;

    final LoginInteraction delegate;

    public LoginLayout(Viewport viewport, LoginInteraction delegate) {
        super(viewport);

        this.delegate = delegate;
        skin = new Skin(Gdx.files.internal("skins/clean-crispy/skin/clean-crispy-ui.json"));
        skin.getFont("font").getData().setScale(5);
    }

    private final float buttonHeight = 150;
    private final float spaceHeight = 10;

    public void createLoginLayout() {
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

        nicknameField = new TextField("", skin);
        nicknameField.setMessageText("Nick");
        nicknameField.setAlignment(1);
        nicknameField.setMaxLength(12);

        passwordField = new TextField("", skin);
        passwordField.setAlignment(1);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        ipField = new TextField("127.0.0.1", skin);
        ipField.setAlignment(1);

        options.add(nicknameField).width(tableWidth).height(buttonHeight).row();
        options.add().height(spaceHeight).row();

        options.add(passwordField).width(tableWidth).height(buttonHeight).row();
        options.add().height(spaceHeight).row();

        options.add(ipField).width(tableWidth).height(buttonHeight).row();
        options.add().height(spaceHeight).row();

        TextButton loginButton = new TextButton("Login", skin2);
        setupButton(options, tableWidth, loginButton);
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.login(nicknameField.getText(), passwordField.getText(), ipField.getText());
            }
        });
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        button.getLabel().setFontScale(4);
        table.add(button).width(tableWidth).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }
}

