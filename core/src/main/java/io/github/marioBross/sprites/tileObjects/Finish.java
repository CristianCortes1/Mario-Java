package io.github.marioBross.sprites.tileObjects;

import com.badlogic.gdx.maps.MapObject;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.Mario;

public class Finish extends InteractiveTileObject{

    public Finish(PlayScreen screen, MapObject object) {
        super(screen, object);

    }

    @Override
    public void onHeadHit(Mario mario) {

    }
}
