package org.glowacki.core.dungen;

public class Room
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

    Room(int num, int x, int y, int width, int height, String name)
    {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    void addStairs(int x, int y, boolean up)
    {
        if (x < 1 || x >= width) {
            throw new Error("Bad staircase X " + x);
        } else if (y < 1 || y >= height) {
            throw new Error("Bad staircase Y " + y);
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

    void decX()
    {
        x--;
    }

    void decY()
    {
        y--;
    }

    char getChar()
    {
        return (char)('A' + num);
    }

    int getHeight()
    {
        return height;
    }

    String getName()
    {
        return name;
    }

    int getNumber()
    {
        return num;
    }

    int getStaircaseX()
    {
        return stairX;
    }

    int getStaircaseY()
    {
        return stairY;
    }

    int getWidth()
    {
        return width;
    }

    int getX()
    {
        return x;
    }

    int getY()
    {
        return y;
    }

    boolean hasStaircase()
    {
        return stairX != Integer.MIN_VALUE && stairY != Integer.MIN_VALUE;
    }

    void incX()
    {
        x++;
    }

    void incY()
    {
        y++;
    }

    boolean isUpStaircase()
    {
        return stairUp;
    }

    void setNumber(int num)
    {
        this.num = num;
    }

    void changeHeight(int amt)
    {
        height += amt;
    }

    void changeWidth(int amt)
    {
        width += amt;
    }

    public String toString()
    {
        return String.format("%c[%d,%d]%dx%d", getChar(), x, y, width, height);
    }
}
