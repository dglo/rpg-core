package org.glowacki.core;

import org.glowacki.core.event.EventListener;

/**
 * Character methods.
 */
public interface ICharacter
    extends IMapObject
{
    /** Character states */
    public enum State { ASLEEP, MEANDER, IN_PURSUIT };

    /**
     * Add an event listener.
     *
     * @param listener new listener
     */
    void addEventListener(EventListener listener);

    /**
     * Build a path from the current position to the goal.
     *
     * @param goal target point
     *
     * @throws CoreException if there is a problem
     */
    void buildPath(IMapPoint goal)
        throws CoreException;

    /**
     * Clear the stored path.
     */
    void clearPath();

    /**
     * Get unique character ID.
     *
     * @return id
     */
    int getId();

    /**
     * Get character's current level
     *
     * @return level
     */
    Level getLevel();

    /**
     * Get the boolean array indicating which cells in the current level
     * have been seen.
     *
     * @return two dimensional boolean array
     */
    boolean[][] getSeenArray();

    /**
     * Get the distance this character can see (in number of tiles)
     *
     * @return distance
     */
    int getSightDistance();

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
     * Take a turn.
     */
    void takeTurn();
}
