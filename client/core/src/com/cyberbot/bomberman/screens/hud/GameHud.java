package com.cyberbot.bomberman.screens.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
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
    private HealthBar healthBar;
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
        InventoryView inventoryView = new InventoryView(player, skin);

        NinePatch separator = new NinePatch(Atlas.getSkinAtlas().findRegion("separator"));

        left.add(playerView).expandX().padLeft(PPM).row();
        left.add(new Image(separator)).fillX().minHeight(2).prefHeight(2).row();
        left.add(inventoryView).expandY().fill();
//        left.setDebug(true);
    }

    private Table createPlayerView() {
        Table playerView = new Table();

        Image playerAvatar = new Image(Atlas.getInstance().findRegion("Player_bbb_idle_front"));
        healthBar = new HealthBar(player, playerAvatar.getWidth() * 2, PPM / 2);

        Label playerLabel = new Label("James", skin);
        playerLabel.setAlignment(Align.center);

        TextButton crap = new TextButton("Kill em", skin);
        crap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                healthBar.changeHealthBy(-10);
            }
        });

        playerView.add(playerAvatar)
            .padBottom(5)
            .minWidth(playerAvatar.getWidth() * 2)
            .minHeight(playerAvatar.getHeight() * 2);
        playerView.add(playerLabel).prefWidth(999).padBottom(5).row();
        playerView.add(healthBar).padBottom(PPM / 2);
        playerView.add(crap);

//        playerView.setDebug(true);

        return playerView;
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
