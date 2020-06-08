package com.cyberbot.bomberman.core.models.tiles;

public class MapLoadException extends Exception {
    public MapLoadException() {
    }

    public MapLoadException(String message) {
        super(message);
    }

    public MapLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapLoadException(Throwable cause) {
        super(cause);
    }
}
