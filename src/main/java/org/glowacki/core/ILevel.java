package org.glowacki.core;

import java.util.List;

/**
 * Level methods
 */
public interface ILevel
{
    /**
     * Add a nonplayer to this level.
     *
     * @param ch character
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws CoreException if there is a problem
     */
    void addNonplayer(ComputerCharacter ch, int x, int y)
        throws CoreException;

    /**
     * This character is entering this level from above.
     *
     * @param ch character
     *
     * @return point where character entered the level
     *
     * @throws CoreException if the level doesn't have an up staircase
     */
    IMapPoint enterDown(ICharacter ch)
        throws CoreException;

    /**
     * This character is entering this level from below.
     *
     * @param ch character
     *
     * @return point where character entered the level
     *
     * @throws CoreException if the level doesn't have a down staircase
     */
    IMapPoint enterUp(ICharacter ch)
        throws CoreException;

    /**
     * Remove the character from this level.
     *
     * @param ch character to remove
     *
     * @throws CoreException if the character is not on this level
     */
    void exit(ICharacter ch)
        throws CoreException;

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
     * Get the next level.
     *
     * @return next level
     */
    Level getNextLevel();

    /**
     * Get the previous level.
     *
     * @return previous level
     */
    Level getPreviousLevel();

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
     * Is the specified point occupied?
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>true</tt>if the point is occupied
     *
     * @throws MapException if there is a problem
     */
    boolean isOccupied(int x, int y)
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
