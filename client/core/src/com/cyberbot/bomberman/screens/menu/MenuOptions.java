package com.cyberbot.bomberman.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.utils.Atlas;

public class MenuOptions extends Stage {
    private Table options;
    private Skin skin;

    public MenuOptions(Viewport viewport) {
        super(viewport);
    }

    public void createMenuOptions(){
        options = new Table();
        options.setDebug(true);

        float worldWidth = getViewport().getWorldWidth();
        float worldHeight = getViewport().getWorldHeight();

        float tableWidth = worldWidth / 3;

        options.setPosition((worldWidth - tableWidth)/2,1);
        options.setWidth(tableWidth);
        options.setHeight(worldHeight);
        addActor(options);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        skin = new Skin(Atlas.getSkinAtlas());
        skin.add("default_font", font);
        skin.load(Gdx.files.internal("skins/skin.json"));
    }
}
