package com.cyberbot.bomberman.core.models.tiles;

public class MissingLayersException extends Exception {
    public MissingLayersException() {
    }

    public MissingLayersException(String message) {
        super(message);
    }

    public MissingLayersException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingLayersException(Throwable cause) {
        super(cause);
    }
}
