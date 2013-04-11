package org.glowacki.core.fov;

/**
 * A point.
 *
 * Adapted from http://rlforj.sourceforge.net/
 */
public class Point
    implements Comparable
{
    int x;
    int y;

    /**
     * Create a point
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Compare an object with this object.
     *
     * @param obj object being compared
     *
     * @return the usual values
     */
    public int compareTo(Object obj)
    {
        if (!(obj instanceof Point)) {
            return getClass().getName().compareTo(obj.getClass().getName());
        }

        Point p = (Point) obj;

        int val = x - p.x;
        if (val == 0) {
            val = y - p.y;
        }

        return val;
    }

    /**
     * Compute the distance from the specified point to this point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return distance
     */
    public double distance(double x, double y)
    {
        x -= this.x;
        y -= this.y;

        return Math.sqrt((x * x) + (y * y));
    }

    /**
     * Return <tt>true</tt> if the object equals this object.
     *
     * @param obj object being compared
     *
     * @return <tt>true</tt> if the object equals this object.
     */
    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    /**
     * Compute the hash code for this point.
     *
     * @return hash code
     */
    public int hashCode()
    {
        int bits = x ^ y;
        return bits ^ (bits >> 16);
    }
}
