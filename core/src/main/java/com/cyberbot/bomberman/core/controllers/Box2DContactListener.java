package com.cyberbot.bomberman.core.controllers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Convenience empty defult implementation of {@link ContactListener}
 */
public interface Box2DContactListener extends ContactListener {
    @Override
    default void beginContact(Contact contact) {
    }

    @Override
    default void endContact(Contact contact) {
    }

    @Override
    default void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    default void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
