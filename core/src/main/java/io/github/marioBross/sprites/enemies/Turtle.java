package io.github.marioBross.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.Tools.B2WorldCreator;
import io.github.marioBross.sprites.Mario;

public class Turtle extends Enemy {
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}

    public final static int KICK_LEFT_SPEED = -2;
    public final static int KICK_RIGHT_SPEED = 2;
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion shell;
    private Array<TextureRegion> frames;
    private float deadRotationDegrees;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for (int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), i * 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation<>(0.4f, frames);
        currentState = previousState = State.WALKING;
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBross.PPM, 24 / MarioBross.PPM);
        deadRotationDegrees = 0;
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
        vertice[2] = new Vector2(5, 16).scl(1 / MarioBross.PPM);
        vertice[3] = new Vector2(-5, 16).scl(1 / MarioBross.PPM);
        head.set(vertice);
        fdef.shape = head;
        fdef.restitution = .8f;
        fdef.filter.categoryBits = MarioBross.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y  - 2 / MarioBross.PPM);

        if (currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if (stateTime > 3 && !destroyed) {
                world.destroyBody(b2body);
                B2WorldCreator.removeTurle(this);
                destroyed = true;
            }
        } else {
            b2body.setLinearVelocity(velocity);
        }

    }

    private TextureRegion getFrame(float dt) {
        TextureRegion region;
        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void hitOnHead(Mario mario) {
        if (currentState != State.STANDING_SHELL) {
            MarioBross.manager.get("audio/sounds/dough.mp3", Sound.class).play();
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }

    }
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1)
            super.draw(batch);
    }

    public void kick(int speed) {
        Sound sound = MarioBross.manager.get("audio/sounds/y-se-marcho.mp3", Sound.class);
        sound.play();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                sound.stop();
            }
        }, 1.8f);

        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState() {
        return currentState;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle) {
            if (((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING) {
                ((Turtle)enemy).killed();
            } else {
                reverseVelocity(true, false);
                enemy.reverseVelocity(true, false);
            }
        } else if (currentState != State.MOVING_SHELL) {
            reverseVelocity(true, false);
            enemy.reverseVelocity(true, false);
        }

    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBross.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);
    }

}
