package com.mygdx.game.MainGame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Tile {
    public Vector2 pos; //pozicija x,y
    public Rectangle bounds;
    public int value;   //vrednost zastavic

    //ločeno indiksiranje, saj se lahko en tile pojavi v obeh grafih in bo v vsakem drugače označen
    public int indexGraphWalls; //index za graf z zidovi
    public int indexGraphEmpty; //index za graf z praznimi polji

    boolean willExplode;    //če bo eksplodiralo
    boolean isPassable; //če je prehodno

    @Override
    public String toString() {
        return "pos=" + pos.x + ", " + pos.y + ", " + value;
    }

    public Tile(int x, int y, int value, int index) {
        this.pos = new Vector2(x, y);
        this.bounds = new Rectangle(x - 1 / 2, y - 1 / 2, 1, 1);
        this.value = value;
        this.indexGraphEmpty = index;
        this.indexGraphWalls = index;
        this.willExplode = false;
        this.isPassable = true;
    }

    public void setIndexGraphWalls(int i) {
        this.indexGraphWalls = i;
    }
    public void setIndexGraphEmpty(int i) {
        this.indexGraphEmpty = i;
    }
    public void setWillExplode(boolean v) {
        this.willExplode = v;
    }
    public boolean getWillExplode() {
        return this.willExplode;
    }
    public void setIsPassable(boolean isPassable) {
        this.isPassable = isPassable;
    }
    public boolean getIsPassable() {
        return isPassable;
    }
    //vrne desni tile
    public Tile right(GameField gf, int i) {
        return gf.tiles.get((int) ((this.pos.x + i) * gf.width + this.pos.y));
    }
    //crne levi tile
    public Tile left(GameField gf, int i) {
        return gf.tiles.get((int) ((this.pos.x - i) * gf.width + this.pos.y));
    }
    //vrne zgornji tile
    public Tile up(GameField gf, int i) {
        return gf.tiles.get((int) (this.pos.x * gf.width + this.pos.y + i));
    }
    //vrne spodji tile
    public Tile down(GameField gf, int i) {
        return gf.tiles.get((int) (this.pos.x * gf.width + this.pos.y - i));
    }
    //vrne če je tile trenutno varen in prehoden
    public boolean isSafeTile() {
        if ((this.value & GameField.EXPLOSIONV) == GameField.EXPLOSIONV ||
                (this.value & GameField.EXPLOSIONM) == GameField.EXPLOSIONM ||
                (this.value & GameField.EXPLOSIONH) == GameField.EXPLOSIONH ||
                !this.isPassable ||
                this.willExplode)
            return false;
        else
            return true;
    }
    //vrne če ima tile to vrednost
    public boolean isTile(int value) {
        if ((this.value & value) == value)
            return true;
        else
            return false;
    }

    public Tile closestTile(Array<Tile> tiles) {
        Tile tmp = tiles.get(0);
        float distance = Math.abs(tmp.pos.x - this.pos.x) + Math.abs(tmp.pos.y - this.pos.y);
        for (Tile t : tiles) {
            float dist = Math.abs(t.pos.x - this.pos.x) + Math.abs(t.pos.y - this.pos.y);
            if (distance > dist) {
                tmp = t;
                distance = dist;
            }
        }
        return tmp;
    }

    public void update(Array<Character> players) {
        if (this.isTile(GameField.BOMB) && isPassable == true) {
            boolean isEmpty = true;
            for (Character p : players) {
                if (p.bounds.overlaps(this.bounds))
                    isEmpty = false;
            }
            if (isEmpty)
                isPassable = false;
        }
    }
}
