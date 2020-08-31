package com.mygdx.game.Ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MainGame.Bomb;
import com.mygdx.game.MainGame.Character;
import com.mygdx.game.MainGame.GameField;
import com.mygdx.game.MainGame.MainScreen;
import com.mygdx.game.MainGame.Tile;
import com.mygdx.game.Ai.Pathfinding.GameFieldGraph;

import java.util.Comparator;
import java.util.Random;

public class EnemyAgent extends Character {
    int updateCounter;

    GameFieldGraph gameFieldGraphWalls; //graf ki vsebuje varna polja in navadne zidove
    GameFieldGraph gameFieldGraphEmpty; //graf ki vsebuje polja ki so trenutno prehodna

    public int difficulty;
    public GraphPath<Tile> safePath;    //graphEmpty
    public GraphPath<Tile> playerPath;  //graphWalls

    public StateMachine<EnemyAgent, EnemyState> stateMachine;
    public StateMachine<EnemyAgent, EnemySubState> subStateMachine;

    EnemySubState subState;
    boolean isSafe; //če je varen
    public Character target;   //igralec ki je trenutna tarča

    Array<Bomb> bombs;
    Array<Character> players;
    int updateInterval;

    public EnemyAgent(int x, int y, TextureAtlas atlas, String u, Color c, GameField gf, Array<Character> players, Character target, Array<Bomb> bombs
            , GameFieldGraph gameFieldGraphWalls, GameFieldGraph gameFieldGraphEmpty, int difficulty) {
        super(x, y, atlas, u, c, gf);
        this.gameFieldGraphWalls = gameFieldGraphWalls;
        this.gameFieldGraphEmpty = gameFieldGraphEmpty;
        this.isSafe = true;
        this.target = target;
        this.bombs = bombs;
        this.stateMachine = new DefaultStateMachine<>(this, EnemyState.ISSAFE);
        this.updateCounter = 0;
        this.players = players;
        this.subStateMachine = new DefaultStateMachine<>(this, EnemySubState.NONE);
        this.difficulty=difficulty;
        findPlayerPath();
        findSafePath();
        if(difficulty == 0){
            updateInterval =0;
        }else if(difficulty == 1){
            updateInterval =10;
        }else if(difficulty == 2){
            updateInterval =20;
        }
    }

    public void update(float delta, Array<Bomb> bombs, boolean inputData[]) {
        checkIfSafe();  //pogledam če je character varen
        stateMachine.update();
        subStateMachine.update();
        //MainScreen.log.debug("Player:" + this.username + ", " + subStateMachine.getCurrentState() + ", " + subState);
    }


    public void checkIfSafe() {
        isSafe = true;
        for (Tile t : gameField.tiles) {
            //če stoji na polju ki bo exsplodiralo
            if (this.bounds.overlaps(t.bounds)) {
                if (t.getWillExplode()) {
                    isSafe = false;
                    subState = EnemySubState.NONE;
                    break;
                }
            }
        }
    }

    public void moveCloser() {
        float delta = Gdx.graphics.getDeltaTime();
        boolean mUp = false;
        boolean mDown = false;
        boolean mLeft = false;
        boolean mRight = false;
        boolean placeBomb = false;

        for (int i = 0; i < playerPath.getCount(); i++) {
            if (this.bounds.overlaps(playerPath.get(i).bounds)) {
                if (playerPath.get(i + 1).isTile(GameField.FIELD_WALL) && checkIfSafeToPlaceBomb()) {
                    placeBomb = true;
                } else {
                    if (playerPath.get(i + 1).pos.x > this.bounds.x) {
                        mRight = true;
                    } else if (playerPath.get(i + 1).pos.x + 0.1f < this.bounds.x) {
                        mLeft = true;
                    }
                    if (playerPath.get(i + 1).pos.y > this.bounds.y) {
                        mUp = true;
                    } else if (playerPath.get(i + 1).pos.y + 0.1f < this.bounds.y) {
                        mDown = true;
                    }
                }
                break;
            }
        }
        super.update(delta, bombs, new boolean[]{mLeft, mRight, mUp, mDown, placeBomb});
    }

    public void placeBomb() {
        if (checkIfSafeToPlaceBomb())
            super.update(Gdx.graphics.getDeltaTime(), bombs, new boolean[]{false, false, false, false, true});
    }

