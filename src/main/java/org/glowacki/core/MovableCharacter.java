package org.glowacki.core;

public interface MovableCharacter
{
    enum Direction { LEFT, UP, RIGHT, DOWN, CLIMB, DESCEND };

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
     */
    int move(Direction dir);
}
