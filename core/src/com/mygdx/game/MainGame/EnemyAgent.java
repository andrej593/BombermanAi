package com.mygdx.game.MainGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.pathfinding.GameFieldGraph;


public class EnemyAgent extends Character {
    public static final int UPDATE = 15;    //čas posodobitve poti
    int updateCounter;


    GameFieldGraph gameFieldGraphWalls; //graf ki vsebuje varna polja in navadne zidove
    GameFieldGraph gameFieldGraphEmpty; //graf ki vsebuje polja ki so trenutno prehodna
    public GraphPath<Tile> safePath;    //graphEmpty
    public GraphPath<Tile> playerPath;  //graphWalls

    public StateMachine<EnemyAgent, EnemyState> stateMachine;

    boolean isSafe; //če je varen
    Character target;   //igralec ki je trenutna tarča

    Array<Bomb> bombs;
    Array<Character> players;


    public EnemyAgent(int x, int y, TextureAtlas atlas, String u, Color c, GameField gf, Array<Character> players, Character target, Array<Bomb> bombs
            , GameFieldGraph gameFieldGraphWalls, GameFieldGraph gameFieldGraphEmpty) {
        super(x, y, atlas, u, c, gf);
        this.gameFieldGraphWalls = gameFieldGraphWalls;
        this.gameFieldGraphEmpty = gameFieldGraphEmpty;
        this.isSafe = true;
        this.target = target;
        this.bombs = bombs;
        this.stateMachine = new DefaultStateMachine<>(this, EnemyState.NONE);
        this.updateCounter = 0;
        this.players = players;
    }

    public void update(float delta, Array<Bomb> bombs, boolean inputData[]) {
        checkIfSafe();  //pogledam če je character varen
        stateMachine.update();
    }


    public void checkIfSafe() {
        isSafe = true;
        for (Tile t : gameField.tiles) {
            //če stoji na polju ki bo exsplodiralo
            if (this.bounds.overlaps(t.bounds)) {
                if (t.getWillExplode()) {
                    isSafe = false;
                    break;
                }
            }
        }
    }

