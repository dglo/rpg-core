package org.glowacki.core;

public enum Terrain
{
    FLOOR(1.0),
    WATER(1.2),
    DOOR(1.0);

    private double cost;

    Terrain(double cost)
    {
        this.cost = cost;
    }

    public double getCost() { return cost; }
}
