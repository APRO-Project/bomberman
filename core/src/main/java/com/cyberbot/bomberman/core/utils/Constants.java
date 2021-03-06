package com.cyberbot.bomberman.core.utils;

public final class Constants {
    /**
     * Pixels per meter (unit). Determines the scaling between Box2D simulation and drawn pixels.
     * Usually set to the pixel size of a single map tile texture.
     *
     * @see <a href="https://seanballais.github.io/blog/box2d-and-the-pixel-per-meter-ratio/">Box2D and the Pixel Per Meter Ratio</a>
     */
    public static final float PPM = 24;

    /**
     * An animation speed multiplier for all animations (excluding real time based animations,
     * such as bomb fuse animations)
     */
    public static final float ANIMATION_SPEED = 1f;

    /**
     * How many times per second the user's input should be polled and
     * how many times per second the server simulates the world.
     */
    public static final int SIM_RATE = 60;

    /**
     * How many times per second a snapshot is sent over the network.
     */
    public static final int TICK_RATE = 60;

    public static final int LOBBY_ID_LENGTH = 5;
}
