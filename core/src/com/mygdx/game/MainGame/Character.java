package com.mygdx.game.MainGame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.assets.RegionNames;

public abstract class Character {
    public static final float SPEED = 5f;   //hitrost premikanja
    public static final int START_BOMBS = 2;    //število bomb ki jih lahko oseba nastavi na zacetku igre
    public static final int START_POWER = 1;    //Polmer območja ki ga bomba raztreli na zacetku igre(število tilov/kvadratkov)
    public static final float IMMUNE_TIME = 1f; //kako dogo igralec ne mora biti pošodovan po prejemu škode - drugač bi vsaki frame ko se dotikaš ognja izgubil življenje
    public static final int START_HEALTH = 100; //začetno življenje, bomba naredi 25 škode

    public Rectangle bounds;
    public Vector2 pos;
    public Vector2 speed;

    //za animacijo
    public float animationTime;
    public boolean isMoving;    //če je true se bo animacija prikazala drugač bo slika primiru

    public String username;

    public GameField gameField;

    public int health;
    public int power;  //moč bombe za igralca
    public int bombs_max;  //maksimalno število bomb ki jih lahko nastavi igralec na enkrat
    public int bombs_placed;   //trenutno število nastavljenih bomb
    public float imune_timer;  //koliko časa je minilo od zadnje škode
    public boolean runner;

    public Color oldColor;
    public Color color;

    TextureRegion skull;
    TextureRegion characterUp;
    TextureRegion characterDown;
    TextureRegion characterLeft;
    TextureRegion characterRight;
    TextureRegion currentCharacter;
    Animation<TextureRegion> upA;
    Animation<TextureRegion> downA;
    Animation<TextureRegion> leftA;
    Animation<TextureRegion> rightA;

    public Character(int x, int y, TextureAtlas atlas, String u, Color c, GameField gf) {
        this.imune_timer = IMMUNE_TIME;
        this.health = START_HEALTH;
        this.pos = new Vector2(x,y);
        this.speed = new Vector2(SPEED, SPEED);
        this.power = START_POWER;
        this.bombs_max = START_BOMBS;
        this.bombs_placed = 0;
        this.username = u;
        this.bounds = new Rectangle(x - 1 / 2, y - 1 / 2, 0.9f, 0.9f);
        this.skull = atlas.findRegion(RegionNames.SKULL);
        this.isMoving = false;
        oldColor = new Color();
        this.gameField = gf;
        this.runner = false;

        upA = new Animation(0.15f, atlas.findRegions(RegionNames.UP1), Animation.PlayMode.LOOP);
        downA = new Animation(0.15f, atlas.findRegions(RegionNames.DOWN1), Animation.PlayMode.LOOP);
        leftA = new Animation(0.15f, atlas.findRegions(RegionNames.LEFT1), Animation.PlayMode.LOOP);
        rightA = new Animation(0.15f, atlas.findRegions(RegionNames.RIGHT1), Animation.PlayMode.LOOP);
        characterUp = atlas.findRegions(RegionNames.UP1).toArray()[0];
        characterDown = atlas.findRegions(RegionNames.DOWN1).toArray()[0];
        characterRight = atlas.findRegions(RegionNames.RIGHT1).toArray()[0];
        characterLeft = atlas.findRegions(RegionNames.LEFT1).toArray()[0];
        currentCharacter = characterDown;
        color=c;
        animationTime = 0;
    }

