package org.glowacki.core;

import org.glowacki.core.event.EventListener;
import org.glowacki.core.util.IRandom;

/**
 * Character methods.
 */
public interface ICharacter
    extends Comparable<ICharacter>, IMapObject
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
     * Attack the character
     *
     * @param random random number generator
     * @param ch character to attack
     * @param weapon weapon used for the attack
     */
    void attack(IRandom random, ICharacter ch, IWeapon weapon);

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
     * Get percent chance to hit on attack
     *
     * @param weapon weapon used in attack
     *
     * @return attack percent
     */
    int getAttackPercent(IWeapon weapon);

    /**
     * Get percent chance to parry an attack
     *
     * @param weapon weapon used in attack
     *
     * @return defend percent
     */
    int getDefendPercent(IWeapon weapon);

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
    ILevel getLevel();

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
     * Get the visible cell array
     *
     * @return array of visible cells
     */
    boolean[][] getVisible();

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
     * Is the specified point visible?
     *
     * @param px X coordinate
     * @param py Y coordinate
     *
     * @return <tt>true</tt> if the point is visible
     */
    boolean isVisible(int px, int py);

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
     * Is the character on a staircase?
     *
     * @return <tt>true</tt> if character is on a staircase
     */
    boolean onStaircase();

    /**
     * Set computer character's level
     *
     * @param lvl level
     *
     * @throws CoreException if there is a problem
     */
    void setLevel(ILevel lvl)
        throws CoreException;

    /**
     * Take damage from an attack
     *
     * @param random random number generator
     * @param ch attacker
     * @param weapon weapon used for the attack
     */
    void takeDamage(IRandom random, ICharacter ch, IWeapon weapon);

    /**
     * Take a turn.
     */
    void takeTurn();

    /**
     * Use the staircase in the character's current position.
     *
     * @return number of turns
     *
     * @throws CoreException if there is a problem
     */
    int useStaircase()
        throws CoreException;
}
