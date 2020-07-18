package com.mygdx.game.MainGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.BombermanGame;
import com.mygdx.game.GameManager.GameManager;
import com.mygdx.game.MainMenuScreens.MenuScreen;
import com.mygdx.game.MainMenuScreens.SettingsScreen;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.debug.DebugCameraController;
import com.mygdx.game.hud.HUD;
import com.mygdx.game.pathfinding.GameFieldGraph;
import com.mygdx.game.util.GdxUtils;

public class MainScreen extends ScreenAdapter {
    public static final int HUD_H = 2;
    public static final Logger log = new Logger(MainScreen.class.getName(), Logger.DEBUG);
    BombermanGame game;

    //grafa za pathfiding
    GameFieldGraph gameFieldGraphWalls;
    GameFieldGraph gameFieldGraphEmpty;

    GameField gameField;    //igralno polje
    Array<Character> players;   //igarlci
    Array<Bomb> bombs;  //bombe

    public SpriteBatch batch;
    public OrthographicCamera cameraFont;
    public Viewport viewportFont;

    DebugCameraController dcc;
    ShapeRenderer sr;
    AssetManager am;

    public OrthographicCamera boardCam;
    public Viewport boardV;
    BitmapFont font;

    int width;
    int height;

    boolean debug = true;
    public boolean paused = false;
    public int gameState;
    public Character winner;

    TextureRegion empty;
    TextureRegion wall;
    TextureRegion wallS;
    TextureRegion explosionM;
    TextureRegion explosionV;
    TextureRegion explosionH;
    TextureRegion power;
    TextureRegion bomb;
    TextureRegion bombN;
    TextureRegion speedup;

    HUD hud;

    public MainScreen(BombermanGame game, int width, int height, int powerups, int numP) {
        this.game=game;
        this.width=width;
        this.height=height;

        am = game.getAssetManager();
        TextureAtlas atlas = am.get(AssetDescriptors.ATLAS);
        empty=atlas.findRegion(RegionNames.EMPTY);
        power=atlas.findRegion(RegionNames.POWER);
        bomb=atlas.findRegion(RegionNames.BOMB);
        wallS=atlas.findRegion(RegionNames.WALLS);
        wall=atlas.findRegion(RegionNames.WALL);
        speedup=atlas.findRegion(RegionNames.SPEEDUP);
        bombN=atlas.findRegion(RegionNames.BOMBN);
        explosionM = atlas.findRegion(RegionNames.EXPLOSIONM);
        explosionV = atlas.findRegion(RegionNames.EXPLOSIONV);
        explosionH = atlas.findRegion(RegionNames.EXPLOSIONH);

        gameField=new GameField(width,height,powerups);
        bombs=new Array<Bomb>();

        this.gameFieldGraphWalls = new GameFieldGraph(gameField, width, GameFieldGraph.WALL_PATH);
        this.gameFieldGraphEmpty = new GameFieldGraph(gameField, width, GameFieldGraph.EMPTY_PATH);

        players = new Array<Character>();
        players.add(new Player(0, 1, height-2, atlas, SettingsScreen.player1Name, Color.WHITE, gameField));
        players.add(new EnemyAgent(width-2, height-2, atlas, SettingsScreen.player2Name, Color.RED, gameField, players, players.get(0), bombs, gameFieldGraphWalls, gameFieldGraphEmpty));
        if(numP == 3){
            players.add(new EnemyAgent(width-2, 1, atlas, SettingsScreen.player3Name, Color.BLUE, gameField, players, players.get(0), bombs, gameFieldGraphWalls, gameFieldGraphEmpty));
            //players.add(new Player(2, width-2, 1, atlas, SettingsScreen.player3Name, gameField));
        }
        if(numP == 4){
            players.add(new EnemyAgent(width-2, 1, atlas, SettingsScreen.player3Name, Color.BLUE, gameField, players, players.get(0), bombs, gameFieldGraphWalls, gameFieldGraphEmpty));
            players.add(new EnemyAgent(1, 1, atlas, SettingsScreen.player4Name, Color.GREEN, gameField, players, players.get(0), bombs, gameFieldGraphWalls, gameFieldGraphEmpty));
            //players.add(new Player(2, width-2, 1, atlas, SettingsScreen.player3Name, gameField));
            //players.add(new Player(3, 1, 1, atlas, SettingsScreen.player4Name, gameField));
        }

        cameraFont = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewportFont = new ScreenViewport(cameraFont);
        batch = game.batch;
        sr = new ShapeRenderer();
        boardCam = new OrthographicCamera(width, height +HUD_H);
        boardV = new FitViewport(width, height + HUD_H, boardCam);
        boardV.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        font = GdxUtils.getTTFFontInWorldUnits(2f, boardV.getWorldHeight(), 2);
        hud = new HUD(0, (float) (height + HUD_H), (float) width, HUD_H, this);
        hud.start();
        dcc = new DebugCameraController();
        dcc.setStartPosition(width / 2+0.5f, (height+HUD_H) / 2+0.5f);
        gameState =0;
    }

    @Override
    public void show() {
        super.show();
    }

    public void inputHandle() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
            game.setScreen(new MenuScreen(game));

