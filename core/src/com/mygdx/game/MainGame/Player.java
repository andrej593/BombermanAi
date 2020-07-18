package com.mygdx.game.MainGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.assets.RegionNames;

public class Player extends Character{
    int up;
    int down;
    int left;
    int right;
    int bomb;


    public Player(int inputSet, int x,  int y, TextureAtlas atlas, String u, Color c, GameField gf ) {
        super(x,y,atlas,u, c, gf);
        //nastavim input keye
        if(inputSet == 0){
            up=Input.Keys.UP;
            down=Input.Keys.DOWN;
            left=Input.Keys.LEFT;
            right=Input.Keys.RIGHT;
            bomb=Input.Keys.SPACE;
        }else if(inputSet == 1){
            up=Input.Keys.W;
            down=Input.Keys.S;
            left=Input.Keys.A;
            right=Input.Keys.D;
            bomb=Input.Keys.Q;
        }else if(inputSet == 2){
            up=Input.Keys.T;
            down=Input.Keys.G;
            left=Input.Keys.F;
            right=Input.Keys.H;
            bomb=Input.Keys.R;
        }else if(inputSet == 3){
            up=Input.Keys.I;
            down=Input.Keys.K;
            left=Input.Keys.J;
            right=Input.Keys.L;
            bomb=Input.Keys.U;
        }
    }

    @Override
    public void renderPath(ShapeRenderer sr) {}

    public void update(float delta, Array<Bomb> bombs, boolean inputData[]) {
        //preverim input
        inputData[0] = Gdx.input.isKeyPressed(left);
        inputData[1] = Gdx.input.isKeyPressed(right);
        inputData[2] = Gdx.input.isKeyPressed(up);
        inputData[3] = Gdx.input.isKeyPressed(down);
        inputData[4] = Gdx.input.isKeyJustPressed(bomb);
        //izvedem premik, nastavljanje bombe
        super.update(delta, bombs, inputData);
    }
}
