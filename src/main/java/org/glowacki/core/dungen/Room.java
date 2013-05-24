package org.glowacki.core.dungen;

/**
 * Room exception
 */
class RoomException
    extends GeneratorException
{
    RoomException(String msg)
    {
        super(msg);
    }
}

/**
 * Room description
 */
public class Room
    implements IRoom
{
    private int num;
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;

    private int stairX = Integer.MIN_VALUE;
    private int stairY = Integer.MIN_VALUE;
    private boolean stairUp;

    /**
     * Create a room
     *
     * @param num room number
     * @param x X coordinate
     * @param y Y coordinate
     * @param width room width
     * @param height room height
     * @param name room name
     */
    public Room(int num, int x, int y, int width, int height, String name)
    {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    /**
     * Add a staircase
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param up if <tt>true</tt>, this is an up staircase
     *
     * @throws RoomException if there is a problem
     */
    public void addStairs(int x, int y, boolean up)
        throws RoomException
    {
        if (stairX != Integer.MIN_VALUE || stairY != Integer.MIN_VALUE) {
            final String msg =
                String.format("Staircase is already located at %d,%d",
                              stairX, stairY);
            throw new RoomException(msg);
        } else if (x < 1 || x >= width - 1) {
            throw new RoomException("Bad staircase X " + x);
        } else if (y < 1 || y >= height - 1) {
            throw new RoomException("Bad staircase Y " + y);
        }

        if (width > 4) {
            if (x < 3) {
                stairX = 3;
            } else if (x > width - 2) {
                stairX = width - 2;
            } else {
                stairX = x;
            }
        } else {
            stairX = x;
        }

        if (height > 4) {
            if (y < 3) {
                stairY = 3;
            } else if (y > height - 2) {
                stairY = height - 2;
            } else {
                stairY = y;
            }
        } else {
            stairY = y;
        }

        stairUp = up;
    }

    /**
     * Change the room height
     *
     * @param amt amount to add or remove
     */
    public void changeHeight(int amt)
    {
        height += amt;
    }

    /**
     * Change the room width
     *
     * @param amt amount to add or remove
     */
    public void changeWidth(int amt)
    {
        width += amt;
    }

    /**
     * Move this room left one square
     */
    public void decX()
    {
        x--;
    }

    /**
     * Move this room up one square
     */
    public void decY()
    {
        y--;
    }

    /**
     * Get a character designation for this room's number
     *
     * @return character
     */
    public char getChar()
    {
        return (char) ('A' + num);
    }

    /**
     * Get this room's height
     *
     * @return height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the room name
     *
     * @return name
     */
    String getName()
    {
        return name;
    }

    /**
     * Get the room number
     *
     * @return room number
     */
    public int getNumber()
    {
        return num;
    }

    /**
     * Get the X coordinate for this room's staircase (or <tt>-1</tt>)
     *
     * @return X coordinate
     */
    public int getStaircaseX()
    {
        return stairX;
    }

    /**
     * Get the Y coordinate for this room's staircase (or <tt>-1</tt>)
     *
     * @return Y coordinate
     */
    public int getStaircaseY()
    {
        return stairY;
    }

    /**
     * Get this room's width
     *
     * @return width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get this room's X coordinate
     *
     * @return X coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get this room's Y coordinate
     *
     * @return Y coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Does this room contain a staircase?
     *
     * @return return <tt>true</tt> if this room has a staircase
     */
    public boolean hasStaircase()
    {
        return stairX != Integer.MIN_VALUE && stairY != Integer.MIN_VALUE;
    }

    /**
     * Move this room right one square
     */
    public void incX()
    {
        x++;
    }

    /**
     * Move this room down one square
     */
    public void incY()
    {
        y++;
    }

    /**
     * Does this room contain an up staircase?
     *
     * @return return <tt>true</tt> if this room has an up staircase
     */
    public boolean isUpStaircase()
    {
        return stairUp;
    }

    void setNumber(int num)
    {
        this.num = num;
    }

    /**
     * Return a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("%c[%d,%d]%dx%d", getChar(), x, y, width, height);
    }
}
