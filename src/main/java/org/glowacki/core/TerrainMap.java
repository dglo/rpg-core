package org.glowacki.core;

/**
 * Exceptions for this class.
 */
class TerrainMapException
    extends CoreException
{
    /**
     * Create a terrain map exception.
     *
     * @param msg error message
     */
    TerrainMapException(String msg)
    {
        super(msg);
    }
}

public class TerrainMap
{
    private Terrain[][] map;

    /**
     * Build a terrain map.
     *
     * @param rawMap map of Strings describing the level
     *
     * @throws TerrainMapException if the raw map is not valid
     */
    public TerrainMap(String[] rawMap)
        throws TerrainMapException
    {
        if (rawMap == null || rawMap.length == 0 || rawMap[0] == null ||
            rawMap[0].length() == 0)
        {
            if (rawMap == null) {
                throw new TerrainMapException("Null map");
            } else {
                String hgt = Integer.toString(rawMap.length);
                String wid = "?";
                if (rawMap.length > 0 && rawMap[0] != null) {
                    wid = Integer.toString(rawMap[0].length());
                }
                throw new TerrainMapException("Bad map dimensions [" + hgt +
                                              ", " + wid + "]");
            }
        }

        int width = 0;
        for (int i = 0; i < rawMap.length; i++) {
            if (rawMap[i] != null && rawMap[i].length() > width) {
                width = rawMap[i].length();
            }
        }

        map = new Terrain[rawMap.length][width];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Terrain t;
                if (rawMap[y] == null || x >= rawMap[y].length()) {
                    t = Terrain.UNKNOWN;
                } else {
                    t = Terrain.getTerrain(rawMap[y].charAt(x));
                }
                map[y][x] = t;
            }
        }
    }

    /**
     * Find the first occurrence of the specified terrain.
     *
     * @param t terrain to find
     *
     * @return null if the terrain cannot be found on this level
     */
    public Point find(Terrain t)
    {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == t) {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }

    /**
     * Get the terrain found at the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return terrain at the specified point
     *
     * @throws TerrainMapException if the point is not valid
     */
    public Terrain get(int x, int y)
        throws TerrainMapException
    {
        if (y < 0 || y >= map.length) {
            throw new TerrainMapException("Bad Y coordinate in (" + x + "," +
                                          y + "), max is " + getMaxY());
        } else if (x < 0 || x >= map[y].length) {
            throw new TerrainMapException("Bad X coordinate in (" + x + "," +
                                          y + "), max is " + getMaxX());
        }

        return map[y][x];
    }

    /**
     * Get maximum X coordinate for this level.
     *
     * @return maximum addressable X coordinate
     */
    public int getMaxX()
    {
        return map[0].length - 1;
    }

    /**
     * Get maximum Y coordinate for this level.
     *
     * @return maximum addressable Y coordinate
     */
    public int getMaxY()
    {
        return map.length - 1;
    }

    /**
     * Get a graphic representation of this level.
     *
     * @return string representation of level with embedded newlines
     */
    public String getPicture()
    {
        StringBuilder buf = new StringBuilder();

        for (int y = 0; y < map.length; y++) {
            if (y > 0) {
                buf.append('\n');
            }

            for (int x = 0; x < map[y].length; x++) {
                char ch;
                if (map[y][x] != Terrain.WALL) {
                    ch = Terrain.getCharacter(map[y][x]);
                } else {
                    if ((x > 0 && map[y][x - 1] == Terrain.WALL) ||
                        (x < map[y].length - 1 &&
                         map[y][x + 1] == Terrain.WALL))
                    {
                        ch = '-';
                    } else {
                        ch = '|';
                    }
                }

                buf.append(ch);
            }
        }

        return buf.toString();
    }
}
