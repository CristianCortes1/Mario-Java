package io.github.marioBross.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.*;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Scenes.Hud;
import io.github.marioBross.Tools.B2WorldCreator;
import io.github.marioBross.Tools.WorldContactListener;
import io.github.marioBross.sprites.enemies.Enemy;
import io.github.marioBross.sprites.Mario;
import io.github.marioBross.sprites.enemies.Goomba;
import io.github.marioBross.sprites.enemies.Turtle;
import io.github.marioBross.sprites.items.Item;
import io.github.marioBross.sprites.items.ItemDef;
import io.github.marioBross.sprites.items.Mushroom;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private MarioBross game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;


    private Mario player;


    private Music music;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    private float initialTouchY = -1f;


    public PlayScreen(MarioBross game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioBross.V_WIDTH / MarioBross.PPM, MarioBross.V_HEIGHT / MarioBross.PPM, gameCam);
        Hud.level = switch (MarioBross.level) {
            case "level1.tmx" -> "1-1";
            case "level2.tmx" -> "1-2";
            case "level3.tmx" -> "1-3";
            case "level4.tmx" -> "1-4";
            default -> "??";
        };
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(MarioBross.level);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBross.PPM);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);


        world.setContactListener(new WorldContactListener());

        music = MarioBross.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.2f);

        music.play();

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();

    }

    public boolean gameOver() {
        if (player.currentState== Mario.State.DEAD && player.getStateTimer() > 3) {
            return true;
        } else {
            return false;
        }
    }
    private boolean gameWin() {
        if (player.isWin()) {
            return true;
        } else {
            return false;
        }
    }


    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);

    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }


    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (player.currentState != Mario.State.DEAD && player.currentState != Mario.State.FALLING) {
            if (player.b2body.getPosition().x < 0.1f)
                player.b2body.applyLinearImpulse(new Vector2(1f, 0), player.b2body.getWorldCenter(), true);
            else if (player.b2body.getPosition().x > 38)
                player.b2body.applyLinearImpulse(new Vector2(-1f, 0), player.b2body.getWorldCenter(), true);


            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) {
//                if (player.b2body.getLinearVelocity().y == 0) {
                    player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
                    MarioBross.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.1f);
//                }
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && player.b2body.getLinearVelocity().x <= 2) {
                    player.b2body.applyLinearImpulse(new Vector2(.1f, 0), player.b2body.getWorldCenter(), true);
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && player.b2body.getLinearVelocity().x >= -2) {
                    player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                }
            // Variables para detectar el deslizamiento
            if (Gdx.input.isTouched()) {
                // Obtener la posición del toque en pantalla
                float touchX = Gdx.input.getX();
                float touchY = Gdx.input.getY();

                // Si es el inicio del toque, guardar la posición Y inicial
                if (initialTouchY == -1f) {
                    initialTouchY = touchY;
                }

                // Determinar si el toque está en la mitad izquierda o derecha de la pantalla
                if (touchX > Gdx.graphics.getWidth() / 2) {
                    // Tocar la mitad derecha - mover a la derecha
                    if (player.b2body.getLinearVelocity().x <= 2) {
                        player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                    }
                } else {
                    // Tocar la mitad izquierda - mover a la izquierda
                    if (player.b2body.getLinearVelocity().x >= -2) {
                        player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                    }
                }
            }
            else {
                // Verificar si se realizó deslizamiento hacia arriba para el salto
                if (initialTouchY != -1f && initialTouchY - Gdx.input.getY() > 50f) { // Verifica que el deslizamiento en Y sea suficiente
                    // Saltar: Aplicar impulso hacia arriba si no se está ya saltando
                    if (player.b2body.getLinearVelocity().y == 0) { // Verifica que el jugador esté en el suelo
                        player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
                        MarioBross.manager.get("audio/sounds/jump.mp3", Sound.class).play(0.1f);
                    }
                }
                // Reiniciar posición inicial del toque
                initialTouchY = -1f;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            if (music.getVolume() < 0.1f) {
                music.setVolume(0.1f);
            } else {
                music.setVolume(music.getVolume() - 0.1f);
                System.out.println(music.getVolume());
            }

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (music.getVolume() > 0.9f) {
                music.setVolume(0.9f);
            } else {
                music.setVolume(music.getVolume() + 0.1f);
                System.out.println(music.getVolume());
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    public void update(float dt) {

        handleInput(dt);
        handleSpawningItems();

        world.step(1 / 60f, 6, 2);

        player.update(dt);

        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 224 / MarioBross.PPM) {
                enemy.b2body.setActive(true);
            }
        }


        for (Item item : items) {
            item.update(dt);
        }
        hud.update(dt);

        if (player.currentState != Mario.State.DEAD) {
            if (player.b2body.getPosition().x > 2 && player.b2body.getPosition().x < 36) {
                gameCam.position.x = player.b2body.getPosition().x;
            }
        }

        gameCam.update();

        renderer.setView(gameCam);

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
        if (gameWin()) {
            game.setScreen(new GameWinScreen(game));
            dispose();
        }

    }



    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.stage.dispose();
    }
}
