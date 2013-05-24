package org.glowacki.core.dungen;

import java.io.PrintStream;

/**
 * A character representation of a set of rooms
 */
public class CharMap
{
    private static final char DOOR = '+';
    private static final char DOWNSTAIRS = '>';
    private static final char EMPTY = ' ';
    private static final char FLOOR = '.';
    private static final char SIDEWALL = '|';
    private static final char TUNNEL = '#';
    private static final char UPSTAIRS = '<';
    private static final char WALL = '-';

    private char[][] map;

    CharMap(int width, int height)
    {
        map = new char[width][height];
        clearMap();
    }

    CharMap(IRoom[] rooms)
    {
        this(rooms, false);
    }

    /**
     * Create a character-based map
     *
     * @param rooms array of room descriptions
     * @param addLabel if <tt>true</tt> add a letter designation to
     *                 the center of each room
     */
    public CharMap(IRoom[] rooms, boolean addLabel)
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
        addRooms(rooms, addLabel);
    }

    /**
     * Add a door
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws GeneratorException if the current terrain is not a wall
     */
    public void addDoor(int x, int y)
        throws GeneratorException
    {
        if (map[x][y] != WALL && map[x][y] != SIDEWALL) {
            final String msg =
                String.format("Cannot add door at %d,%d, '%c' is not a wall\n",
                              x, y, map[x][y]);
            throw new GeneratorException(msg);
        }

        map[x][y] = DOOR;
    }

    private void addRooms(IRoom[] rooms, boolean addLabel)
    {
        for (int i = 0; i < rooms.length; i++) {
            addRoom(rooms[i], addLabel);
        }
    }

    private void addRoom(IRoom room, boolean addLabel)
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

        if (addLabel) {
            final int cx = room.getX() + (room.getWidth() / 2);
            final int cy = room.getY() + (room.getHeight() / 2);
            map[cx][cy] = room.getChar();
        }
    }

    /**
     * Add a staircase
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param isUp if <tt>true</tt>, add an up staircase.
     *             otherwise add down staircase
     *
     * @throws GeneratorException if the current terrain is not a floor
     */
    public void addStaircase(int x, int y, boolean isUp)
        throws GeneratorException
    {
        if (map[x][y] != FLOOR) {
            final String msg =
                String.format("Cannot add staircase at %d,%d," +
                              " '%c' is not a floor\n", x, y, map[x][y]);
            throw new GeneratorException(msg);
        }

        map[x][y] = isUp ? UPSTAIRS : DOWNSTAIRS;
    }

    private void clearMap()
    {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                map[x][y] = EMPTY;
            }
        }
    }

    /**
     * Get the map as an array of strings
     *
     * @return array of strings depicting the map
     */
    public String[] getStrings()
    {
        String[] strMap = new String[map[0].length];

        StringBuilder buf = new StringBuilder(map.length);
        for (int y = 0; y < map[0].length; y++) {
            buf.setLength(0);
            for (int x = 0; x < map.length; x++) {
                buf.append(map[x][y]);
            }
            strMap[y] = buf.toString();
        }
        return strMap;
    }

    void set(int x, int y, char ch)
        throws GeneratorException
    {
        if (map[x][y] != EMPTY && map[x][y] != ch) {
            final String msg =
                String.format("Overwriting '%c' at %d,%d with '%c'\n",
                              map[x][y], x, y, ch);
            throw new GeneratorException(msg);
        }

        map[x][y] = ch;
    }

    /**
     * Write the map to System.out
     */
    public void show()
    {
        show(System.out);
    }

    /**
     * Write the map to the output file/device
     *
     * @param out output file/device
     */
    public void show(PrintStream out)
    {
        showMap(map, out);
    }

    /**
     * Write the map to System.out
     *
     * @param map map characters
     */
    static void showMap(char[][] map)
    {
        showMap(map, System.out);
    }

    /**
     * Write the map to the output file/device
     *
     * @param map map characters
     * @param out output file/device
     */
    static void showMap(char[][] map, PrintStream out)
    {
        StringBuilder buf = new StringBuilder(map.length);
        for (int y = 0; y < map[0].length; y++) {
            buf.setLength(0);
            for (int x = 0; x < map.length; x++) {
                buf.append(map[x][y]);
            }
            out.println(buf.toString());
        }
    }

    /**
     * Dig a tunnel
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws GeneratorException if the current terrain is not tunnelable
     */
    public void tunnel(int x, int y)
        throws GeneratorException
    {
        if (map[x][y] != EMPTY && map[x][y] != TUNNEL) {
            final String msg =
                String.format("Cannot add tunnel at %d,%d," +
                              " '%c' is not empty\n", x, y, map[x][y]);
            throw new GeneratorException(msg);
        }

        map[x][y] = TUNNEL;
    }
}
