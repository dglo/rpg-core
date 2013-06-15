package org.glowacki.core;

import org.glowacki.core.fov.IVisibilityMap;
import org.glowacki.core.fov.ShadowCasting;

/**
 * Map of all visible cells.
 */
public class VisibleMap
{
    private IMap map;
    private ShadowCasting algorithm;

    private int vx = Integer.MIN_VALUE;
    private int vy = Integer.MIN_VALUE;
    private int vdist = Integer.MIN_VALUE;
    private boolean[][] visible;

    /**
     * Create a map of the visible squares.
     *
     * @param map source map
     */
    public VisibleMap(IMap map)
    {
        this.map = map;

        algorithm = new ShadowCasting();
    }

    /**
     * Get a graphic representation of the visible portion of this map.
     *
     * @param visible array returned by buildMap
     *
     * @return string representation of map with embedded newlines
     */
    public static String getPicture(boolean[][] visible)
    {
        StringBuilder buf = new StringBuilder();

        for (int y = 0; y < visible[0].length; y++) {
            if (y > 0) {
                buf.append('\n');
            }

            for (int x = 0; x < visible.length; x++) {
                char ch;
                if (visible[x][y]) {
                    ch = '*';
                } else {
                    ch = '.';
                }

                buf.append(ch);
            }
        }

        return buf.toString();
    }

    /**
     * Generate the map of visible cells.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param distance distance a cell can be seen
     *
     * @return map (in [x][y] order) set to <tt>true</tt> if cell is visible
     */
    public boolean[][] getVisible(int x, int y, int distance)
    {
        // only recalculate when position changes
        if (x != vx || y != vy || distance != vdist) {
            TemporaryMap tmp = new TemporaryMap();

            algorithm.findVisible(tmp, x, y, distance);

            visible = tmp.getVisible();

            vx = x;
            vy = y;
            vdist = distance;
        }

        return visible;
    }

    /**
     * Temporary map used to generate the boolean visibility array.
     */
    class TemporaryMap
        implements IVisibilityMap
    {
        private boolean[][] visible;

        /**
         * Create a temporary map
         */
        TemporaryMap()
        {
            visible = new boolean[map.getMaxX() + 1][map.getMaxY() + 1];
        }

        /**
         * Is the specified inside the map?
         *
         * @param x X coordinate
         * @param y Y coordinate
         *
         * @return <tt>true</tt> if there is an obstacle at the specified point
         */
        public boolean contains(int x, int y)
        {
            return (x >= 0 && x <= map.getMaxX()) &&
                (y >= 0 && y <= map.getMaxY());
        }

        /**
         * Get the map of visible cells.
         *
         * @return map of visible cells
         */
        boolean[][] getVisible()
        {
            return visible;
        }

        /**
         * Is the specified point obstructed?
         *
         * @param x X coordinate
         * @param y Y coordinate
         *
         * @return <tt>true</tt> if the specified point is obstructed
         */
        public boolean isObstructed(int x, int y)
        {
            try {
                if (map.isOccupied(x, y)) {
                    return true;
                }
            } catch (MapException me) {
                return true;
            }

            Terrain t;
            try {
                t = map.getTerrain(x, y);
            } catch (MapException me) {
                return true;
            }

            if (t == Terrain.WALL || t == Terrain.UNKNOWN) {
                return true;
            }

            return false;
        }

        /**
         * Mark the specified point as visible
         *
         * @param x X coordinate
         * @param y Y coordinate
         */
        public void setVisible(int x, int y)
        {
            visible[x][y] = true;
        }
    }
}
