package org.glowacki.core;

/**
 * Mapping between terrain and ASCII characters.
 */
public abstract class MapCharRepresentation
{
    /**
     * Get the character associated with this Terrain.
     *
     * @param t terrain
     *
     * @return associated character
     */
    public static char getCharacter(Terrain t)
    {
        switch (t) {
        case DOOR:
            return '+';
        case DOWNSTAIRS:
            return '>';
        case FLOOR:
            return '.';
        case TUNNEL:
            return '#';
        case UPSTAIRS:
            return '<';
        case WALL:
            return '-';
        case WATER:
            return '~';
        default:
            return ' ';
        }
    }


    /**
     * Get the Terrain value associated with this character.
     *
     * @param ch character
     *
     * @return associated terrain
     */
    static Terrain getTerrain(char ch)
    {
        switch (ch) {
        case '+':
            return Terrain.DOOR;
        case '>':
            return Terrain.DOWNSTAIRS;
        case '.':
            return Terrain.FLOOR;
        case '#':
            return Terrain.TUNNEL;
        case '<':
            return Terrain.UPSTAIRS;
        case '-':
        case '|':
            return Terrain.WALL;
        case '~':
            return Terrain.WATER;
        default:
            return Terrain.UNKNOWN;
        }
    }
}
