package io.github.marioBross.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Scenes.Hud;
import io.github.marioBross.Screens.PlayScreen;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github.marioBross.sprites.Mario;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for (int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation<>(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBross.PPM, 16 / MarioBross.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6 / MarioBross.PPM, .1f / MarioBross.PPM);

        fdef.filter.categoryBits = MarioBross.ENEMY_BIT;
        fdef.filter.maskBits = MarioBross.GROUND_BIT |
                MarioBross.COIN_BIT |
                MarioBross.BRICK_BIT |
                MarioBross.ENEMY_BIT |
                MarioBross.OBJECT_BIT |
                MarioBross.MARIO_BIT;

        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);

        // Create the head of the goomba
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-4, 1).scl(1 / MarioBross.PPM);
        vertice[1] = new Vector2(4, 1).scl(1 / MarioBross.PPM);
        vertice[2] = new Vector2(6, 16).scl(1 / MarioBross.PPM);
        vertice[3] = new Vector2(-6, 16).scl(1 / MarioBross.PPM);
        head.set(vertice);
        fdef.shape = head;
        fdef.restitution = 0.8f;
        fdef.filter.categoryBits = MarioBross.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        } else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 6 / MarioBross.PPM);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }

    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        MarioBross.manager.get("audio/sounds/fart.mp3", Sound.class).play();
        Hud.addScore(150);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL) {
            setToDestroy = true;
        } else {
            reverseVelocity(true, false);
            enemy.reverseVelocity(true, false);
        }
    }

}
