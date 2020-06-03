package com.cyberbot.bomberman.screens.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.Client;
import com.cyberbot.bomberman.core.models.tiles.MissingLayersException;
import com.cyberbot.bomberman.screens.GameScreen;
import com.cyberbot.bomberman.utils.Atlas;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MenuOptions extends Stage {

    public MenuOptions(Viewport viewport) {
        super(viewport);
    }

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

        Skin skin = new Skin(Atlas.getSkinAtlas());
        skin.add("default_font", font);
        skin.load(Gdx.files.internal("skins/skin.json"));


        TextButton play = new TextButton("Play", skin);
        play.getLabel().setFontScale(10);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen((Client) Gdx.app.getApplicationListener()));
                } catch (MissingLayersException | IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                }
            }
        });
        options.add(play).row();
    }
}
