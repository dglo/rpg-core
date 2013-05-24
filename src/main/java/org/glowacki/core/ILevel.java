package org.glowacki.core;

import java.util.List;

/**
 * Level methods
 */
public interface ILevel
{
    /**
     * Get a list of characters on this level.
     *
     * @return list of characters
     */
    List<ICharacter> getCharacters();

    /**
     * Get the map of this level.
     *
     * @return map
     */
    Map getMap();

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
     * Get the level name.
     *
     * @return name
     */
    String getName();

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
}
