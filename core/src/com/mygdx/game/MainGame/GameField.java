package com.mygdx.game.MainGame;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import org.omg.PortableServer.POA;

public class GameField {
    public int width;
    public int height;
    public Array<Tile> tiles;
    int powerups;

    public static final int FIELD_EMPTY = 2 << 0; //2
    public static final int FIELD_WALL = 2 << 1; //4
    public static final int FIELD_UNBRAKABLE_WALL = 2 << 2; //8

    public static final int POWERUP_BOMBS = 2 << 3; //16
    public static final int POWERUP_POWER = 2 << 4; //32
    public static final int POWERUP_SPEED = 2 << 5; //64

    public static final int EXPLOSIONM = 2 << 6; //128  //middle
    public static final int EXPLOSIONV = 2 << 7; //256  //vertical
    public static final int EXPLOSIONH = 2 << 8; //512  //horizontal
    public static final int BOMB = 2 << 9; //1024

    public GameField(int width, int height, int powerups) {
        this.height = height;
        this.width = width;
        this.powerups = powerups;
        tiles=new Array<Tile>();
        clearAndInit();
    }

    public void clearAndInit() {
        buildField();
    }

    public void buildField(){
        //usvarim vse prazne tile
        for(int i =0;i<width;i++){
            for(int j=0;j<height;j++){
                tiles.add(new Tile(i, j, FIELD_EMPTY, 0));
            }
        }

        //naključno generiram navadne zidove(50%)
        for(int i =0;i<width;i++){
            for(int j=0;j<height;j++){
                if( Math.random() > 0.5) {
                    tiles.get(width*i+j).value = tiles.get(width*i+j).value | FIELD_WALL;
                    tiles.get(width*i+j).isPassable=false;
                }
            }
        }
        //robovi - okvir neprebojnih zidov
        for(int i=0;i<width;i++){
            tiles.get(i).value = FIELD_UNBRAKABLE_WALL;
            tiles.get(i).isPassable = false;
            tiles.get(width*(width-1)+i).value = FIELD_UNBRAKABLE_WALL;
            tiles.get(width*(width-1)+i).isPassable = false;
            tiles.get(width*i+height-1).value = FIELD_UNBRAKABLE_WALL;
            tiles.get(width*i+height-1).isPassable = false;
            tiles.get(width*i).value = FIELD_UNBRAKABLE_WALL;
            tiles.get(width*i).isPassable = false;
        }

        //vsaki drugo polje v vsaki drugi vrstici je neprebojni zid
        for(int i=2;i<this.width;i+=2){
            for(int j=2;j<this.height;j+=2){
                tiles.get(width*i+j).value=FIELD_UNBRAKABLE_WALL;
                tiles.get(width*i+j).isPassable=false;
            }
        }

        //počistim robove v katerih začnejo igralci
        tiles.get(width*1+1).value=FIELD_EMPTY;
        tiles.get(width*1+2).value=FIELD_EMPTY;
        tiles.get(width*2+1).value=FIELD_EMPTY;

        tiles.get(width*1+1).isPassable=true;
        tiles.get(width*1+2).isPassable=true;
        tiles.get(width*2+1).isPassable=true;

        tiles.get(width*(height-2)+height-2).value=FIELD_EMPTY;
        tiles.get(width*(height-2)+height-3).value=FIELD_EMPTY;
        tiles.get(width*(height-3)+height-2).value=FIELD_EMPTY;

        tiles.get(width*(height-2)+height-2).isPassable=true;
        tiles.get(width*(height-2)+height-3).isPassable=true;
        tiles.get(width*(height-3)+height-2).isPassable=true;

        tiles.get(width*1+height-2).value=FIELD_EMPTY;
        tiles.get(width*2+height-2).value=FIELD_EMPTY;
        tiles.get(width*1+height-3).value=FIELD_EMPTY;

        tiles.get(width*1+height-2).isPassable=true;
        tiles.get(width*2+height-2).isPassable=true;
        tiles.get(width*1+height-3).isPassable=true;

        tiles.get(width*(height-2)+1).value=FIELD_EMPTY;
        tiles.get(width*(height-2)+2).value=FIELD_EMPTY;
        tiles.get(width*(height-3)+1).value=FIELD_EMPTY;

        tiles.get(width*(height-2)+1).isPassable=true;
        tiles.get(width*(height-2)+2).isPassable=true;
        tiles.get(width*(height-3)+1).isPassable=true;

        //v zidove naključno skrijem powerupe
        while(powerups > 0) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if(powerups!=0) {
                        Tile tmp = tiles.get(width*j+i);
                        if ((tmp.value & FIELD_WALL)== FIELD_WALL) {
                            if (Math.random() > 0.9) {
                                if(((tmp.value & POWERUP_SPEED) != POWERUP_SPEED) &&
                                        ((tmp.value & POWERUP_POWER) != POWERUP_POWER) &&
                                        ((tmp.value & POWERUP_BOMBS) != POWERUP_BOMBS)){
                                    double num = Math.random();
                                    if (num < 0.4) {
                                        tmp.value = tmp.value | POWERUP_BOMBS;
                                        powerups--;
                                    } else if (num < 0.8) {
                                        tmp.value = tmp.value | POWERUP_POWER;
                                        powerups--;
                                    } else {
                                        tmp.value = tmp.value | POWERUP_SPEED;
                                        powerups--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //logMatrix();
    }

    public void logMatrix() {
        String message = "";
        for(Tile t : tiles){
            message+=t.toString();
        }
        MainScreen.log.debug(message);
    }

}
