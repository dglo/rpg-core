package org.glowacki.core.dungen;

enum RoomType
{
    DOOR('+'),
    EMPTY(' '),
    FLOOR('.'),
    //SIDEWALL('|'),
    TUNNEL('#'),
    WALL('-');

    private final char ch;

    RoomType(char ch)
    {
        this.ch = ch;
    }

    char getChar()
    {
        return ch;
    }
}
