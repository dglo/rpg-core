package org.glowacki.core.dungen;

class RoomException
    extends GeneratorException
{
    RoomException(String msg)
    {
        super(msg);
    }
}

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

    public Room(int num, int x, int y, int width, int height, String name)
    {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

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
                x = 3;
            } else if (x > width - 2) {
                x = width - 2;
            }
        }

        if (height > 4) {
            if (y < 3) {
                y = 3;
            } else if (y > height - 2) {
                y = height - 2;
            }
        }

        stairX = x;
        stairY = y;
        stairUp = up;
    }

    public void decX()
    {
        x--;
    }

    public void decY()
    {
        y--;
    }

    public char getChar()
    {
        return (char) ('A' + num);
    }

    public int getHeight()
    {
        return height;
    }

    String getName()
    {
        return name;
    }

    public int getNumber()
    {
        return num;
    }

    public int getStaircaseX()
    {
        return stairX;
    }

    public int getStaircaseY()
    {
        return stairY;
    }

    public int getWidth()
    {
        return width;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean hasStaircase()
    {
        return stairX != Integer.MIN_VALUE && stairY != Integer.MIN_VALUE;
    }

    public void incX()
    {
        x++;
    }

    public void incY()
    {
        y++;
    }

    public boolean isUpStaircase()
    {
        return stairUp;
    }

    void setNumber(int num)
    {
        this.num = num;
    }

    public void changeHeight(int amt)
    {
        height += amt;
    }

    public void changeWidth(int amt)
    {
        width += amt;
    }

    public String toString()
    {
        return String.format("%c[%d,%d]%dx%d", getChar(), x, y, width, height);
    }
}
