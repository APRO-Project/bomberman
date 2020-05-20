package com.cyberbot.bomberman.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.utils.Atlas;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class GameHud extends Stage {

    private Skin skin;
    private Table left;
    private Table right;
    private PlayerEntity player;

    public GameHud(Viewport viewport) {
        super(viewport);
    }

    public void createHud(float mapVirtualWidth) {
        left = new Table();
        right = new Table();

        final float mapPixelWidth = mapVirtualWidth * PPM;
        final float tablePixelWidth = (getViewport().getWorldWidth() - mapPixelWidth) / 2;

        left.setPosition(1, 1);
        left.setWidth(tablePixelWidth - 2);
        left.setHeight(mapPixelWidth - 2);
        right.setPosition(tablePixelWidth + mapPixelWidth + 1, 1);
        right.setWidth(tablePixelWidth - 2);
        right.setHeight(mapPixelWidth - 2);

        left.align(Align.topLeft);
        left.pad(5);
        right.align(Align.center | Align.top);

        addActor(left);
        addActor(right);

        // TODO: Create initSkin method
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        skin = new Skin(Atlas.getSkinAtlas());
        skin.add("default_font", font);
        skin.load(Gdx.files.internal("skins/skin.json"));

        Table playerView = createPlayerView();
        Table inventoryView = createInventoryView();

        left.add(playerView).expandX();
        left.row();
        left.add(inventoryView).expandY();
//        left.setDebug(true);
    }

    private Table createPlayerView() {
        Table playerView = new Table();

        Image image = new Image(Atlas.getInstance().findRegion("Player_bbb_idle_front"));
        Label label = new Label("James", skin);

        playerView.add(image).pad(5).minWidth(image.getWidth() * 2).minHeight(image.getHeight() * 2);
        playerView.add(label).prefWidth(999);

        return playerView;
    }

    private Table createInventoryView() {
        Table inventoryView = new Table();

        TextButton buttonStart = new TextButton("EXIT GAME", skin);
        buttonStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        inventoryView.add(buttonStart).row();

        return inventoryView;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
