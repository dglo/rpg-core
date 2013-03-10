package org.glowacki.core;

/**
 * A single point.
 */
class MapPoint
{
    int x;
    int y;

    MapPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public String toString()
    {
        return String.format("(%d,%d)", x, y);
    }
}
