package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.GameManager.GameManager;
import com.mygdx.game.MainMenuScreens.MenuScreen;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.util.GdxUtils;

public class BombermanGame extends Game {
	AssetManager assetManager;
	public SpriteBatch batch;
	public BitmapFont font;

	public AssetManager getAssetManager() {
		return assetManager;
	}

	@Override
	public void create() {
		batch=new SpriteBatch();
		font = GdxUtils.getTTFFontInWorldUnits(1f, 15, 2);
		assetManager = new AssetManager();
		GameManager.INSTANCE.loadResults();
		Gdx.app.setLogLevel(Logger.DEBUG);
		assetManager.load(AssetDescriptors.ATLAS);
		assetManager.load(AssetDescriptors.GAME_UI);
		assetManager.finishLoading();
		selectFirstScreen();
	}

	public void selectFirstScreen() {
		//setScreen(new MainScreen(this, 15, 15, 10));
		setScreen(new MenuScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		screen.resize(width, height);
	}

	public void safeExit() {
		Gdx.app.exit();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		screen.dispose();
	}
}

