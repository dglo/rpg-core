package org.glowacki.core.dungen;

public class Room
{
    private int num;
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;

    Room(int num, int x, int y, int width, int height, String name)
    {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
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

    void incX()
    {
        x++;
    }

    void incY()
    {
        y++;
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
