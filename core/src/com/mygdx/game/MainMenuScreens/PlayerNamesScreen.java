package com.mygdx.game.MainMenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.MainGame.MainScreen;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.util.GameConfig;
import com.mygdx.game.util.GdxUtils;

public class PlayerNamesScreen extends ScreenAdapter {
    protected Stage stage;
    protected BombermanGame game;
    public Viewport viewport;
    public Camera camera;
    public AssetManager assetManager;
    private Skin skin;

    public static String player1Name = "player1";
    public static String player2Name = "player2";
    public static String player3Name = "player3";
    public static String player4Name = "player4";

    int nump;
    public PlayerNamesScreen(BombermanGame game, int numP) {
        this.game = game;
        this.nump=numP;
        assetManager = game.getAssetManager();
        skin = assetManager.get(AssetDescriptors.GAME_UI);
    }

    @Override public void render(float delta) {
        GdxUtils.clearScreen(Color.TEAL);
        stage.act();
        stage.draw();
    }

    @Override public void show() {
        camera = new OrthographicCamera(GameConfig.GRAPHICS_WIDTH, GameConfig.GRAPHICS_HEIGHT);
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        stage.addActor(createUI()); //ADD MAIN TABLE
    }

    public Actor createUI() {
        Table table = new Table();
        table.setFillParent(true);


        Label player1Label = new Label("Player 1 name: ", skin);
        final TextField player1Text = new TextField("player1", skin);
        Label player2Label = new Label("Player 2 name: ", skin);
        final TextField player2Text = new TextField("player2", skin);
        Label player3Label = new Label("Player 3 name: ", skin);
        final TextField player3Text = new TextField("player3", skin);
        Label player4Label = new Label("Player 4 name: ", skin);
        final TextField player4Text = new TextField("player4", skin);

        TextButton backBtn = new TextButton("Play", skin);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                player1Name = player1Text.getText();
                player2Name = player2Text.getText();
                if(nump>=3) {
                    player3Name = player3Text.getText();
                }
                if(nump>=4) {
                    player4Name = player4Text.getText();
                }
                game.setScreen(new MainScreen(game, GameConfig.WIDTH, GameConfig.HEIGHT, GameConfig.POWERUPS, nump,AiDifficultyScreen.NONE));
            }
        });

        table.add(player1Label);
        table.add(player1Text).width(200);
        table.row();
        table.add(player2Label);
        table.add(player2Text).width(200);
        table.row();

        if(nump>=3) {
            table.add(player3Label);
            table.add(player3Text).width(200);
            table.row();
        }
        if(nump>=4) {
            table.add(player4Label);
            table.add(player4Text).width(200);
            table.row();
        }
        table.add(backBtn).colspan(2).padTop(10);

        return table;
    }

    @Override public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width,height, true);
    }

    @Override public void dispose() {
        stage.dispose();
    }
}