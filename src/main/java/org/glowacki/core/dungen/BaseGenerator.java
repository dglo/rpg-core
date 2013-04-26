package org.glowacki.core.dungen;

import java.util.Random;

public abstract class BaseGenerator
{
    enum Direction {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM;

        static Direction get(int i)
        {
            switch (i % 4) {
            case 0:
                return LEFT;
            case 1:
                return TOP;
            case 2:
                return RIGHT;
            default:
                return BOTTOM;
            }
        }
    };

    public static void addStairs(Room[] rooms, Random random, boolean addUp,
                                 boolean addDown)
    {
        int up = -1;
        if (addUp) {
            up = random.nextInt(rooms.length);

            final int x = random.nextInt(rooms[up].getWidth() - 2) + 1;
            final int y = random.nextInt(rooms[up].getHeight() - 2) + 1;

            rooms[up].addStairs(x, y, true);
        }

        if (addDown) {
            int down;
            do {
                down = random.nextInt(rooms.length);
            } while (down == up);

            final int x = random.nextInt(rooms[down].getWidth() - 2) + 1;
            final int y = random.nextInt(rooms[down].getHeight() - 2) + 1;

            rooms[down].addStairs(x, y, false);
        }
    }

    private static void fixHorizontalNeighbors(Room left, Room right)
    {
        if (left.getWidth() < right.getWidth()) {
            left.changeWidth(1);
        } else {
            right.decX();
            right.changeWidth(1);
        }
    }

    static void fixNeighbors(Room[] rooms)
    {
        for (int i = 0; i < rooms.length - 1; i++) {
            for (int j = i; j < rooms.length; j++) {
                for (Direction d : Direction.values()) {
                    switch (d) {
                    case LEFT:
                        if (rooms[i].getX() ==
                            rooms[j].getX() + rooms[j].getWidth())
                        {
                            fixHorizontalNeighbors(rooms[j], rooms[i]);
                        }
                        break;
                    case TOP:
                        if (rooms[i].getY() ==
                            rooms[j].getY() + rooms[j].getHeight())
                        {
                            fixVerticalNeighbors(rooms[j], rooms[i]);
                        }
                        break;
                    case RIGHT:
                        if (rooms[j].getX() ==
                            rooms[i].getX() + rooms[i].getWidth())
                        {
                            fixHorizontalNeighbors(rooms[i], rooms[j]);
                        }
                        break;
                    default:
                        if (rooms[j].getY() ==
                            rooms[i].getY() + rooms[i].getHeight())
                        {
                            fixVerticalNeighbors(rooms[i], rooms[j]);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void fixVerticalNeighbors(Room top, Room bottom)
    {
        if (top.getHeight() < bottom.getHeight()) {
            top.changeHeight(1);
        } else {
            bottom.decY();
            bottom.changeHeight(1);
        }
    }
}
