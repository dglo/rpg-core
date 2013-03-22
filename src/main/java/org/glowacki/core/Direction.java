package org.glowacki.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Direction
{
    LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN,
        CLIMB, DESCEND, UNKNOWN;

    private static final Random RANDOM = new Random();
    private static final List<Direction> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    public Direction next()
    {
        Direction[] vals = values();
        return vals[(ordinal() + 1) % 8];
    }

    public static Direction random()
    {
        return VALUES.get(RANDOM.nextInt(8));
    }
}
