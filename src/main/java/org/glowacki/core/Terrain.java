package org.glowacki.core;

interface TerrainConst
{
    public static final double IMPASSABLE = Double.MAX_VALUE;

}

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

    Terrain(double cost)
    {
        this.cost = cost;
    }

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

    public double getCost() { return cost; }

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

    public boolean isMovable() { return cost != TerrainConst.IMPASSABLE; }
}
