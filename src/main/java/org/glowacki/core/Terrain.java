package org.glowacki.core;

public enum Terrain
{
    DOOR(1.0),
    DOWNSTAIRS(1.0),
    FLOOR(1.0),
    TUNNEL(1.0),
    UPSTAIRS(1.0),
    WALL(Double.MAX_VALUE),
    WATER(1.2),
    UNKNOWN(Double.MAX_VALUE);

    public static final double IMPASSABLE = Double.MAX_VALUE;

    private double cost;

    Terrain(double cost)
    {
        this.cost = cost;
    }

    public static Terrain[] getAll()
    {
        return new Terrain[] {
            DOOR, DOWNSTAIRS, FLOOR, TUNNEL, UPSTAIRS, WALL, WATER, UNKNOWN,
        };
    }

    public double getCost() { return cost; }

    public boolean isMovable() { return cost != Double.MAX_VALUE; }
}
