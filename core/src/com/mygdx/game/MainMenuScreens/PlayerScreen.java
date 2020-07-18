package com.mygdx.game.MainMenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.MainGame.MainScreen;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.util.GameConfig;
import com.mygdx.game.util.GdxUtils;

public class PlayerScreen extends ScreenAdapter {
    private Stage stage;
    private BombermanGame game;
    private Viewport viewport;
    private Camera camera;
    private AssetManager assetManager;
    private Skin skin;

    public PlayerScreen(BombermanGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        this.skin = assetManager.get(AssetDescriptors.GAME_UI);
    }

    @Override public void render(float delta) {
        GdxUtils.clearScreen(Color.TEAL);
        stage.act();
        stage.draw();
        inputExit();
    }

    private void inputExit() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            game.safeExit();
    }

    @Override public void show() {
        //super.show();
        camera = new OrthographicCamera(GameConfig.GRAPHICS_WIDTH, GameConfig.GRAPHICS_HEIGHT);
        viewport = new ScreenViewport(camera);

        viewport.update((int)GameConfig.GRAPHICS_WIDTH, (int)GameConfig.GRAPHICS_HEIGHT, true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        stage.addActor(createUI());
    }
    public Actor createUI() {
        Table table = new Table();
        table.setFillParent(true);

        TextButton players2 = new TextButton("2 players", skin);
        players2.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayerNamesScreen(game, 2));
            }
        });
        TextButton players3 = new TextButton("3 players", skin);
        players3.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayerNamesScreen(game, 3));
            }
        });
        TextButton players4 = new TextButton("4 players", skin);
        players4.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayerNamesScreen(game, 4));
            }
        });

        TextButton exitBtn = new TextButton("Exit", skin);

        exitBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.safeExit();
            }
        });
        table.add();
        table.add();
        table.add(exitBtn).align(Align.right);

        table.row();
        table.add().height(100);
        table.row().padBottom(5).colspan(3);
        table.add(players2);
        table.row().colspan(3).padBottom(5);
        table.add(players3);
        table.row().colspan(3).padBottom(5);
        table.add(players4);
        table.row().colspan(3).padBottom(5);
        table.row().colspan(3);
        //table.add(startGameBtn).width(400).height(150);

		/*table.row();
		table.add(resultsBtn).colspan(5);*/

        return table;
    }

    @Override public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width,height, true);
    }

    @Override public void dispose() {
        super.dispose();
    }
}
