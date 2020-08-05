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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.MainGame.MainScreen;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.util.GameConfig;
import com.mygdx.game.util.GdxUtils;

public class AiDifficultyScreen extends ScreenAdapter {
    public static final int NONE = 0;
    public static final int EASY = 3;
    public static final int MEDIUM = 2;
    public static final int HARD = 1;

    private Stage stage;
    private BombermanGame game;
    private Viewport viewport;
    private Camera camera;
    private AssetManager assetManager;
    private Skin skin;

    int nump;

    public AiDifficultyScreen(BombermanGame game, int nump) {
        this.game = game;
        this.nump=nump;
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

        TextButton easyDiffBtn = new TextButton("Easy", skin);
        easyDiffBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game, GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.POWERUPS, nump, EASY));
            }
        });
        TextButton medDiffBtn = new TextButton("Medium", skin);
        medDiffBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game, GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.POWERUPS, nump, MEDIUM));
            }
        });
        TextButton hardDiffBtn = new TextButton("Hard", skin);
        hardDiffBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainScreen(game, GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.POWERUPS, nump, HARD));
            }
        });


        table.add();
        table.add();
        table.row();
        table.add().height(100);

        table.row().padBottom(5).colspan(3);
        table.add(easyDiffBtn).width(350).height(125);

        table.row().colspan(3).padBottom(5);
        table.add(medDiffBtn).width(350).height(125);

        table.row().colspan(3).padBottom(5);
        table.add(hardDiffBtn).width(350).height(125);


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
