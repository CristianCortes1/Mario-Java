package io.github.marioBross.sprites.tileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Scenes.Hud;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.Mario;
import io.github.marioBross.sprites.items.ItemDef;
import io.github.marioBross.sprites.items.Mushroom;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBross.COIN_BIT);

    }

    @Override
    public void onHeadHit(Mario mario) {
        if (getCell().getTile().getId() == BLANK_COIN) {
            MarioBross.manager.get("audio/sounds/bump.wav", Sound.class).play();
            Gdx.app.log("Coin", "No Coin");
        }
        else {
            if (object.getProperties().containsKey("mushroom")){
                MarioBross.manager.get("audio/sounds/vine-boom.mp3", Sound.class).play();
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBross.PPM), Mushroom.class));
                Hud.addScore(50);
                Gdx.app.log("Coin", "Mushroom");
            }else{
                Gdx.app.log("Coin", "Collected");
                MarioBross.manager.get("audio/sounds/coin.wav", Sound.class).play();
                Hud.addScore(100);
            }
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));

    }
}
