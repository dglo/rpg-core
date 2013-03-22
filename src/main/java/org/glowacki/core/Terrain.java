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
     * Get the terrain's movement cost
     *
     * @return cost
     */
    public double getCost()
    {
        return cost;
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
