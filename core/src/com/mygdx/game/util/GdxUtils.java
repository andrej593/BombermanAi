package com.mygdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

public class GdxUtils {

    public static void clearScreen() {
        clearScreen(Color.BLACK);
    }

    public static void clearScreen(Color color) {
        // clear screen
        // DRY - Don't repeat yourself
        // WET - Waste everyone's time
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    public static void ProjectWorldCoordinatesInScreenCoordinates(Camera camera, Vector3 inWorldDimensions) {
        camera.project(inWorldDimensions);
    }

    //https://gamedev.stackexchange.com/questions/77658/how-to-match-font-size-with-screen-resolution
    //https://ilearnsomethings.blogspot.com/2014/02/libgdx-generate-bitmap-fonts-for-any.html
    public static BitmapFont getTTFFontInWorldUnits(float size, float worldHeght, int bwidth) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/simpleprintbold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        int calcSize = (int) (size / worldHeght * Gdx.graphics.getHeight());
        parameter.size = calcSize > 72 ? 72 : calcSize;
        parameter.color = Color.WHITE;
        parameter.borderWidth = bwidth;
        parameter.borderStraight = true;
        parameter.characters = "0123456789QWERTZUIOPASDFGHJKLYXCVBNMqwertzuiopasdfghjklxcvbnmy:.,-+ ";
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        BitmapFont tmp = generator.generateFont(parameter);
        tmp.getData().markupEnabled = true;
        tmp.getData().setScale((float) calcSize / tmp.getCapHeight());
        generator.dispose(); // avoid memory leaks, important
        return tmp;
    }

    public static String GetFormatedInMMSS(int duration) {
        return String.format("%02d:%02d", (int) (duration / 60), duration % 60);
    }

    private GdxUtils() {
    }
}
