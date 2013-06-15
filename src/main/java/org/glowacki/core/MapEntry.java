package org.glowacki.core;

/**
 * A map entry.
 */
public class MapEntry
    implements IMapPoint
{
    private int x;
    private int y;
    private Terrain terrain;
    private IMapObject object;

    /**
     * Create a map entry.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param t terrain
     */
    public MapEntry(int x, int y, Terrain t)
    {
        this.x = x;
        this.y = y;
        this.terrain = t;
    }

    /**
     * Remove the object from this position.
     *
     * @param obj object to remove
     *
     * @throws MapException if the object is not in this entry
     */
    public void clearObject(IMapObject obj)
        throws MapException
    {
        if (object == null) {
            final String msg =
                String.format("Entry [%d, %d] does not contain %s", obj.getX(),
                              obj.getY(), obj.getName());
            throw new MapException(msg);
        } else if (!object.equals(obj)) {
            final String msg =
                String.format("Entry [%d, %d] contains %s, not %s", obj.getX(),
                              obj.getY(), object.getName(), obj.getName());
            throw new MapException(msg);
        }

        object = null;

        obj.clearPosition();
    }

    /**
     * Get the object occupying this entry.
     *
     * @return <tt>null</tt> if there is no object at this position
     */
    public IMapObject getObject()
    {
        return object;
    }

    /**
     * Get this entry's terrain.
     *
     * @return terrain
     */
    public Terrain getTerrain()
    {
        return terrain;
    }

    /**
     * Get this entry's X coordinate.
     *
     * @return X coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get this entry's Y coordinate.
     *
     * @return Y coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Set the object which occupies this entry.
     *
     * @param obj object
     *
     * @throws OccupiedException if this entry is occupied
     */
    public void setObject(IMapObject obj)
        throws OccupiedException
    {
        if (object != null) {
            final String msg =
                String.format("Cannot move %s to [%d,%d]; occupied by %s",
                              obj.getName(), x, y, object.getName());
            throw new OccupiedException(msg);
        }

        object = obj;
        object.setPosition(x, y);
    }

    /**
     * Return a debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("[%d,%d]", x, y);
    }
}