    public void update(float delta, Array<Bomb> bombs, boolean[] inputData) {
        //posodobim čas za animacijo, imunity
        animationTime += delta;
        imune_timer += delta;
        isMoving = false;

        //premikanje levo
        if (inputData[0]) {
            //povečam vrednost x coordinate
            this.bounds.x -= this.speed.x * delta;
            for (Tile t : gameField.tiles) {
                //če nemoreš skozi tile se ne premakne
                if (!t.isPassable) {
                    if (t.bounds.overlaps(this.bounds)) {
                        this.bounds.x += this.speed.x * delta;
                        break;
                    }
                }
            }
            //za animacijo - da se izvaja
            isMoving = true;
            //katera animacija se bo izvajala
            currentCharacter = characterLeft;
        }
        //premikanje desno
        if (inputData[1]) {
            this.bounds.x += this.speed.x * delta;
            for (Tile t : gameField.tiles) {
                if (!t.isPassable) {
                    if (t.bounds.overlaps(this.bounds)) {
                        this.bounds.x -= this.speed.x * delta;
                        break;
                    }
                }
            }
            isMoving = true;
            currentCharacter = characterRight;
        }
        //premikanje gor
        if (inputData[2]) {
            this.bounds.y += this.speed.y * delta;
            for (Tile t : gameField.tiles) {
                if (!t.isPassable) {
                    if (t.bounds.overlaps(this.bounds)) {
                        this.bounds.y -= this.speed.y * delta;
                        break;
                    }
                }
            }
            isMoving = true;
            currentCharacter = characterUp;
        }
        //premikanje dol
        if (inputData[3]) {
            this.bounds.y -= this.speed.y * delta;
            for (Tile t : gameField.tiles) {
                if (!t.isPassable) {
                    if (t.bounds.overlaps(this.bounds)) {
                        this.bounds.y += this.speed.y * delta;
                        break;
                    }
                }
            }
            isMoving = true;
            currentCharacter = characterDown;
        }

        //za powerupe
        for (Tile t : gameField.tiles) {
            if (t.bounds.overlaps(this.bounds)) {
                //če se dotikam tila, ki vsebuje powerup bombs
                if (t.isTile(GameField.POWERUP_BOMBS)) {
                    //povečam maximalno število bomb
                    bombs_max++;
                    //odstranim powerup
                    t.value = t.value & ~GameField.POWERUP_BOMBS;
                }
                //če se dotikam tila, ki vsebuje powerup power
                if (t.isTile(GameField.POWERUP_POWER)) {
                    //povečam moć
                    power++;
                    //odstranim powerup
                    t.value = t.value & ~GameField.POWERUP_POWER;
                }
                //če se dotikam tila, ki vsebuje powerup speed
                if (t.isTile(GameField.POWERUP_SPEED)) {
                    //povečam hitrost
                    speed.x++;
                    speed.y++;
                    //odstranim powerup
                    t.value = t.value & ~GameField.POWERUP_SPEED;
                }
                //če nisem imune in se dotikam tila, ki gori(na katerem je eksplozija)
                if (imune_timer > IMMUNE_TIME && (t.isTile(GameField.EXPLOSIONH) || t.isTile(GameField.EXPLOSIONV) || t.isTile(GameField.EXPLOSIONM))) {
                    //izgubim nekaj življenja, resetiram imune timer
                    MainScreen.log.debug("-25"+this.username);
                    health -= 25;
                    imune_timer = 0;
                }
            }
        }

        //nastavljanje bomb
        if (inputData[4]) {
            //če še lahko nastavim bomo, še ne presegam maksimalnega stevila nastavljenih bomb
            if (bombs_placed < bombs_max) {
                //najbližji tile, nanj bom nastavil bombo
                Tile tmp = this.closestTile(gameField.tiles);
                //če pe tile ni zaseden
                if (!tmp.isTile(GameField.BOMB)) {
                    //dodam novo bombo
                    bombs.add(new Bomb(tmp.pos.x, tmp.pos.y, this.power, this));
                    tmp.value = tmp.value | GameField.BOMB;
                    tmp.setWillExplode(true);
                    //za ai posodobim polja, willExplode lastnost uporabljam pri pathfinding
                    //UP
                    for (int i = 1; i < power + 1; i++) {
                        //tile 1 više od prejšnega
                        tmp = tmp.up(gameField, 1);
                        //če je neprebojen zid ne označujem dalje
                        if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                            tmp.setWillExplode(true);
                            //če pa je navaden zid pa njega označim, dalje pa ne
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    //DOWN
                    tmp = this.closestTile(gameField.tiles);
                    for (int i = 1; i < power + 1; i++) {
                        tmp = tmp.down(gameField, 1);
                        if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                            tmp.setWillExplode(true);
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    //RIGHT
                    tmp = this.closestTile(gameField.tiles);
                    for (int i = 1; i < power + 1; i++) {
                        tmp = tmp.right(gameField, 1);
                        if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                            tmp.setWillExplode(true);
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    //LEFT
                    tmp = this.closestTile(gameField.tiles);
                    for (int i = 1; i < power + 1; i++) {
                        tmp = tmp.left(gameField, 1);
                        if (!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)) {
                            tmp.setWillExplode(true);
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    //povečam število nastavljenih bomb za igralca
                    bombs_placed++;
                }
            }
        }
    }

    public abstract void renderPath(ShapeRenderer sr);

    public Tile closestTile(Array<Tile> tiles) {
        //zračunam razdaljo do vseh tilov in vrnem najkrašo
        Tile tmp = tiles.get(0);
        float distance = Math.abs(tmp.pos.x - this.bounds.x) + Math.abs(tmp.pos.y - this.bounds.y);
        for (Tile t : tiles) {
            float dist = Math.abs(t.pos.x - this.bounds.x) + Math.abs(t.pos.y - this.bounds.y);
            if (distance > dist) {
                tmp = t;
                distance = dist;
            }
        }
        return tmp;
    }

    public void render(SpriteBatch batch) {
        oldColor.set(batch.getColor());
        batch.setColor(color);
        //če je igralec mrtev izrišem smrtno glavo
        if (this.health <= 0) {
            batch.draw(skull, bounds.x, bounds.y, 1, 1);
        } else {
            //če se ne premika izrišem sliko- odvisno od smeri kamor se je nazadje premaknil
            if (!isMoving) {
                batch.draw(currentCharacter, bounds.x, bounds.y, 1, 1);
            } else {
                //če se premika pa animacijo - odvisno od smeri kamor se je nazadje premaknil
                if (currentCharacter == characterDown) {
                    batch.draw(downA.getKeyFrame(animationTime), bounds.x, bounds.y, 1, 1);
                } else if (currentCharacter == characterUp) {
                    batch.draw(upA.getKeyFrame(animationTime), bounds.x, bounds.y, 1, 1);
                } else if (currentCharacter == characterLeft) {
                    batch.draw(leftA.getKeyFrame(animationTime), bounds.x, bounds.y, 1, 1);
                } else if (currentCharacter == characterRight) {
                    batch.draw(rightA.getKeyFrame(animationTime), bounds.x, bounds.y, 1, 1);
                }
            }
        }
        batch.setColor(oldColor);
    }
}
