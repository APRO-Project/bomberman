package com.cyberbot.bomberman.core.models.tiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.core.models.net.data.PhysicalTileData;

/**
 * An abstract base class for tiles that should contain a Box2D body.
 */
public abstract class PhysicalTile extends Tile implements Disposable {
    protected Body body;

    public PhysicalTile(World world, String textureName, int x, int y) {
        super(textureName, x, y);

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(new Vector2(x + 0.5f, y + 0.5f));
        def.fixedRotation = true;

        body = world.createBody(def);
        body.setUserData(this);

        createFixture();
    }

    /**
     * Implementations of this method should create an appropriate fixture and assign it to the body.
     */
    protected abstract void createFixture();

    public abstract PhysicalTileData<?> getData();

    @Override
    public void dispose() {
        body.getWorld().destroyBody(body);
    }
}
