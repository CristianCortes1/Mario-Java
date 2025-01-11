package io.github.marioBross.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Scenes.Hud;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.Mario;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBross.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            setCategoryFilter(MarioBross.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBross.manager.get("audio/sounds/breakblock.wav", com.badlogic.gdx.audio.Sound.class).play();
        } else
            MarioBross.manager.get("audio/sounds/bump.wav", com.badlogic.gdx.audio.Sound.class).play();

    }
}
