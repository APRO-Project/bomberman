package com.cyberbot.bomberman.models.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.cyberbot.bomberman.models.Drawable;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

import static com.cyberbot.bomberman.utils.Constants.PPM;

public class TileMap implements Disposable, Drawable {
    private TiledMap sourceMap;
    private List<TileMapLayer> layers;

    public TileMap(World world, String path) throws InvalidPropertiesFormatException {
        sourceMap = new TmxMapLoader().load(path);

        layers = new ArrayList<>();
        for (MapLayer layer : sourceMap.getLayers()) {
            if(layer instanceof TiledMapTileLayer) {
                layers.add(new TileMapLayer((TiledMapTileLayer) layer, world));
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        layers.forEach(t -> t.draw(batch));
    }

    @Override
    public void dispose() {
        sourceMap.dispose();
        layers.forEach(TileMapLayer::dispose);
    }

    public void bindToWorld(World world, String layer) {
        MapLayer mapLayer = sourceMap.getLayers().get(layer);
        for (MapObject object : mapLayer.getObjects()) {
            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object);
            } else {
                continue;
            }

            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(def);
            body.createFixture(shape, 1);

            shape.dispose();
        }
    }

    // Methods from https://gamedev.stackexchange.com/a/70448
    // Modified by RouNdeL
    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();

        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / PPM,
                (rectangle.y + rectangle.height * 0.5f) / PPM);

        polygon.setAsBox(rectangle.width * 0.5f / PPM,
                rectangle.height * 0.5f / PPM, size, 0.0f);

        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / PPM);
        circleShape.setPosition(new Vector2(circle.x / PPM, circle.y / PPM));

        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();
        scaleVertices(vertices);

        PolygonShape polygon = new PolygonShape();
        polygon.set(vertices);

        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        scaleVertices(vertices);

        ChainShape chain = new ChainShape();
        chain.createChain(vertices);

        return chain;
    }

    private static void scaleVertices(float[] vertices) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] /= PPM;
        }
    }
}
