package org.glowacki.core;

/**
 * Terrain-related constant value
 */
interface TerrainConst
{
    double IMPASSABLE = Double.MAX_VALUE;
}

/**
 * Terrain types.
 */
public enum Terrain
{
    DOOR(1.0),
    DOWNSTAIRS(1.0),
    FLOOR(1.0),
    TUNNEL(1.0),
    UPSTAIRS(1.0),
    WALL(TerrainConst.IMPASSABLE),
    WATER(1.2),
    UNKNOWN(TerrainConst.IMPASSABLE);

    private double cost;

    /**
     * Create a terrain value.
     *
     * @param cost movement cost
     */
    Terrain(double cost)
    {
        this.cost = cost;
    }

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
     * Get the terrain's movement cost
     *
     * @return cost
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Get the Terrain value associated with this character.
     *
     * @param ch character
     *
     * @return associated terrain
     */
    public static Terrain getTerrain(char ch)
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

    /**
     * Is this a space which can be moved onto?
     *
     * @return <tt>true</tt> if this is a movable space
     */
    public boolean isMovable()
    {
        return cost != TerrainConst.IMPASSABLE;
    }
}
