package com.cyberbot.bomberman.screens.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.utils.Utils;
import com.cyberbot.bomberman.utils.Atlas;

public class LoginLayout extends Stage {

    final Skin skin;
    TextField nicknameField;
    TextField passwordField;
    TextField ipField;

    final LoginInteraction delegate;

    private final Skin skin2;

    public LoginLayout(Viewport viewport, LoginInteraction delegate) {
        super(viewport);

        this.delegate = delegate;
        skin = new Skin(Gdx.files.internal("skins/clean-crispy/skin/clean-crispy-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));
    }

    private final float buttonHeight = 30;
    private final float spaceHeight = 2;

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

        nicknameField = new TextField("", skin);
        nicknameField.setMessageText("Nick");
        nicknameField.setText(Utils.generatePlayerNick());
        nicknameField.setAlignment(1);
        nicknameField.setMaxLength(12);
        addEnterKeyListener(nicknameField);

        passwordField = new TextField("", skin);
        passwordField.setAlignment(1);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        addEnterKeyListener(passwordField);

        ipField = new TextField("127.0.0.1", skin);
        ipField.setAlignment(1);
        addEnterKeyListener(ipField);

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
        table.add(button).width(tableWidth).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }

    private void addEnterKeyListener(TextField textField){
        textField.setTextFieldListener((field, c) -> {
            if (c == '\n' || c == '\r'){
                delegate.login(nicknameField.getText(), passwordField.getText(), ipField.getText());
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

