package org.glowacki.core.dungen;

/**
 * Room node types
 */
enum RoomType
{
    DOOR('+'),
    DOWNSTAIRS('>'),
    EMPTY(' '),
    FLOOR('.'),
    //SIDEWALL('|'),
    TUNNEL('#'),
    UPSTAIRS('<'),
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
