package com.cyberbot.bomberman.core.models.tiles.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.cyberbot.bomberman.core.models.tiles.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

public class TileMapFactory {
    private static final String TAG = TileMapFactory.class.getSimpleName();

    private static final String LAYER_BASE = "base";
    private static final String LAYER_FLOOR = "floor";
    private static final String LAYER_WALLS = "walls";

    /**
     * Factory method used to load {@link TileMap TileMap} with {@link DocumentBuilder DOM parser}.
     *
     * @param world the world to bind the body to.
     * @param path map folder path. Should contain map.xml and tileset.xml files.
     * @param mapFileName name of map.xml file located in path.
     * @return created {@link TileMap TileMap}.
     * @throws IOException missing file or read error.
     * @throws MissingLayersException missing essential map layers.
     * @throws ParserConfigurationException {@link DocumentBuilder DOM parser} exception.
     * @throws SAXException {@link DocumentBuilder DOM parser} exception.
     */
    public static TileMap createTileMap(World world, String path, String mapFileName) throws IOException,
        MissingLayersException, ParserConfigurationException, SAXException {
        File mapFile = new File(path + mapFileName);

        Element sourceMap = (Element) loadXmlMap(mapFile);
        Element tilesetSourceElement = (Element) sourceMap.getElementsByTagName("tileset").item(0);

        File tilesetFile = new File(path + tilesetSourceElement.getAttribute("source"));

        NodeList tileset = loadXmlTileset(tilesetFile);

        TileMapLayer baseLayer = null;
        TileMapLayer floorLayer = null;
        TileMapLayer wallsLayer = null;

        NodeList layers = sourceMap.getElementsByTagName("layer");

        for (int i = 0; i < layers.getLength(); i++) {
            Element element = (Element) layers.item(i);

            Tile[][] tiles = csvStringToTileArray(element.getElementsByTagName("data").item(0).getTextContent(),
                Integer.parseInt(element.getAttribute("width")),
                Integer.parseInt(element.getAttribute("height")),
                tileset,
                world);

            TileMapLayer mapLayer = new TileMapLayer(Integer.parseInt(element.getAttribute("width")),
                Integer.parseInt(element.getAttribute("height")), tiles);
            switch (element.getAttribute("name")) {
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
                        element.getAttribute("name") + "', will be ignored");
            }
        }

        return new TileMap(baseLayer, floorLayer, wallsLayer);
    }

    /**
     * Loads map file.
     *
     * @param inputFile map.xml file
     * @return map.xml root {@link Node Node}
     * @throws IOException missing file or read error.
     * @throws ParserConfigurationException {@link DocumentBuilder DOM parser} exception.
     * @throws SAXException {@link DocumentBuilder DOM parser} exception.
     */
    private static Node loadXmlMap(File inputFile) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputFile);
        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    /**
     * Loads tileset file.
     *
     * @param inputFile tileset.xml file.
     * @return {@link NodeList NodeList} containing elements named "tile"
     * @throws IOException missing file or read error.
     * @throws ParserConfigurationException {@link DocumentBuilder DOM parser} exception.
     * @throws SAXException {@link DocumentBuilder DOM parser} exception.
     */
    private static NodeList loadXmlTileset(File inputFile) throws IOException, ParserConfigurationException,
        SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputFile);
        document.getDocumentElement().normalize();

        return document.getElementsByTagName("tile");
    }

    /**
     * Parses CSV map String and builds {@link Tile Tile[][]}.
     *
     * @param input map CSV String.
     * @param width map width.
     * @param height map height.
     * @param tileset {@link NodeList NodeList} of tiles.
     * @param world the world to bind the body to.
     * @return {@link Tile Tile[][]}.
     * @throws InvalidPropertiesFormatException thrown on misdefined tile properties.
     */
    private static Tile[][] csvStringToTileArray(String input, int width, int height, NodeList tileset, World world)
        throws InvalidPropertiesFormatException {
        String[] mapStringArray = input.trim().replaceAll("\n", "").split(",");
        Tile[][] tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = TileFactory.createTile(getTileById(tileset, mapStringArray[(height - y - 1) * width + x]),
                    world, x, y);
            }
        }

        return tiles;
    }

    private static NodeList getTileById(NodeList tileset, String id) {
        int tileId = Integer.parseInt(id) - 1;
        for (int i = 0; i < tileset.getLength(); i++) {
            Element element = (Element) tileset.item(i);

            if (Integer.parseInt(element.getAttribute("id")) == tileId) {
                Node properties = element.getElementsByTagName("properties").item(0);
                Element element1 = (Element) properties;
                return element1.getElementsByTagName("property");
            }
        }

        return null;
    }
}
