package io.github.marioBross.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;
import io.github.marioBross.MarioBross;
import io.github.marioBross.sprites.Mario;
import io.github.marioBross.sprites.enemies.Enemy;
import io.github.marioBross.sprites.items.Item;
import io.github.marioBross.sprites.tileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case MarioBross.MARIO_BIT | MarioBross.FINISH_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.MARIO_BIT)
                    ((Mario) fixA.getUserData()).win();
                else
                    ((Mario) fixB.getUserData()).win();
                break;
            case MarioBross.MARIO_HEAD_BIT | MarioBross.BRICK_BIT:
                case MarioBross.MARIO_HEAD_BIT | MarioBross.COIN_BIT:
                    if (fixA.getFilterData().categoryBits == MarioBross.MARIO_HEAD_BIT)
                        ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                    else
                        ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;

            case MarioBross.ENEMY_HEAD_BIT | MarioBross.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;
            case MarioBross.ENEMY_BIT | MarioBross.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBross.MARIO_BIT | MarioBross.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                    ((Mario) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
            case MarioBross.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
            case MarioBross.ITEM_BIT | MarioBross.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBross.ITEM_BIT | MarioBross.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioBross.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Mario) fixA.getUserData());
                break;

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
