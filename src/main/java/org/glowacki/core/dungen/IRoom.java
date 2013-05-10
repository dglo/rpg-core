package org.glowacki.core.dungen;

/**
 * Room template
 */
public interface IRoom
{
    /**
     * Add a staircase to this room
     *
     * @param x X coordinate of staircase within the room
     * @param y Y coordinate of staircase within the room
     * @param up if <tt>true</tt>, staircase should go up
     *
     * @throws RoomException if there is a problem
     */
    void addStairs(int x, int y, boolean up)
        throws RoomException;

    /**
     * Change the room height
     *
     * @param height new height
     */
    void changeHeight(int height);

    /**
     * Change the room width
     *
     * @param width new width
     */
    void changeWidth(int width);

    /**
     * Decrement the X coordinate of this room
     */
    void decX();

    /**
     * Decrement the Y coordinate of this room
     */
    void decY();

    /**
     * Get an alphabet character representing this room
     *
     * @return alpha character ('A' for 0, 'B' for 1, etc.)
     */
    char getChar();

    /**
     * Get the room height
     *
     * @return height
     */
    int getHeight();

    /**
     * Get the room number
     *
     * @return number
     */
    int getNumber();

    /**
     * Get the staircase's X coordinate (relative to the room)
     *
     * @return X coordinate (0 if it's on the top edge of the room)
     */
    int getStaircaseX();

    /**
     * Get the staircase's Y coordinate (relative to the room)
     *
     * @return Y coordinate (0 if it's on the top edge of the room)
     */
    int getStaircaseY();

    /**
     * Get the room width
     *
     * @return width
     */
    int getWidth();

    /**
     * Get the room's X coordinate
     *
     * @return X coordinate
     */
    int getX();

    /**
     * Get the room's Y coordinate
     *
     * @return Y coordinate
     */
    int getY();

    /**
     * Does this room have a staircase?
     *
     * @return <tt>true</tt> if this room has a staircase
     */
    boolean hasStaircase();

    /**
     * Increment the X coordinate of this room
     */
    void incX();

    /**
     * Increment the Y coordinate of this room
     */
    void incY();

    /**
     * Does this room's staircase go up?
     *
     * @return <tt>true</tt> if this room has an up staircase
     */
    boolean isUpStaircase();
}
