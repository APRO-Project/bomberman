package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.core.controllers.WorldChangeListener;
import com.cyberbot.bomberman.core.models.entities.Entity;
import com.cyberbot.bomberman.core.models.entities.PlayerEntity;
import com.cyberbot.bomberman.utils.Atlas;

import static com.cyberbot.bomberman.core.utils.Constants.PPM;

public class GameHud extends Stage implements WorldChangeListener {

    private Skin skin;
    private Table left;
    private Table right;

    // Left side
    public final LocalPlayerView localPlayerView;
    public final InventoryView inventoryView;

    // Right side
    public final TimerView timerView;
    public final PlayerListView playerListView;

    private PlayerEntity localPlayerEntity;

    public GameHud(Viewport viewport) {
        super(viewport);

        initSkin();

        localPlayerView = new LocalPlayerView(skin);
        inventoryView = new InventoryView(skin);
        timerView = new TimerView(skin);
        playerListView = new PlayerListView(skin);
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
        right.align(Align.topLeft);
        right.pad(5);

        addActor(left);
        addActor(right);

        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));

        left.add(localPlayerView)
            .expandX()
            .fillX()
            .row();
        left.add(new Image(separator))
            .expandX()
            .fillX()
            .minHeight(2)
            .prefHeight(2)
            .pad(10)
            .row();
        left.add(inventoryView)
            .expand()
            .fill();

        right.add(timerView)
            .expandX()
            .fillX()
            .row();
        right.add(new Image(separator))
            .expandX()
            .fillX()
            .minHeight(2)
            .prefHeight(2)
            .pad(10)
            .row();
        right.add(playerListView)
            .expand()
            .fill();

//        left.setDebug(true);
//        right.setDebug(true);
    }

    private void initSkin() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        skin = new Skin(Atlas.getSkinAtlas());
        skin.add("default_font", font);
        skin.load(Gdx.files.internal("skins/skin.json"));
    }

    public void setLocalPlayerEntity(PlayerEntity entity) {
        localPlayerEntity = entity;
        inventoryView.setPlayerEntity(entity);
        localPlayerView.setPlayerEntity(entity);
    }

    public void setLocalPlayerName(String name) {
        localPlayerView.setPlayerName(name);
    }

    public void addToPlayerList(String name, long id) {
        playerListView.addPlayer(name, id);
    }

    @Override
    public void onEntityAdded(Entity entity) { }

    @Override
    public void onEntityRemoved(Entity entity) {
        if(entity instanceof PlayerEntity && entity.getId() != localPlayerEntity.getId()) {
            playerListView.onPlayerDeath((PlayerEntity) entity);
        }
    }
}
