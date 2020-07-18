package com.mygdx.game.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {
    public static final AssetDescriptor<TextureAtlas> ATLAS =
            new AssetDescriptor<TextureAtlas>(AssetPaths.ATLAS, TextureAtlas.class);
    public static final AssetDescriptor<Skin> GAME_UI =
            new AssetDescriptor<Skin>(AssetPaths.GAME_UI, Skin.class);
    private AssetDescriptors() {}
}
