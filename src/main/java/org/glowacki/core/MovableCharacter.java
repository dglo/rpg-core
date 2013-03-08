package org.glowacki.core;

public interface MovableCharacter
{
    enum Direction { UNKNOWN, LEFT, UP, RIGHT, DOWN, CLIMB, DESCEND };

    /**
     * Get this character's name.
     *
     * @return name
     */
    String getName();

    /**
     * Get X coordinate for this character.
     *
     * @return X coordinate
     */
    int getX();

    /**
     * Get Y coordinate for this character.
     *
     * @return Y coordinate
     */
    int getY();

    /**
     * Move in the specified direction.
     *
     * @param dir direction
     *
     * @return number of turns required to make the move, or
     *         <tt>-1</tt> if the move is not valid
     *
     * @throws LevelException if the move is not valid
     */
    int move(Direction dir)
        throws LevelException;

    /**
     * Move the character to the specified position on the level.
     *
     * @param l level
     * @param x x coordinate
     * @param y y coordinate
     */
    void position(Level l, int x, int y);
}
