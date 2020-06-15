package com.cyberbot.bomberman.screens.finish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyberbot.bomberman.utils.Atlas;

import java.util.List;

public class FinishLayout extends Stage {

    final Skin skin;
    private final float spaceHeight = 14;

    private final Label[] playerLabels;
    private final FinishInteraction delegate;

    private final float worldWidth = getViewport().getWorldWidth();
    private final float worldHeight = getViewport().getWorldHeight();
    private final float tableWidth = worldWidth / 3;
    private final Skin skin2;

    public FinishLayout(Viewport viewport, FinishInteraction delegate) {
        super(viewport);
        this.delegate = delegate;
        this.playerLabels = new Label[4];
        skin = new Skin(Gdx.files.internal("skins/clean-crispy/skin/clean-crispy-ui.json"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skins/8bit_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = 20;
        BitmapFont font = generator.generateFont(fontParams);

        this.skin2 = new Skin(Atlas.getSkinAtlas());
        skin2.add("default_font", font);
        skin2.load(Gdx.files.internal("skins/skin.json"));
    }

    public void createFinishUi() {
        Table ui = new Table();
        ui.setDebug(false);

        ui.setPosition((worldWidth - tableWidth) / 2, 1);
        ui.setWidth(tableWidth);
        ui.setHeight(worldHeight);
        addActor(ui);

        Label title = new Label("Leaderboard", skin2);
        title.setWidth(tableWidth);
        title.setAlignment(1);
        title.setFontScale(1.5f);
        title.setWrap(false);
        float playerLabelHeight = 6;
        ui.add(title).width(tableWidth).height(playerLabelHeight).row();
        ui.add().height(spaceHeight * 2).row();

        for (int i = 0; i < 4; i++) {
            Label label = new Label("", skin2);
            label.setWidth(tableWidth);
            label.setAlignment(Align.left);
            label.setFontScale(1);
            label.setWrap(false);
            playerLabels[i] = label;
            ui.add(label).width(tableWidth).height(playerLabelHeight).row();
            ui.add().height(spaceHeight).row();
        }
        ui.add().height(spaceHeight).row();

        TextButton leaveFinishButton = new TextButton("Leave", skin2);
        leaveFinishButton.getLabel().setFontScale(0.85f);
        setupButton(ui, tableWidth, leaveFinishButton);
        leaveFinishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                delegate.leaveFinish();
            }
        });
    }

    private void setupButton(Table table, float tableWidth, TextButton button) {
        float buttonHeight = 30;
        table.add(button).width(tableWidth - 10).height(buttonHeight).row();
        table.add().height(spaceHeight).row();
    }

    public void updateScoreTable(List<String> scoreTable) {
        for (int i = 0; i < scoreTable.size(); i++) {
            playerLabels[i].setText((i + 1) + ". " + scoreTable.get(i));
        }
        for (int i = 3; i > scoreTable.size() - 1; i--) {
            playerLabels[i].setText("");
        }
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
