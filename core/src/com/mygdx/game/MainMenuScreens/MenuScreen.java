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

public class MenuScreen extends ScreenAdapter {
	private Stage stage;
	private BombermanGame game;
	private Viewport viewport;
	private Camera camera;
	private AssetManager assetManager;
	private Skin skin;
	
	public MenuScreen(BombermanGame game) {
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
		
		TextButton startMultyBtn = new TextButton("Multiplayer", skin);
		startMultyBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new PlayerScreen(game, false));
			}
		});

		TextButton startSingleBtn = new TextButton("Singleplayer", skin);
		startSingleBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new PlayerScreen(game, true));
			}
		});
		
		//TextButton settingsBtn = new TextButton("Settings", skin);
		/*settingsBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new SettingsScreen(game));
			}
		});*/
		TextButton exitBtn = new TextButton("Exit", skin);
		TextButton resultsBtn = new TextButton("Results", skin);
		resultsBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new ResultsScreen(game));
			}
		});
		exitBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.safeExit();
			}
		});

		table.add();
		table.add();
		table.row();
		table.add().height(100);

		table.row().padBottom(5).colspan(3);
		table.add(startSingleBtn).width(350).height(125);

		table.row().colspan(3).padBottom(5);
		table.add(startMultyBtn).width(350).height(125);

		table.row().colspan(3).padBottom(5);
		table.add(resultsBtn).width(350).height(125);

		table.row().colspan(3).padBottom(5);
		table.add(exitBtn);

		
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
