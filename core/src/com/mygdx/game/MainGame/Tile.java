package com.mygdx.game.MainGame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Tile {
    public Vector2 pos;
    public Rectangle bounds;
    public int value;

    //ločeno indiksiranje, saj se lahko en tile pojavi v obeh grafih in bo v vsakem drugače označen
    public int indexGraphWalls; //index za graf z zidovi
    public int indexGraphEmpty; //index za graf z praznimi polji

    boolean willExplode;
    boolean isPassable;

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
    public Tile right(GameField gf) {
        return gf.tiles.get((int) ((this.pos.x + 1) * gf.width + this.pos.y));
    }

    //crne levi tile
    public Tile left(GameField gf) {
        return gf.tiles.get((int) ((this.pos.x - 1) * gf.width + this.pos.y));
    }

    //vrne zgornji tile
    public Tile up(GameField gf) {
        return gf.tiles.get((int) (this.pos.x * gf.width + this.pos.y + 1));
    }

    //vrne spodji tile
    public Tile down(GameField gf) {
        return gf.tiles.get((int) (this.pos.x * gf.width + this.pos.y - 1));
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

    //ko igralec nastavi bombo še mora biti sposoben bombo zapustit , nato pa postane neprehodna, problem sem imel da je igralec zaštekal v bombi ko jo je nastavil
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
