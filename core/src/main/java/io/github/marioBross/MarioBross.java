package io.github.marioBross;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.marioBross.Screens.PlayScreen;

public class MarioBross extends Game {

    public static final String TITLE = "MarioBross";
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public static final short FINISH_BIT = 1024;
    public static String level = "level1.tmx";
    public SpriteBatch batch;

    public static AssetManager manager;


    public MarioBross(){
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/mario_music.ogg", Music.class);
        manager.load("audio/sounds/coin.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.load("audio/sounds/breakblock.wav", Sound.class);
        manager.load("audio/sounds/jump.mp3", Sound.class);
        manager.load("audio/sounds/gato-riendo.mp3", Sound.class);
        manager.load("audio/sounds/fart.mp3", Sound.class);
        manager.load("audio/sounds/siu.mp3", Sound.class);
        manager.load("audio/sounds/vine-boom.mp3", Sound.class);
        manager.load("audio/sounds/kaioken.mp3", Sound.class);
        manager.load("audio/sounds/dough.mp3", Sound.class);
        manager.load("audio/sounds/y-se-marcho.mp3", Sound.class);
        manager.load("audio/sounds/mission-complete.mp3", Sound.class);

        manager.finishLoading();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        manager.dispose();
        batch.dispose();
    }

}
