package org.glowacki.core.test;

import java.util.ArrayList;
import java.util.List;

import org.glowacki.core.Direction;
import org.glowacki.core.IMap;
import org.glowacki.core.IMapObject;
import org.glowacki.core.IMapPoint;
import org.glowacki.core.MapEntry;
import org.glowacki.core.MapException;
import org.glowacki.core.Terrain;
import org.glowacki.core.UnimplementedError;

class MapPoint
    implements IMapPoint
{
    private int x;
    private int y;

    MapPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String toString()
    {
        return String.format("[%d,%d]", x, y);
    }
}

public class MockMap
    implements IMap
{
    private int maxX;
    private int maxY;
    private Terrain terrain = Terrain.UNKNOWN;
    private MapPoint upStaircase;
    private MapPoint downStaircase;
    private List<MapEntry> entries;

    public MockMap(int maxX, int maxY)
    {
        this.maxX = maxX;
        this.maxY = maxY;
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
        if (upStaircase == null) {
            throw new MapException("Map has no up staircase");
        }

        obj.setPosition(upStaircase.getX(), upStaircase.getY());

        return upStaircase;
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
        if (downStaircase == null) {
            throw new MapException("Map has no down staircase");
        }

        obj.setPosition(downStaircase.getX(), downStaircase.getY());

        return downStaircase;
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
        throw new UnimplementedError();
    }

    /**
     * Iterate through all map entries (used for path-finding).
     *
     * @return entry iterator
     */
    public Iterable<MapEntry> getEntries()
    {
        if (entries == null) {
            ArrayList<MapEntry> list = new ArrayList<MapEntry>();

            for (int y = 0; y <= maxY; y++) {
                for (int x = 0; x <= maxX; x++) {
                    MapEntry entry;
                    if (upStaircase != null && x == upStaircase.getX() &&
                        y == upStaircase.getY())
                    {
                        entry = new MapEntry(x, y, Terrain.UPSTAIRS);
                    } else if (downStaircase != null &&
                               x == downStaircase.getX() &&
                               y == downStaircase.getY())
                    {
                        entry = new MapEntry(x, y, Terrain.DOWNSTAIRS);
                    } else {
                        entry = new MapEntry(x, y, terrain);
                    }

                    list.add(entry);
                }
            }

            entries = list;
        }

        return entries;
    }

    public int getMaxX()
    {
        return maxX;
    }

    public int getMaxY()
    {
        return maxY;
    }

    /**
     * Get the object which occupies the specified position
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>null</tt> if no object is at the specified position
     */
    public IMapObject getOccupant(int x, int y)
        throws MapException
    {
        throw new UnimplementedError();
    }

    /**
     * Get a graphic representation of this level.
     *
     * @return string representation of level with embedded newlines
     */
    public String getPicture()
    {
        throw new UnimplementedError();
    }

    public Terrain getTerrain(int x, int y)
        throws MapException
    {
        if (downStaircase != null && x == downStaircase.getX() &&
            y == downStaircase.getY())
        {
            return Terrain.DOWNSTAIRS;
        }

        if (upStaircase != null && x == upStaircase.getX() &&
            y == upStaircase.getY())
        {
            return Terrain.UPSTAIRS;
        }

        return terrain;
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
    public void insertObject(IMapObject obj, int x, int y)
        throws MapException
    {
        obj.setPosition(x, y);
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
        return false;
    }

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

    public void moveTo(IMapObject obj, int x, int y)
        throws MapException
    {
        obj.setPosition(x, y);
    }


    /**
     * Remove this object from the map.
     *
     * @param obj object to remove
     *
     * @throws MapException if there is a problem
     */
    public void removeObject(IMapObject obj)
        throws MapException
    {
        obj.setPosition(-1, -1);
    }

    public void setDownStaircase(int x, int y)
    {
        downStaircase = new MapPoint(x, y);
    }

    public void setUpStaircase(int x, int y)
    {
        upStaircase = new MapPoint(x, y);
    }

    public void setTerrain(Terrain t)
    {
        terrain = t;
    }
}
