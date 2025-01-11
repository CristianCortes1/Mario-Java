package io.github.marioBross.sprites.items;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github.marioBross.MarioBross;
import io.github.marioBross.Screens.PlayScreen;
import io.github.marioBross.sprites.Mario;

public class Mushroom extends Item{

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fdef = new FixtureDef();
        Shape shape = new CircleShape();
        shape.setRadius(6 / MarioBross.PPM);

        fdef.filter.categoryBits = MarioBross.ITEM_BIT;
        fdef.filter.maskBits = MarioBross.GROUND_BIT |
            MarioBross.COIN_BIT |
            MarioBross.BRICK_BIT |
            MarioBross.OBJECT_BIT |
            MarioBross.MARIO_BIT |
            MarioBross.ITEM_BIT;

        fdef.shape = shape;

        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        destroy();
        if (!mario.isBig()) {
            mario.grow();
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
