package com.mygdx.game.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.MainGame.MainScreen;
import com.mygdx.game.util.GdxUtils;

public class HUD implements Disposable {
    MainScreen mainscreen;

    Vector3 statusCoordinates;
    Vector3 winnerCoordinates;
    Vector3 durationCoordinates;

    public BitmapFont font;

    float x;
    float y;
    float w;
    float h;

    public static float duration;

    public HUD(float x, float y, float w, float h, MainScreen main){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        statusCoordinates=new Vector3();
        winnerCoordinates=new Vector3();
        durationCoordinates = new Vector3();
        this.mainscreen = main;
        createFontBasedObjects();
        start();
    }

    public void start() {
        duration = 0;
    }

    void updateDuration(float duration) {
        this.duration = duration;
    }

    public void render(SpriteBatch batch, float dt) {
        batch.setProjectionMatrix(mainscreen.cameraFont.combined);

        font.setColor(Color.RED);
        font.draw(batch, GdxUtils.GetFormatedInMMSS((int) duration), durationCoordinates.x, durationCoordinates.y);

        if(mainscreen.gameState ==1){
            font.setColor(Color.GREEN);
            GlyphLayout layout = new GlyphLayout(font,  mainscreen.winner.username+" WON");
            font.draw(batch, layout, winnerCoordinates.x - layout.width/2, winnerCoordinates.y);
        }else {
            if (mainscreen.paused) {
                font.setColor(Color.RED);
                GlyphLayout layout = new GlyphLayout(font, "PAUSED");
                font.draw(batch, layout, statusCoordinates.x - layout.width, statusCoordinates.y);
            } else {
                updateDuration(duration + dt);
            }
        }
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    private void createFontBasedObjects() {
        font = GdxUtils.getTTFFontInWorldUnits(1f, mainscreen.boardV.getWorldHeight(), 2);
        durationCoordinates.set(x, y - h / 2 + 0.5f, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(mainscreen.boardCam, durationCoordinates);
        statusCoordinates.set(x + w, y - h / 2 + 0.5f, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(mainscreen.boardCam, statusCoordinates);
        winnerCoordinates.set(x+w/2, y-h/2+0.5f, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(mainscreen.boardCam, winnerCoordinates);
    }

    public void resize(int width, int height) {
        if (font != null) font.dispose();
        createFontBasedObjects();
    }
}
