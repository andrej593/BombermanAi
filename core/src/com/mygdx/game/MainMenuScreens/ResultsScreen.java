package com.mygdx.game.MainMenuScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.GameManager.GameManager;
import com.mygdx.game.GameManager.Results;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.util.GameConfig;
import com.mygdx.game.util.GdxUtils;

public class ResultsScreen extends ScreenAdapter {
	private Stage stage;
	private BombermanGame game;
	private Viewport viewport;
	private Camera camera;
	private AssetManager assetManager;
	private Skin skin;
	private Results results;
	private Json json;
	
	public ResultsScreen(BombermanGame game) {
		this.game = game;
		stage = new Stage();
		assetManager = game.getAssetManager();
		skin = assetManager.get(AssetDescriptors.GAME_UI);
		results = GameManager.INSTANCE.results;
		json = new Json();
		System.out.println(json.prettyPrint(results));
	}
	
	@Override public void show() {
		camera = new OrthographicCamera(GameConfig.GRAPHICS_WIDTH, GameConfig.GRAPHICS_HEIGHT);
		viewport = new ScreenViewport(camera);
		
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);
		stage.addActor(createUI()); //ADD MAIN TABLE
	}
	
	private Table createUI() {
		Table table = new Table();
		table.setFillParent(true);
		
		List resultsList = new List(skin);
		resultsList.setItems(results.results);
		ScrollPane scrollPane = new ScrollPane(resultsList);
		//scrollPane.setScrollBarPositions(false, true);
		table.add(scrollPane);
		table.row();
		
		TextButton backTxtBtn = new TextButton("Back", skin);
		backTxtBtn.addListener(new ClickListener() {
			@Override public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MenuScreen(game));
			}
		});
		table.add(backTxtBtn).padTop(10);
		
		return table;
	}
	
	@Override public void render(float delta) {
		GdxUtils.clearScreen(Color.TEAL);
		stage.act();
		stage.draw();
		if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE))
			game.setScreen(new MenuScreen(game));
	}
	
	@Override public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width,height, true);
	}
	
	@Override public void dispose() {
		stage.dispose();
	}
}
