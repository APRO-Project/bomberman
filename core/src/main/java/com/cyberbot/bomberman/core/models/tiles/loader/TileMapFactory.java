package com.cyberbot.bomberman.core.models.tiles.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.tiles.*;
import com.cyberbot.bomberman.core.models.tiles.loader.tileset.Tileset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TileMapFactory {
    private static final String TAG = TileMapFactory.class.getSimpleName();

    private static final String LAYER_BASE = "base";
    private static final String LAYER_FLOOR = "floor";
    private static final String LAYER_WALLS = "walls";


    public static TileMap createTileMap(World world, String path) throws JAXBException,
        FileNotFoundException, MissingLayersException {

        Map sourceMap = loadXmlMap(path);
        Tileset tileset = loadXmlTileset("./map/" + sourceMap.getTileset().getSource());

        TileMapLayer baseLayer = null;
        TileMapLayer floorLayer = null;
        TileMapLayer wallsLayer = null;

        for (Layer layer : sourceMap.getLayer()) {

            Tile[][] tiles = csvStringToTileArray(layer.getData().getContent(),
                layer.getWidth().intValue(),
                layer.getHeight().intValue(),
                tileset,
                world);

            TileMapLayer mapLayer = new TileMapLayer(layer.getWidth().intValue(), layer.getHeight().intValue(), tiles);
            switch (layer.getName()) {
                case LAYER_BASE:
                    baseLayer = mapLayer;
                    break;
                case LAYER_FLOOR:
                    floorLayer = mapLayer;
                    break;
                case LAYER_WALLS:
                    wallsLayer = mapLayer;
                    break;
                default:
                    Gdx.app.error(TAG, "Unsupported tile layer found: '" +
                        layer.getName() + "', will be ignored");
            }
        }

        return new TileMap(baseLayer, floorLayer, wallsLayer);
    }

    private static Map loadXmlMap(String path) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext;

        jaxbContext = JAXBContext.newInstance(Map.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();

        return (Map) um.unmarshal(new InputStreamReader(
            new FileInputStream(path), StandardCharsets.UTF_8)
        );
    }

    private static Tileset loadXmlTileset(String path) throws JAXBException, FileNotFoundException {

        JAXBContext jaxbContext;

        jaxbContext = JAXBContext.newInstance(Tileset.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();

        return (Tileset) um.unmarshal(new InputStreamReader(
            new FileInputStream(path), StandardCharsets.UTF_8)
        );
    }

    private static Tile[][] csvStringToTileArray(String input, int width, int height, Tileset tileset, World world) {
        String[] strings = input.trim().replaceAll("\n", "").split(",");
        Tile[][] tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = TileFactory.createTile(getTileById(tileset, strings[(height - y - 1) * width + x]), world, x, y);
            }
        }
        return tiles;
    }

    private static com.cyberbot.bomberman.core.models.tiles.loader.tileset.Tile getTileById(Tileset tileset, String id) {
        for (com.cyberbot.bomberman.core.models.tiles.loader.tileset.Tile tile : tileset.getTile()) {
            if (tile.getId().intValue() + 1 == Integer.parseInt(id)) {
                return tile;
            }
        }
        return null;
    }
}