    //premikanje proti playerju
    public void moveToPlayer() {
        //poti ne posodabljam vsaki frame
        if (updateCounter == UPDATE) {
            findPlayerPath();
            updateCounter = 0;
        }
        updateCounter++;

        float delta = Gdx.graphics.getDeltaTime();
        boolean mUp = false;
        boolean mDown = false;
        boolean mLeft = false;
        boolean mRight = false;
        boolean placeBomb = false;

        // ugotovim kam se hoče character pemaknit, če hoče nastaviti bombo
        for (int i = 0; i < playerPath.getCount(); i++) {
            //na katerem tileu je character
            if (this.bounds.overlaps(playerPath.get(i).bounds)) {
                //če je od tarče oddaljen več kot 2 tilea
                if (i + 2 < playerPath.getCount()) {
                    //če je nasledni tile zid pogledam če je varno nastaviti bombo in jo nastavi
                    if (playerPath.get(i + 1).isTile(GameField.FIELD_WALL) && checkIfSafeToPlaceBomb()) {
                        placeBomb = true;
                    } else {
                        //premik na naslednji tile v poti
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
                    //če je od tarče odaljen 1 kvadratek in je varno nastavit bombo jo nastavi
                } else if (i + 1 < playerPath.getCount()) {
                    if (checkIfSafeToPlaceBomb()) {
                        placeBomb = true;
                    }
                } else {
                    //če je v tarči gre vstran od nje(imel sem problem,
                    // če sta bila dva ai playerja sta prišla drug v drugega, nastavila bombo,
                    // našla enako pot na varnost se premaknila tja in to ponavljala v neskončnost) zato se zdaj če se že znajdeta v istem tileu najprej
                    //ločita in nato nastavljata bombe

                    if (runner) {//da imata različno vedenje ko se srečata - en gre levo dol, drugi desno gor
                        if (playerPath.get(i).right(gameField).isSafeTile())
                            mRight = true;
                        if (playerPath.get(i).up(gameField).isSafeTile())
                            mUp = true;
                    } else {
                        if (playerPath.get(i).left(gameField).isSafeTile())
                            mLeft = true;
                        if (playerPath.get(i).down(gameField).isSafeTile())
                            mDown = true;
                    }
                }
                break;
            }
        }

        //izvedem premikanje, nastavljanje bombe
        super.update(delta, bombs, new boolean[]{mLeft, mRight, mUp, mDown, placeBomb});
    }

    //premikanje na varnost
    public void moveToSafety() {
        if (updateCounter == UPDATE) {
            findSafePath();
            updateCounter = 0;
        }
        updateCounter++;
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
                runner = target.runner ? false : true;
            }
        }
    }

    public float calculateDistance(Character a, Character b) {
        return Math.abs(a.bounds.x - b.bounds.x) + Math.abs(a.bounds.y - b.bounds.y);
    }

    //poiščem pot do najbližjega igralca
    public void findPlayerPath() {
        this.setTarget();
        playerPath = gameFieldGraphWalls.findPath(this.closestTile(gameFieldGraphWalls.tiles), target.closestTile(gameFieldGraphWalls.tiles));
    }

    //pogeldam če je varno nastaviti bombo - not perfect
    public boolean checkIfSafeToPlaceBomb() {
        //poiščem vsa polja, ki bi jih bomba poškodovala če bi jo nastavil enako kot pri nastavljanju bombe, le da ne nastavim willExplode in bombe še dejansko ne nastavi
        Tile tmp = this.closestTile(gameField.tiles);
        Array<Tile> tileWouldExplode = new Array<>();

        //UP
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.up(gameField);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //DOWN
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.down(gameField);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //RIGHT
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.right(gameField);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //LEFT
        for (int i = 1; i < power + 1; i++) {
            tmp = tmp.left(gameField);
            if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                tileWouldExplode.add(tmp);
                if (tmp.isTile(GameField.FIELD_WALL))
                    break;
            } else
                break;
        }
        //posodobim graf
        gameFieldGraphEmpty.findEmptyNodes();
        return checkIfStillSafe(tileWouldExplode);
    }


    public boolean checkIfStillSafe(Array<Tile> tileWouldExplode) {
        //poiščem najkrajšo pot na varno,
        //enako kot iskanje varne poti, pri čemer izključim polja,
        //ki bojo eksplodirala če nastavim bombo, iz končne destinacije
        Tile startTile = this.closestTile(gameFieldGraphEmpty.tiles);
        GraphPath<Tile> tmpPath;
        GraphPath<Tile> tmp;
        int i = 0;
        while (i < gameFieldGraphEmpty.tiles.size &&
                gameFieldGraphEmpty.tiles.get(i).willExplode ||
                tileWouldExplode.contains(gameFieldGraphEmpty.tiles.get(i), true)) {
            i++;
        }
        if (i < gameFieldGraphEmpty.tiles.size) {
            tmpPath = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
            i++;
            while (i < gameFieldGraphEmpty.tiles.size && tmpPath.getCount() == 0) {
                if (!gameFieldGraphEmpty.tiles.get(i).willExplode &&
                        !tileWouldExplode.contains(gameFieldGraphEmpty.tiles.get(i), true) &&
                        gameFieldGraphEmpty.tiles.get(i) != startTile)
                    tmpPath = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                i++;
            }
            for (; i < gameFieldGraphEmpty.tiles.size; i++) {
                if (!gameFieldGraphEmpty.tiles.get(i).willExplode &&
                        !tileWouldExplode.contains(gameFieldGraphEmpty.tiles.get(i), true) &&
                        gameFieldGraphEmpty.tiles.get(i) != startTile) {
                    tmp = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                    if (tmp.getCount() != 0 && tmp.getCount() < tmpPath.getCount()) {
                        tmpPath = tmp;
                    }
                }
            }
        } else {
            tmpPath = new DefaultGraphPath<Tile>();
        }

        //če je dobljena pot prazna vrnem false - ni varno nastaviti bombe
        //NOTE preverim še če je prvo polje v poti varno, deluje malo bolše
        // , ni prefektno, včasih nastavi bombo, in se premakne na polja ki eksplodirajo kasneje
        // nimam vključeno da bi vedel koliko časa bo neko polje še varno pred eksplozijo in koliko časa bo character potreboval da pride čez tisto polje
        if (tmpPath.getCount() > 1 && tmpPath.get(1).isSafeTile()) {
            if (tmpPath.getCount() > 2 && tmpPath.get(2).isSafeTile()) {
                return true;
            }
            return true;
        } else
            return false;
    }

    public void findSafePath() {
        //poiščem najkrajšo pot na varno
        Tile startTile = this.closestTile(gameFieldGraphEmpty.tiles);
        GraphPath<Tile> tmp;
        int i = 0;
        //iščem varno polje
        while (i < gameFieldGraphEmpty.tiles.size && gameFieldGraphEmpty.tiles.get(i).willExplode) {
            i++;
        }
        //če sem v vseh možnih poljim našel varno polje
        if (i < gameFieldGraphEmpty.tiles.size) {
            //izračunam pot do tega polja
            safePath = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
            i++;
            //iščem pot ki obstaja(dolžina ni enaka 0)
            while (i < gameFieldGraphEmpty.tiles.size && safePath.getCount() == 0) {
                if (!gameFieldGraphEmpty.tiles.get(i).willExplode)
                    safePath = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                i++;
            }
            //preverim če obstaja krajša pot, jo zamenjam
            for (; i < gameFieldGraphEmpty.tiles.size; i++) {
                if (!gameFieldGraphEmpty.tiles.get(i).willExplode) {
                    tmp = gameFieldGraphEmpty.findPath(startTile, gameFieldGraphEmpty.tiles.get(i));
                    if (tmp.getCount() != 0 && tmp.getCount() < safePath.getCount()) {
                        safePath = tmp;
                    }
                }
            }
        } else {
            //prazna pot dolžien nič
            safePath = new DefaultGraphPath<Tile>();
        }
    }

    //izris trenutne poti ki ji sledi
    public void renderPath(ShapeRenderer sr) {
        if (stateMachine.getCurrentState() == EnemyState.ISSAFE) {
            sr.setColor(Color.PURPLE);
            for (Tile t : playerPath) {
                sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
            }
        } else if (stateMachine.getCurrentState() == EnemyState.INDANGER) {
            sr.setColor(Color.PURPLE);
            for (Tile t : safePath) {
                sr.rect(t.bounds.x, t.bounds.y, t.bounds.width, t.bounds.height);
            }
        }
    }
}
