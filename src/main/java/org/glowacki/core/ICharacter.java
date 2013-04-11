package org.glowacki.core;

/**
 * Character methods.
 */
public interface ICharacter
{
    /**
     * Build a path from the current position to the goal.
     *
     * @param goal target point
     *
     * @throws CoreException if there is a problem
     */
    void buildPath(MapPoint goal)
        throws CoreException;

    /**
     * Clear the stored path.
     */
    void clearPath();

    /**
     * Get character's current level
     *
     * @return level
     */
    Level getLevel();

    /**
     * Return character's name.
     *
     * @return name
     */
    String getName();

    /**
     * Get the boolean array indicating which cells in the current level
     * have been seen.
     *
     * @return two dimensional boolean array
     */
    boolean[][] getSeenArray();

    /**
     * Return character's X coordinate.
     *
     * @return X coordinate
     */
    int getX();

    /**
     * Return character's Y coordinate.
     *
     * @return Y coordinate
     */
    int getY();

    /**
     * Does this character have an existing path?
     *
     * @return <tt>true</tt> if this character has an ongoing path
     */
    boolean hasPath();

    /**
     * Is this character a player?
     *
     * @return <tt>true</tt> if this character is a player
     */
    boolean isPlayer();

    /**
     * Move the computer character.
     *
     * @param dir direction
     *
     * @return number of turns
     *
     * @throws CoreException if there is a problem
     */
    int move(Direction dir)
        throws CoreException;

    /**
     * Move to the next point in the path.
     *
     * @return number of turns
     *
     * @throws CoreException always
     */
    int movePath()
        throws CoreException;

    /**
     * Set computer character's level
     *
     * @param lvl level
     *
     * @throws CoreException if there is a problem
     */
    void setLevel(Level lvl)
        throws CoreException;

    /**
     * Set the character's position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    void setPosition(int x, int y);

    /**
     * Take a turn.
     */
    void takeTurn();
}
