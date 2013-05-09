package org.glowacki.core;

import java.util.Iterator;

/**
 * Exception returned if a position is occupied
 */
class OccupiedException
    extends MapException
{
    OccupiedException()
    {
        super();
    }

    OccupiedException(String msg)
    {
        super(msg);
    }
}

/**
 * Map.
 */
public class Map
    implements IMap
{
    private MapEntry[][] map;

    /**
     * Create a map.
     *
     * @param template mape template
     *
     * @throws MapException if there is a problem
     */
    public Map(String[] template)
        throws MapException
    {
        if (template == null) {
            throw new MapException("Map template cannot be null");
        }

        int maxLen = 0;
        for (int y = 0; y < template.length; y++) {
            if (template[y] != null && template[y].length() > maxLen) {
                maxLen = template[y].length();
            }
        }

        if (maxLen == 0) {
            throw new MapException("Map template cannot be empty");
        }

        map = new MapEntry[template.length][maxLen];

        for (int y = 0; y < template.length; y++) {
            int x;
            for (x = 0; template[y] != null && x < template[y].length();
                 x++)
            {
                Terrain t =
                    MapCharRepresentation.getTerrain(template[y].charAt(x));
                map[y][x] = new MapEntry(x, y, t);
            }
            for ( ; x < maxLen; x++) {
                map[y][x] = new MapEntry(x, y, Terrain.UNKNOWN);
            }
        }
    }

    /**
     * Add this object to the map at the up staircase.
     *
     * @param obj object
     *
     * @return location of up staircase
     *
     * @throws MapException if there is a problem
     */
    public IMapPoint enterDown(IMapObject obj)
        throws MapException
    {
        MapEntry entry = find(Terrain.UPSTAIRS);
        if (entry == null) {
            throw new MapException("Map has no up staircase");
        }

        if (entry.getObject() != null) {
            throw new OccupiedException("Up staircase is occupied");
        }

        entry.setObject(obj);
        obj.setPosition(entry.getX(), entry.getY());

        return entry;
    }

    /**
     * Add this object to the map at the down staircase.
     *
     * @param obj object
     *
     * @return location of down staircase
     *
     * @throws MapException if there is a problem
     */
    public IMapPoint enterUp(IMapObject obj)
        throws MapException
    {
        MapEntry entry = find(Terrain.DOWNSTAIRS);
        if (entry == null) {
            throw new MapException("Map has no down staircase");
        }

        if (entry.getObject() != null) {
            throw new OccupiedException("Down staircase is occupied");
        }

        entry.setObject(obj);
        obj.setPosition(entry.getX(), entry.getY());

        return entry;
    }

    /**
     * Find the first occurrence of the specified terrain.
     *
     * @param t terrain to find
     *
     * @return null if the terrain cannot be found on this level
     */
    public MapEntry find(Terrain t)
    {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x].getTerrain() == t) {
                    return map[y][x];
                }
            }
        }

        return null;
    }

    /**
     * Iterate through all map entries (used for path-finding).
     *
     * @return entry iterator
     */
    public Iterable<MapEntry> getEntries()
    {
        return new EntryIterable();
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
                if (map[y][x].getTerrain() != Terrain.WALL) {
                    Terrain t = map[y][x].getTerrain();
                    ch = MapCharRepresentation.getCharacter(t);
                } else {
                    if ((x > 0 &&
                         map[y][x - 1].getTerrain() == Terrain.WALL) ||
                        (x < map[y].length - 1 &&
                         map[y][x + 1].getTerrain() == Terrain.WALL))
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

    /**
     * Get the terrain found at the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return terrain at the specified point
     *
     * @throws MapException if the point is not valid
     */
    public Terrain getTerrain(int x, int y)
        throws MapException
    {
        if (y < 0 || y >= map.length) {
            throw new MapException("Bad Y coordinate in (" + x + "," +
                                   y + "), max is " + getMaxY());
        } else if (x < 0 || x >= map[y].length) {
            throw new MapException("Bad X coordinate in (" + x + "," +
                                   y + "), max is " + getMaxX());
        }

        return map[y][x].getTerrain();
    }

    /**
     * Insert this object into the map.
     *
     * @param obj object to add
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws MapException if there is a problem
     */
    void insertObject(IMapObject obj, int x, int y)
        throws MapException
    {
        if (y < 0 || y >= map.length || x < 0 || x >= map[0].length) {
            final String msg =
                String.format("Bad insert position [%d,%d] for %s", x, y,
                              obj.getName());
            throw new MapException(msg);
        }

        MapEntry entry = map[y][x];
        if (entry.getObject() != null) {
            final String msg =
                String.format("%s is at [%d, %d]", entry.getObject(), x, y);
            throw new OccupiedException(msg);
        }

        Terrain t = entry.getTerrain();
        if (!t.isMovable()) {
            final String msg =
                String.format("Terrain %s at [%d,%d] is not movable", t, x, y);
            throw new MapException(msg);
        }

        entry.setObject(obj);
    }

    /**
     * Is the specified point occupied?
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>true</tt> if this point is occupied
     *
     * @throws MapException if there is a problem
     */
    public boolean isOccupied(int x, int y)
        throws MapException
    {
        if (y < 0 || y >= map.length) {
            throw new MapException("Bad Y coordinate in (" + x + "," +
                                   y + "), max is " + getMaxY());
        } else if (x < 0 || x >= map[y].length) {
            throw new MapException("Bad X coordinate in (" + x + "," +
                                   y + "), max is " + getMaxX());
        }

        return map[y][x].getObject() != null;
    }

    /**
     * Move object in the specified direction.
     *
     * @param obj object being moved
     * @param dir direction
     *
     * @throws MapException if there is a problem
     */
    public void moveDirection(IMapObject obj, Direction dir)
        throws MapException
    {
        int newX = obj.getX();
        int newY = obj.getY();

        boolean moved = true;
        if (dir == Direction.LEFT_UP || dir == Direction.LEFT ||
            dir == Direction.LEFT_DOWN)
        {
            newX -= 1;
            moved &= (newX >= 0);
        } else if (dir == Direction.RIGHT_UP || dir == Direction.RIGHT ||
                   dir == Direction.RIGHT_DOWN)
        {
            newX += 1;
            moved &= (newX <= getMaxX());
        }

        if (dir == Direction.LEFT_UP || dir == Direction.UP ||
            dir == Direction.RIGHT_UP)
        {
            newY -= 1;
            moved &= (newY >= 0);
        } else if (dir == Direction.LEFT_DOWN || dir == Direction.DOWN ||
                   dir == Direction.RIGHT_DOWN)
        {
            newY += 1;
            moved &= (newY <= getMaxY());
        }

        if (!moved) {
            throw new MapException(String.format("Cannot move %s to %s",
                                                 obj.getName(), dir));
        }

        moveTo(obj, newX, newY);
    }

    /**
     * Move the object to the specified position.
     *
     * @param obj object
     * @param x X position
     * @param y Y position
     *
     * @throws MapException if there is a problem
     */
    public void moveTo(IMapObject obj, int x, int y)
        throws MapException
    {
        final int oldX = obj.getX();
        final int oldY = obj.getY();

        // only try to remove object if position is valid
        if (oldX >= 0 && oldY >= 0) {
            removeObject(obj);
        }

        try {
            insertObject(obj, x, y);
        } catch (MapException me) {
            insertObject(obj, oldX, oldY);
            throw me;
        }
    }

    /**
     * Remove this object from the map.
     *
     * @param obj object to remove
     *
     * @throws MapException if there is a problem
     */
    void removeObject(IMapObject obj)
        throws MapException
    {
        if (obj.getY() < 0 || obj.getY() >= map.length ||
            obj.getX() < 0 || obj.getX() >= map[0].length)
        {
            final String msg =
                String.format("Bad current position [%d,%d] for %s",
                              obj.getX(), obj.getY(), obj.getName());
            throw new MapException(msg);
        }

        MapEntry entry = map[obj.getY()][obj.getX()];
        entry.clearObject(obj);
    }

    /**
     * Return a debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("%dx%d", map[0].length, map.length);
    }

    /**
     * MapEntry iterator.
     */
    class EntryIterable
        implements Iterable, Iterator
    {
        private int x;
        private int y;

        public boolean hasNext()
        {
            return y < map.length && x < map[y].length;
        }

        public java.util.Iterator<MapEntry> iterator()
        {
            return this;
        }

        public MapEntry next()
        {
            if (!hasNext()) {
                return null;
            }

            MapEntry entry = map[y][x++];

            if (x >= map[y].length) {
                x = 0;
                y++;
            }

            return entry;
        }

        public  void remove()
        {
            throw new UnimplementedError();
        }
    }
}
