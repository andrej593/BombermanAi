package com.mygdx.game.MainGame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Bomb {
    public static final float EXPLOSION_TIME = 1;   //kdaj bo eksplodirala
    public static final float LIFE_TIME = 1.5f;     //življenska doba

    float time;

    Rectangle bounds;
    Vector2 pos;

    int power;  //moč eksplozije
    boolean lethal; //če je že eksplodirala al še ne
    Character player;   //igralec ki je bombo nastavil

    public Bomb(float x,  float y, int power, Character player) {
        this.pos = new Vector2(x, y);
        this.bounds = new Rectangle(x-1/2, y-1/2, 1, 1);
        this.time=0;
        this.power=power;
        lethal=false;
        this.player=player;
    }

    //prisilno sprožim bombo
    void forceExplosion(){
        time=EXPLOSION_TIME;
    }

    //vrne polje na katerem je bomba
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

    void update(float delta, GameField f, Array<Bomb> bombs){
        time+=delta;
        Tile tmp = this.closestTile(f.tiles);

        //če je čas za eksplozijo
        if(time>=EXPLOSION_TIME){
            if(!lethal) {
                lethal = true;
                player.bombs_placed--;  //igralcu zmanjšam števec nastavljenim bomb

                //posodobim vrednost tilea
                tmp.value = tmp.value | GameField.EXPLOSIONM;
                tmp.value = tmp.value & ~GameField.BOMB;

                //posodobim še ostale tile explozije
                //UP
                for(int i=1;i<power+1;i++){
                    tmp = tmp.up(f);
                    //če je neprebojen zid se ustavim
                    if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                        //če je bomba jo prisilno sprožim
                        if(tmp.isTile(GameField.BOMB)){
                            for(Bomb b:bombs){
                                if(tmp.bounds.overlaps(b.bounds)){
                                    b.forceExplosion();
                                }
                            }
                        }else{
                            //če je prazno polje dodam exsplozijo
                            tmp.value = tmp.value | GameField.EXPLOSIONV;
                            //če je navadni zid ga razbijem, ne nadaljujem
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                tmp.value = tmp.value & ~GameField.FIELD_WALL;
                                tmp.setIsPassable(true);
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                //DOWN
                tmp = this.closestTile(f.tiles);
                for(int i=1;i<power+1;i++){
                    tmp = tmp.down(f);
                    if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                        if(tmp.isTile(GameField.BOMB)){
                            for(Bomb b:bombs){
                                if(tmp.bounds.overlaps(b.bounds)){
                                    b.forceExplosion();
                                }
                            }
                        }else{
                            tmp.value = tmp.value | GameField.EXPLOSIONV;
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                tmp.value = tmp.value & ~GameField.FIELD_WALL;
                                tmp.setIsPassable(true);
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                //RIGHT
                tmp = this.closestTile(f.tiles);
                for(int i=1;i<power+1;i++){
                    tmp = tmp.right(f);
                    if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                        if(tmp.isTile(GameField.BOMB)){
                            for(Bomb b:bombs){
                                if(tmp.bounds.overlaps(b.bounds)){
                                    b.forceExplosion();
                                }
                            }
                        }else{
                            tmp.value = tmp.value | GameField.EXPLOSIONH;
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                tmp.value = tmp.value & ~GameField.FIELD_WALL;
                                tmp.setIsPassable(true);
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
                //LEFT
                tmp = this.closestTile(f.tiles);
                for(int i=1;i<power+1;i++){
                    tmp = tmp.left(f);
                    if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                        if(tmp.isTile(GameField.BOMB)){
                            for(Bomb b:bombs){
                                if(tmp.bounds.overlaps(b.bounds)){
                                    b.forceExplosion();
                                }
                            }
                        }else{
                            tmp.value = tmp.value | GameField.EXPLOSIONH;
                            if (tmp.isTile(GameField.FIELD_WALL)) {
                                tmp.value = tmp.value & ~GameField.FIELD_WALL;
                                tmp.setIsPassable(true);
                                break;
                            }
                        }
                    }else{
                        break;
                    }
                }
            }
        }
    }

    //odstrani explozijo po določenem času
    void destroy(GameField gf){
        //sredina explozije
        Tile tmp = this.closestTile(gf.tiles);
        tmp.value = tmp.value & ~GameField.EXPLOSIONM;
        tmp.setIsPassable(true);
        tmp.setWillExplode(false);
        //remove up
        for(int i=1;i<power+1;i++){
            tmp = tmp.up(gf);
            if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                tmp.value = tmp.value  & ~GameField.EXPLOSIONV;
                tmp.setWillExplode(false);
            }else{
                break;
            }
        }
        //DOWN
        tmp = this.closestTile(gf.tiles);
        for(int i=1;i<power+1;i++){
            tmp = tmp.down(gf);
            if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                tmp.value = tmp.value  & ~GameField.EXPLOSIONV;
                tmp.setWillExplode(false);
            }else{
                break;
            }
        }
        //RIGHT
        tmp = this.closestTile(gf.tiles);
        for(int i=1;i<power+1;i++){
            tmp = tmp.right(gf);
            if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                tmp.value = tmp.value  & ~GameField.EXPLOSIONH;
                tmp.setWillExplode(false);
            }else{
                break;
            }
        }
        //LEFT
        tmp = this.closestTile(gf.tiles);
        for(int i=1;i<power+1;i++){
            tmp = tmp.left(gf);
            if(!tmp.isTile(GameField.FIELD_UNBRAKABLE_WALL)){
                tmp.value = tmp.value  & ~GameField.EXPLOSIONH;
                tmp.setWillExplode(false);
            }else{
                break;
            }
        }
    }
}