    public void moveAway() {
        float delta = Gdx.graphics.getDeltaTime();
        boolean mUp = false;
        boolean mDown = false;
        boolean mLeft = false;
        boolean mRight = false;
        boolean placeBomb = false;

        for (int i = 0; i < playerPath.getCount(); i++) {
            if (this.bounds.overlaps(playerPath.get(i).bounds)) {
                if (runner) {
                    if (playerPath.get(i).right(gameField, 1).isSafeTile())
                        mRight = true;
                    if (playerPath.get(i).up(gameField, 1).isSafeTile())
                        mUp = true;
                } else {
                    if (playerPath.get(i).left(gameField, 1).isSafeTile())
                        mLeft = true;
                    if (playerPath.get(i).down(gameField, 1).isSafeTile())
                        mDown = true;
                }
                break;
            }
        }
        super.update(delta, bombs, new boolean[]{mLeft, mRight, mUp, mDown, placeBomb});
    }

    //premikanje proti playerju
    public void determineSubState() {
        //poti ne posodabljam vsaki frame
        if (updateCounter == updateInterval) {
            findPlayerPath();
            updateCounter = 0;
        } else {
            updateCounter++;
        }

        // ugotovim kam se hoče character pemaknit, če hoče nastaviti bombo
        for (int i = 0; i < playerPath.getCount(); i++) {
            //na katerem tileu je character
            if (this.bounds.overlaps(playerPath.get(i).bounds)) {
                //če je od tarče oddaljen več kot 2 tilea
                if (i + 2 < playerPath.getCount()) {
                    subState = EnemySubState.OUTOFRANGE;
                } else if (i + 1 < playerPath.getCount()) {
                    subState = EnemySubState.INRANGE;
                } else {
                    subState = EnemySubState.OVERLAPING;
                }
                break;
            }
        }
    }

    //premikanje na varnost
    public void moveToSafety() {
        if (updateCounter == updateInterval) {
            findSafePath();
            updateCounter = 0;
        } else {
            updateCounter++;
        }
        float delta = Gdx.graphics.getDeltaTime();
        boolean mUp = false;
        boolean mDown = false;
        boolean mLeft = false;
        boolean mRight = false;
        boolean placeBomb = false;

        //enako kot premikanje proti playerju, le da tu želim doseči cilj, na cilju se ustavim
        for (int i = 0; i < safePath.getCount(); i++) {
            if (this.bounds.overlaps(safePath.get(i).bounds)) {
                if (i + 1 < safePath.getCount()) {  //premik na naslednji tile
                    if (safePath.get(i + 1).pos.x > this.bounds.x) {
                        mRight = true;
                    } else if (safePath.get(i + 1).pos.x + 0.1f < this.bounds.x) {
                        mLeft = true;
                    }
                    if (safePath.get(i + 1).pos.y > this.bounds.y) {
                        mUp = true;
                    } else if (safePath.get(i + 1).pos.y + 0.1f < this.bounds.y) {
                        mDown = true;
                    }
                } else {    //premik na ciljni tile
                    if (safePath.get(i).pos.x > this.bounds.x) {
                        mRight = true;
                    } else if (safePath.get(i).pos.x + 0.1f < this.bounds.x) {
                        mLeft = true;
                    }
                    if (safePath.get(i).pos.y > this.bounds.y) {
                        mUp = true;
                    } else if (safePath.get(i).pos.y + 0.1f < this.bounds.y) {
                        mDown = true;
                    }
                }
            }
            break;
        }
        //izvedba premika
        super.update(delta, bombs, new boolean[]{mLeft, mRight, mUp, mDown, placeBomb});
    }

    //poiščem najbližjega igralca, ki še je živ
    public void setTarget() {
        for (int i = 0; i < players.size; i++) {
            Character p = players.get(i);
            if (p.health > 0 && p != this) {
                if (target.health <= 0) {
                    target = p;
                } else if (calculateDistance(this, p) < calculateDistance(this, target))
                    target = p;
                runner = !target.runner;
            }
        }
    }

    public float calculateDistance(Character a, Character b) {
        return Math.abs(a.bounds.x - b.bounds.x) + Math.abs(a.bounds.y - b.bounds.y);
    }