        if (Gdx.input.isKeyJustPressed(Input.Keys.P))
            paused = paused ? false : true;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F5))
            debug = debug ? false : true;
    }

    @Override
    public void render(float delta) {
        dcc.handleDebugInput(delta);
        dcc.applyTo(boardCam);
        boardCam.update();

        inputHandle();

        int dead=0;
        if(!paused) {
            if (gameState == 0) {

                //updatam bombe, če je življenska doba potekla jih odstranim
                Bomb b;
                for(int i=0;i<bombs.size;i++){
                    b = bombs.get(i);
                    b.update(delta, gameField, bombs);
                    if (b.time >= Bomb.LIFE_TIME) {
                        b.destroy(gameField);
                        bombs.removeIndex(i);
                    }
                }

                //updatam tile
                for (Tile t : gameField.tiles)
                    t.update(players);

                //updatam igralce
                for (Character p : players) {
                    if(p.health>0)
                        p.update(delta, bombs, new boolean[5]);
                    else
                        dead++;
                }
                //updatam grafa
                gameFieldGraphEmpty.update();
                gameFieldGraphWalls.update();
            }
        }
        //če je še samo en igralec
        if(dead>=players.size-1){
            gameState = 1;
            for (Character p : players) {
                if (p.health > 0) {
                    winner=p;
                    GameManager.INSTANCE.addResult(p.username, HUD.duration);
                }
            }
        }

        GdxUtils.clearScreen(Color.LIGHT_GRAY);
        batch.begin();
        {
            //izrišem hud
            hud.render(batch, delta);
            //narišem igralno polje
            drawField();
            //izrišem igralce in imena
            for(Character p :players){
                batch.setProjectionMatrix(boardCam.combined);
                p.render(batch);
                batch.setProjectionMatrix(cameraFont.combined);
                Vector3 v = new Vector3();
                v.set(p.bounds.x, p.bounds.y+1.5f, 0);
                GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(boardCam, v);
                font.setColor(Color.WHITE);
                GlyphLayout layout = new GlyphLayout(font,  p.username);
                font.draw(batch, layout, v.x, v.y);

            }
        }
        batch.end();

        if(debug) {
            sr.setProjectionMatrix(boardCam.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            //vsi tilei
            sr.setColor(Color.BLUE);
            for (Tile t : gameField.tiles) {
                    sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
            }
            //player hitbox + ai paths
            sr.setColor(Color.BLACK);
            for (Character p : players) {
                sr.rect(p.bounds.x, p.bounds.y, p.bounds.width, p.bounds.height);
                p.renderPath(sr);
            }
            //bomb tile
            sr.setColor(Color.BLACK);
            for (Bomb b : bombs) {
                sr.rect(b.bounds.x, b.bounds.y, b.bounds.width, b.bounds.height);
            }
            //nevarni tilei
            sr.setColor(Color.RED);
            for(Tile t : gameField.tiles){
                if(t.getWillExplode()){
                    sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
                }
            }
            sr.end();
        }
    }

    public void drawField(){
        batch.setProjectionMatrix(boardCam.combined);
        for(Tile t: gameField.tiles){
                switch (t.value) {
                    case GameField.FIELD_EMPTY: {
                        batch.draw(empty,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.FIELD_WALL:
                    case GameField.FIELD_EMPTY | GameField.FIELD_WALL | GameField.POWERUP_SPEED:
                    case GameField.FIELD_EMPTY | GameField.FIELD_WALL | GameField.POWERUP_POWER:
                    case GameField.FIELD_EMPTY | GameField.FIELD_WALL | GameField.POWERUP_BOMBS: {
                        batch.draw(wall,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.BOMB: {
                        batch.draw(bombN,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.POWERUP_POWER:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.POWERUP_SPEED:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.POWERUP_BOMBS:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.EXPLOSIONV | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.EXPLOSIONH | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.EXPLOSIONV:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM | GameField.EXPLOSIONH:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONM:{
                        batch.draw(explosionM,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV | GameField.POWERUP_BOMBS:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV | GameField.POWERUP_SPEED:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV | GameField.POWERUP_POWER:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV |GameField.EXPLOSIONH | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV |GameField.EXPLOSIONH:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONV:{
                        batch.draw(explosionV,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONH | GameField.POWERUP_BOMBS:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONH | GameField.POWERUP_SPEED:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONH | GameField.POWERUP_POWER:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONH | GameField.BOMB:
                    case GameField.FIELD_EMPTY | GameField.EXPLOSIONH:{
                        batch.draw(explosionH,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_UNBRAKABLE_WALL: {
                        batch.draw(wallS,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.POWERUP_POWER: {
                        batch.draw(power,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.POWERUP_BOMBS: {
                        batch.draw(bomb,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                    case GameField.FIELD_EMPTY | GameField.POWERUP_SPEED: {
                        batch.draw(speedup,t.pos.x,t.pos.y,1,1);
                        continue;
                    }
                }
        }
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        sr.dispose();
        hud.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewportFont.update(width, height, true);
        boardV.update(width, height, true);
        hud.resize(width, height);
        font = GdxUtils.getTTFFontInWorldUnits(0.5f, boardV.getWorldHeight(), 1);
    }

}
