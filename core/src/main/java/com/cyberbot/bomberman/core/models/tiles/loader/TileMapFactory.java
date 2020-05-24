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


    public static TileMap createTileMap(World world, String path) throws IOException, MissingLayersException, ParserConfigurationException, SAXException {

        Node sourceMap = loadXmlMap(path);
        Element mapElement = (Element) sourceMap;
        NodeList tilesetSource = mapElement.getElementsByTagName("tileset");
        Element tilesetSourceElement = (Element) tilesetSource.item(0);

        NodeList tileset = loadXmlTileset("./map/" + tilesetSourceElement.getAttribute("source"));

        TileMapLayer baseLayer = null;
        TileMapLayer floorLayer = null;
        TileMapLayer wallsLayer = null;


        NodeList layers = mapElement.getElementsByTagName("layer");

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

    private static Node loadXmlMap(String path) throws IOException, ParserConfigurationException, SAXException {

        File inputFile = new File(path);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputFile);
        document.getDocumentElement().normalize();
        return document.getDocumentElement();
    }

    private static NodeList loadXmlTileset(String path) throws IOException, ParserConfigurationException, SAXException {

        File inputFile = new File(path);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputFile);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("tile");
    }

    private static Tile[][] csvStringToTileArray(String input, int width, int height, NodeList tileset, World world)
        throws InvalidPropertiesFormatException {
        String[] mapStringArray = input.trim().replaceAll("\n", "").split(",");
        Tile[][] tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x][y] = TileFactory.createTile(getTileById(tileset, mapStringArray[(height - y - 1) * width + x]),
                    world,
                    x,
                    y);
            }
        }
        return tiles;
    }

    private static NodeList getTileById(NodeList tileset, String id) {
        for (int i = 0; i < tileset.getLength(); i++) {
            Node node = tileset.item(i);
            Element element = (Element) node;
            int tileId = Integer.parseInt(id) - 1;
            if (Integer.parseInt(element.getAttribute("id")) == tileId) {
                Node properties = element.getElementsByTagName("properties").item(0);
                Element element1 = (Element) properties;
                return element1.getElementsByTagName("property");
            }
        }
        return null;
    }
}
