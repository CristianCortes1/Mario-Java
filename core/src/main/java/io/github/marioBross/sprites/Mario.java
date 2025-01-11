package io.github.marioBross.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.enemies.Enemy;
import io.github.marioBross.sprites.enemies.Turtle;

public class Mario extends Sprite {


    public enum State {
        FALLING,
        JUMPING,
        STANDING,
        RUNNING,
        GROWING,
        DEAD,

    }

    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioFall;
    private TextureRegion bigMarioFall;
    private TextureRegion marioDead;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private final Animation<TextureRegion> growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;
    private boolean marioIsWinning;

    private float timer;
    private boolean moveToRightFlag;
    private boolean marioIsInFinish;


    public Mario(PlayScreen screen) {
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        moveToRightFlag = true;
        marioIsInFinish = false;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        marioRun = new Animation<>(0.1f, frames);
        frames.clear();


        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation<>(0.1f, frames);
        frames.clear();

        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        }
        growMario = new Animation<>(0.15f, frames);
        frames.clear();


        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        marioFall = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 128, 0, 16, 16);
        bigMarioFall = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 144, 0, 16, 32);
        defineMario();
        setBounds(0, 0, 16 / MarioBross.PPM, 16 / MarioBross.PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {

        if (marioIsWinning) {
            runWinAnimation(dt);
        }
        setRegion(getFrame(dt));
        if (marioIsBig) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() /2 - 6 / MarioBross.PPM);
        } else {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }


        if (timeToDefineBigMario)
            defineBigMario();
        if (timeToRedefineMario)
            redefineMario();


        if (b2body.getPosition().y < 0) {
            if (!marioIsDead) {
                marioIsDead = true;
                MarioBross.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBross.manager.get("audio/sounds/gato-riendo.mp3", Sound.class).play(0.1f);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }

    }

    private void runWinAnimation(float dt) {
        while (moveToRightFlag) {
            b2body.setTransform(b2body.getPosition().x + .32f, b2body.getPosition().y, 0);
            moveToRightFlag = false;
        }
        if (marioIsBig) {
            if (b2body.getPosition().y > .43f) {
                b2body.setLinearVelocity(0, -1);
            } else {
                marioIsInFinish = false;
                timer += dt;
                if (timer < .99) {
                    b2body.setLinearVelocity(1, 0);
                } else {
                    marioIsWin = true;

                }
            }
        } else {
            if (b2body.getPosition().y > .26f) {
                b2body.setLinearVelocity(0, -1);
            } else {
                marioIsInFinish = false;
                timer += dt;
                if (timer < .99) {
                    b2body.setLinearVelocity(1, 0);
                } else {
                    marioIsWin = true;

                }
            }
        }
    }

    private TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;

        switch (currentState) {
            case FALLING:
                region = marioIsBig ? bigMarioFall : marioFall;
                break;
            case DEAD:
                region = marioDead;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true)
                    : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    runGrowAnimation = false;
                }
                break;
            default:
                region = marioStand;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        if (marioIsInFinish && !region.isFlipX()) {
            region.flip(true, false);
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    private State getState() {
        if (marioIsDead) {
            return State.DEAD;
        }else if (runGrowAnimation) {
            return State.GROWING;
        } else if (b2body.getLinearVelocity().y != 0 && !marioIsInFinish) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else if (marioIsWinning) {
            return State.FALLING;
        }  else {
            return State.STANDING;
        }
    }

    public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioBross.manager.get("audio/sounds/kaioken.mp3", Sound.class).play();
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(200 / MarioBross.PPM, 32 / MarioBross.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBross.PPM);

        fdef.filter.categoryBits = MarioBross.MARIO_BIT;
        fdef.filter.maskBits = MarioBross.GROUND_BIT | MarioBross.BRICK_BIT | MarioBross.COIN_BIT
            | MarioBross.ENEMY_BIT | MarioBross.OBJECT_BIT | MarioBross.ENEMY_HEAD_BIT | MarioBross.ITEM_BIT
            | MarioBross.FINISH_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBross.PPM, 7 / MarioBross.PPM), new Vector2(2 / MarioBross.PPM, 7 / MarioBross.PPM));
        fdef.filter.categoryBits = MarioBross.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;


        b2body.createFixture(fdef).setUserData(this);

    }

    private void redefineMario() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBross.PPM);

        fdef.filter.categoryBits = MarioBross.MARIO_BIT;
        fdef.filter.maskBits = MarioBross.GROUND_BIT | MarioBross.BRICK_BIT | MarioBross.COIN_BIT
            | MarioBross.ENEMY_BIT | MarioBross.OBJECT_BIT | MarioBross.ENEMY_HEAD_BIT | MarioBross.ITEM_BIT
            | MarioBross.FINISH_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBross.PPM, 7 / MarioBross.PPM), new Vector2(2 / MarioBross.PPM, 7 / MarioBross.PPM));
        fdef.filter.categoryBits = MarioBross.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;


        b2body.createFixture(fdef).setUserData(this);
        timeToRedefineMario = false;

    }

    public void defineBigMario() {
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 16 / MarioBross.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBross.PPM);

        fdef.filter.categoryBits = MarioBross.MARIO_BIT;
        fdef.filter.maskBits = MarioBross.GROUND_BIT | MarioBross.BRICK_BIT | MarioBross.COIN_BIT
            | MarioBross.ENEMY_BIT | MarioBross.OBJECT_BIT | MarioBross.ENEMY_HEAD_BIT | MarioBross.ITEM_BIT
            | MarioBross.FINISH_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBross.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBross.PPM, 6 / MarioBross.PPM), new Vector2(2 / MarioBross.PPM, 6 / MarioBross.PPM));
        fdef.filter.categoryBits = MarioBross.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;


        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }


    public void hit(Enemy enemy) {

        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);

        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                MarioBross.manager.get("audio/sounds/gato-riendo.mp3", Sound.class).play(0.1f);
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            } else {
                MarioBross.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioBross.manager.get("audio/sounds/gato-riendo.mp3", Sound.class).play(0.1f);
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBross.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }
        }
    }

    public boolean isBig() {
        return marioIsBig;
    }

    private boolean marioIsWin;

    public void win() {
        MarioBross.manager.get("audio/music/mario_music.ogg", Music.class).stop();
        MarioBross.manager.get("audio/sounds/mission-complete.mp3", Sound.class).play();
        Gdx.app.log("Mario", "You win!");
        marioIsWinning = true;
        marioIsInFinish = true;
    }

    public boolean isWin() {
        return marioIsWin;
    }


}
