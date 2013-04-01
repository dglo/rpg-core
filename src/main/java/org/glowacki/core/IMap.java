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
     * Move the character to the specified point.
     *
     * @param ch character
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws MapException if there is a problem
     */
    void moveTo(ICharacter ch, int x, int y)
        throws MapException;
}