    //poiščem pot do najbližjega igralca
    public void findPlayerPath() {
        this.setTarget();
        Random random=new Random();
        int max=difficulty+1;
        int x = random.nextInt(max);
        int y = random.nextInt(max);
        boolean up= random.nextBoolean();
        boolean left= random.nextBoolean();
        Tile tmp= target.closestTile(gameFieldGraphWalls.tiles);

        if(up) {
            if ((tmp.pos.y + y) < gameField.height)
                tmp = tmp.up(gameField, y);
        }else {
            if ((tmp.pos.y - y) > 0)
                tmp = tmp.down(gameField, y);
        }
        if(left) {
            if ((tmp.pos.x - x) > 0)
                tmp = tmp.left(gameField, x);
        } else {
            if ((tmp.pos.x + x) < gameField.width)
                tmp = tmp.right(gameField, x);
        }
        tmp=tmp.closestTile(gameFieldGraphWalls.tiles);
        //MainScreen.log.debug(x+", "+y+", "+difficulty);
        playerPath = gameFieldGraphWalls.findPath(this.closestTile(gameFieldGraphWalls.tiles), tmp);
    }

    //pogeldam če je varno nastaviti bombo - not perfect
    public boolean checkIfSafeToPlaceBomb() {
        //poiščem vsa polja, ki bi jih bomba poškodovala če bi jo nastavil enako kot pri nastavljanju bombe, le da ne nastavim willExplode in bombe še dejansko ne nastavi
        Tile tmp = this.closestTile(gameField.tiles);
        Array<Tile> tileWouldExplode = new Array<>();

        //napolnim tileWouldExplode
        //UP
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.up(gameField, 1);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //DOWN
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.down(gameField, 1);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //RIGHT
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.right(gameField, 1);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //LEFT
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.left(gameField, 1);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }

        //posodobim graf
        gameFieldGraphEmpty.findEmptyNodes();
        Tile startTile = this.closestTile(gameFieldGraphEmpty.tiles);
        //GraphPath<Tile> tmpPath;
        GraphPath<Tile> tmpPath;
        Array<GraphPath<Tile>> avilablePaths = new Array<>();
        for (int i = 0; i < gameFieldGraphEmpty.tiles.size; i++) {
            if (!gameFieldGraphEmpty.tiles.get(i).getWillExplode() && !tileWouldExplode.contains(gameFieldGraphEmpty.tiles.get(i), true)) {
                tmpPath = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                if (tmpPath.getCount() != 0) {
                    avilablePaths.add(tmpPath);
                }
            }
        }
        if (avilablePaths.size==0){
            return false;
        }else{
            return true;
        }
    }

    public void findSafePath() {
        //poiščem najkrajšo pot na varno
        Tile startTile = this.closestTile(gameFieldGraphEmpty.tiles);
        GraphPath<Tile> tmp;
        Array<GraphPath<Tile>> avilablePaths = new Array<>();

        for (int i = 0; i < gameFieldGraphEmpty.tiles.size; i++) {
            if (!gameFieldGraphEmpty.tiles.get(i).getWillExplode()) {
                tmp = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                if (tmp.getCount() != 0) {
                    avilablePaths.add(tmp);
                }
            }
        }
        //ni najdene poti
        Comparator<GraphPath<Tile>> comparator = new Comparator<GraphPath<Tile>>() {
            @Override
            public int compare(GraphPath<Tile> path1, GraphPath<Tile> path2) {
                return path1.getCount()-path2.getCount();
            }
        };

        avilablePaths.sort(comparator);

        if (avilablePaths.size==0){
            safePath = new DefaultGraphPath<Tile>();
        }else if(avilablePaths.size-1 < difficulty){
            safePath=avilablePaths.get(avilablePaths.size-1);
        }else{
            safePath = avilablePaths.get(difficulty);
        }
    }

    //izris trenutne poti ki ji sledi
    public void renderPath(ShapeRenderer sr) {
        sr.setAutoShapeType(true);
        sr.set(ShapeRenderer.ShapeType.Filled);
        if (stateMachine.getCurrentState() == EnemyState.ISSAFE) {
            sr.setColor(Color.BLUE);

            for (Tile t : playerPath) {
                //sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
                sr.rect(t.bounds.x+0.25f, t.bounds.y+0.25f, t.bounds.width/2, t.bounds.height/2);

            }
        } else if (stateMachine.getCurrentState() == EnemyState.INDANGER) {
            sr.setColor(Color.RED);
            for (Tile t : safePath) {
                //sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
                sr.rect(t.bounds.x+0.25f, t.bounds.y+0.25f, t.bounds.width/2, t.bounds.height/2);
            }
        }
        sr.set(ShapeRenderer.ShapeType.Line);
    }
}
