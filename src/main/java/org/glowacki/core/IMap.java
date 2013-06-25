package org.glowacki.core;

/**
 * Map methods.
 */
public interface IMap
{
    /**
     * Add this object to the map at the up staircase.
     *
     * @param obj object
     *
     * @return location of up staircase
     *
     * @throws MapException if there is a problem
     */
    IMapPoint enterDown(IMapObject obj)
        throws MapException;

    /**
     * Add this object to the map at the down staircase.
     *
     * @param obj object
     *
     * @return location of down staircase
     *
     * @throws MapException if there is a problem
     */
    IMapPoint enterUp(IMapObject obj)
        throws MapException;

    /**
     * Find the first occurrence of the specified terrain.
     *
     * @param t terrain to find
     *
     * @return null if the terrain cannot be found on this level
     */
    MapEntry find(Terrain t);

    /**
     * Iterate through all map entries (used for path-finding).
     *
     * @return entry iterator
     */
    Iterable<MapEntry> getEntries();

    /**
     * Get the maximum X coordinate.
     *
     * @return maximum X coordinate
     */
    int getMaxX();

    /**
     * Get the maximum Y coordinate.
     *
     * @return maximum Y coordinate
     */
    int getMaxY();

    /**
     * Get the object which occupies the specified position
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>null</tt> if no object is at the specified position
     */
    IMapObject getOccupant(int x, int y)
        throws MapException;

    /**
     * Get a graphic representation of this level.
     *
     * @return string representation of level with embedded newlines
     */
    String getPicture();

    /**
     * Get the terrain at the specified point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return terrain
     *
     * @throws MapException if there is a problem
     */
    Terrain getTerrain(int x, int y)
        throws MapException;

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
        throws MapException;

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
    boolean isOccupied(int x, int y)
        throws MapException;

    /**
     * Move object in the specified direction.
     *
     * @param obj object being moved
     * @param dir direction
     *
     * @throws MapException if there is a problem
     */
    void moveDirection(IMapObject obj, Direction dir)
        throws MapException;

    /**
     * Move the object to the specified point.
     *
     * @param obj object
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws MapException if there is a problem
     */
    void moveTo(IMapObject obj, int x, int y)
        throws MapException;

    /**
     * Remove this object from the map.
     *
     * @param obj object to remove
     *
     * @throws MapException if there is a problem
     */
    void removeObject(IMapObject obj)
        throws MapException;
}
