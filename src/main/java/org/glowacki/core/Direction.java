package org.glowacki.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Directions
 */
public enum Direction
{
    /** left */
    LEFT,
    /** upper left */
    LEFT_UP,
    /** up */
    UP,
    /** upper right */
    RIGHT_UP,
    /** right */
    RIGHT,
    /** lower right */
    RIGHT_DOWN,
    /** down */
    DOWN,
    /** lower left */
    LEFT_DOWN,
    /** climb */
    CLIMB,
    /** descend */
    DESCEND,
    /** unknown */
    UNKNOWN;

    private static final Random RANDOM = new Random();
    private static final List<Direction> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    /**
     * Get the next cardinal direction
     *
     * @return cardinal direction
     */
    public Direction next()
    {
        Direction[] vals = values();
        return vals[(ordinal() + 1) % 8];
    }

    /**
     * Get a random cardinal direction
     *
     * @return cardinal direction
     */
    public static Direction random()
    {
        return VALUES.get(RANDOM.nextInt(8));
    }
}
