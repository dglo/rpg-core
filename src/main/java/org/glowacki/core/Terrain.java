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
    /** Door */
    DOOR(1.0),
    /** Down staircase */
    DOWNSTAIRS(1.0),
    /** Generic floor */
    FLOOR(1.0),
    /** Tunnel */
    TUNNEL(1.0),
    /** Up staircase */
    UPSTAIRS(1.0),
    /** Wall */
    WALL(TerrainConst.IMPASSABLE),
    /** Water */
    WATER(1.2),
    /** Unknown terrain */
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
