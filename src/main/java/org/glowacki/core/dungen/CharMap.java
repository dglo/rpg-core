package org.glowacki.core.dungen;

/**
 * A character representation of a set of rooms
 */
public class CharMap
{
    private static final char EMPTY = ' ';
    private static final char FLOOR = '.';
    private static final char SIDEWALL = '|';
    private static final char WALL = '-';

    private char[][] map;

    CharMap(int width, int height)
    {
        map = new char[width][height];
        clearMap();
    }

    CharMap(Room[] rooms)
    {
        int width = 0;
        int height = 0;
        for (int i = 0; i < rooms.length; i++) {
            final int rWidth = rooms[i].getX() + rooms[i].getWidth();
            if (rWidth > width) {
                width = rWidth;
            }

            final int rHeight = rooms[i].getY() + rooms[i].getHeight();
            if (rHeight > height) {
                height = rHeight;
            }
        }

        map = new char[width][height];
        clearMap();
        addRooms(rooms);
    }

    void addRooms(Room[] rooms)
    {
        for (int i = 0; i < rooms.length; i++) {
            addRoom(rooms[i]);
        }
    }

    void addRoom(Room room)
    {
        final int left = room.getX();
        final int top = room.getY();
        final int right = left + room.getWidth() - 1;
        final int bottom = top + room.getHeight() - 1;

        boolean overlapError = false;
        for (int xx = left; xx <= right; xx++) {
            for (int yy = top; yy <= bottom; yy++) {
                if (map[xx][yy] != EMPTY && map[xx][yy] != WALL &&
                    map[xx][yy] != SIDEWALL && !overlapError)
                {
                    System.out.format("Room %s overwrites data at %d,%d%n",
                                      room, xx, yy);
                    overlapError = true;
                }

                if (yy == top || yy == bottom) {
                    map[xx][yy] = WALL;
                } else if (xx == left || xx == right) {
                    map[xx][yy] = SIDEWALL;
                } else {
                    map[xx][yy] = FLOOR;
                }
            }
        }

        final int cx = room.getX() + (room.getWidth() / 2);
        final int cy = room.getY() + (room.getHeight() / 2);
        map[cx][cy] = room.getChar();
    }

    private void clearMap()
    {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = EMPTY;
            }
        }
    }

    void show()
    {
        showMap(map);
    }

    static void showMap(char[][] map)
    {
        StringBuilder buf = new StringBuilder(map.length);
        for (int y = 0; y < map[0].length; y++) {
            buf.setLength(0);
            for (int x = 0; x < map.length; x++) {
                buf.append(map[x][y]);
            }
            System.out.println(buf.toString());
        }
    }
}
