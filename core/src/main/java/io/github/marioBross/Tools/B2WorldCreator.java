package io.github.marioBross.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.enemies.Enemy;
import io.github.marioBross.sprites.enemies.Turtle;
import io.github.marioBross.sprites.tileObjects.Brick;
import io.github.marioBross.sprites.tileObjects.Coin;
import io.github.marioBross.sprites.enemies.Goomba;

public class B2WorldCreator {
    private World world;
    private TiledMap map;
    private Array<Goomba> goombas;
    private static Array<Turtle> turtles;

    public B2WorldCreator(PlayScreen screen) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        createBodies(screen);

    }

    private void createBodies(PlayScreen screen) {

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //create ground bodies/fixtures

        Body body;

        //create ground bodies/fixtures
        //Ground
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBross.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBross.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBross.PPM, rect.getHeight() / 2 / MarioBross.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBross.OBJECT_BIT;


            body.createFixture(fdef);
        }
        //Pipe
        for (RectangleMapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBross.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBross.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBross.PPM, rect.getHeight() / 2 / MarioBross.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBross.OBJECT_BIT;


            body.createFixture(fdef);
        }
        //Coin
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }
        //Brick
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }

        //create goombas
        goombas = new Array<>();
        for (RectangleMapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            goombas.add(new Goomba(screen, rect.getX() / MarioBross.PPM, rect.getY() / MarioBross.PPM));
        }
        //create turtles
        turtles = new Array<>();
        for (RectangleMapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = object.getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / MarioBross.PPM, rect.getY() / MarioBross.PPM));
        }
        //create finishing line
        for (MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBross.PPM, (rect.getY() + rect.getHeight() / 2) / MarioBross.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MarioBross.PPM, rect.getHeight() / 2 / MarioBross.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MarioBross.FINISH_BIT;

            body.createFixture(fdef);
        }


    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

    public static void removeTurle(Turtle turtle) {
        turtles.removeValue(turtle, true);
    }
}
