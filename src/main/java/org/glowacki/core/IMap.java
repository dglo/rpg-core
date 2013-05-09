package org.glowacki.core;

/**
 * Map methods.
 */
public interface IMap
{
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
}
