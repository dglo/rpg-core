package org.glowacki.core.fov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Code adapted from NG roguelike engine http://roguelike-eng.sourceforge.net/
 *
 * Recursive line-of-sight class implementing a spiraling shadow-casting
 * algorithm. This algorithm chosen because it can establish line-of-sight by
 * visiting each grid at most once, and is (for me) much simpler to implement
 * than octant oriented or non-recursive approaches. -TSS
 *
 * @author TSS
 */
public class ShadowCasting
{
    private static final int MAX_CACHED_RADIUS = 40;

    /**
     * Compute and return the list of points in line-of-sight to the given
     * region. In general, this method should be very fast.
     *
     * @param map visibility map
     * @param x X coordinate
     * @param y Y coordinate
     * @param distance distance of line-of-sight
     *
     */
    public void findVisible(IVisibilityMap map, int x, int y, int distance)
    {
        if (map == null) {
            throw new IllegalArgumentException();
        }
        if (distance < 1) {
            throw new IllegalArgumentException();
        }

        map.setVisible(x, y);
        go(map, new Point(x, y), 1, distance, 0.0, 359.9);
    }

    private static void go(IVisibilityMap map, Point ctr, int r,
                           int maxDistance, double th1, double th2)
    {
        if (r <= 0 || r > maxDistance) {
            throw new IllegalArgumentException();
        }

        ArcPoint[] circle = circles[r];

        boolean wasObstructed = false;
        boolean foundClear = false;
        for (int i = 0; i < circle.length; i++) {
            final ArcPoint arcPoint = circle[i];
            final int px = ctr.x + arcPoint.x;
            final int py = ctr.y + arcPoint.y;

            // if outside the map, ignore it and move to the next one
            if (!map.contains(px, py)) {
                wasObstructed = true;
                continue;
            }

            if (arcPoint.lagging < th1 && arcPoint.theta != th1 &&
                arcPoint.theta != th2)
            {
                continue;
            }
            if (arcPoint.leading > th2 && arcPoint.theta != th1 &&
                arcPoint.theta != th2)
            {
                continue;
            }

            // Accept this point
            map.setVisible(px, py);

            // Check to see if we have an obstacle here
            final boolean isObstructed = map.isObstructed(px, py);

            // If obstacle is encountered, we start a new run from our start
            // theta to the rightTheta of the current point at radius+1
            // We then proceed to the next non-obstacle, whose leftTheta
            // becomes our new start theta
            // If the last point is an obstacle, we do not start a new Run
            // at the end.
            if (isObstructed) {

                // keep going
                if (wasObstructed) {
                    continue;
                } else if (foundClear) {
                    // start a new run from start to this point's right side
                    double runEndTheta = arcPoint.leading;
                    double runStartTheta = th1;

                    if (r < maxDistance) {
                        go(map, ctr, r + 1, maxDistance, runStartTheta,
                           runEndTheta);
                    }
                    wasObstructed = true;
                } else {
                    if (arcPoint.theta == 0.0) {
                        th1 = 0.0;
                    } else {
                        th1 = arcPoint.leading;
                    }
                }
            } else {
                foundClear = true;

                // we're clear of obstacle; any runs propogated from this
                // run starts at this point's leftTheta

                if (!wasObstructed) {
                    continue;
                }

                ArcPoint last = circle[i - 1];
                th1 = last.lagging;

                wasObstructed = false;
            }

            wasObstructed = isObstructed;
        }

        if (!wasObstructed && r < maxDistance) {
            go(map, ctr, r + 1, maxDistance, th1, th2);
        }
    }

    /**
     * An arc point
     */
    static class ArcPoint
        implements Comparable
    {
        int x;
        int y;

        double theta;
        double leading;
        double lagging;

        ArcPoint(int dx, int dy)
        {
            this.x = dx;
            this.y = dy;
            theta = angle(y, x);

            if (x < 0 && y < 0) {
                // top left
                leading = angle(y - 0.5, x + 0.5);
                lagging = angle(y + 0.5, x - 0.5);
            } else if (x < 0) {
                // bottom left
                leading = angle(y - 0.5, x - 0.5);
                lagging = angle(y + 0.5, x + 0.5);
            } else if (y > 0) {
                // bottom right
                leading = angle(y + 0.5, x - 0.5);
                lagging = angle(y - 0.5, x + 0.5);
            } else {
                // top right
                leading = angle(y + 0.5, x + 0.5);
                lagging = angle(y - 0.5, x - 0.5);
            }
        }

        double angle(double y, double x)
        {
            double a = Math.atan2(y, x);
            a = Math.toDegrees(a);
            a = 360.0 - a;
            a %= 360;
            if (a < 0) {
                a += 360;
            }
            return a;
        }

        public int compareTo(Object o)
        {
            return theta > ((ArcPoint) o).theta ? 1 : -1;
        }

        public boolean equals(Object o)
        {
            return theta == ((ArcPoint) o).theta;
        }

        public int hashCode()
        {
            return x * y;
        }

        public String toString()
        {
            return "[" + x + "," + y + "=" + (int) (theta) + "/" +
                (int) (leading) + "/" + (int) (lagging);
        }
    }

    private static ArcPoint[][] circles =
        new ArcPoint[MAX_CACHED_RADIUS + 1][];

    static {
        Point origin = new Point(0, 0);

        int radius = MAX_CACHED_RADIUS;

        HashMap<Integer, ArrayList<ArcPoint>> tmp =
            new HashMap<Integer, ArrayList<ArcPoint>>();

        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int distance = (int) Math.floor(origin.distance(i, j));

                // If filled, add anything where floor(distance) <= radius
                // If not filled, require that floor(distance) == radius
                if (distance <= radius) {
                    ArrayList<ArcPoint> circ = tmp.get(distance);
                    if (circ == null) {
                        circ = new ArrayList<ArcPoint>();
                        tmp.put(distance, circ);
                    }
                    circ.add(new ArcPoint(i, j));
                }
            }
        }

        ArcPoint[] template = new ArcPoint[0];

        for (int i = 0; i < circles.length; i++) {
            ArrayList<ArcPoint> list = tmp.get(i);
            if (list == null) {
                circles[i] = null;
            } else {
                circles[i] = list.toArray(template);
                Arrays.sort(circles[i]);
            }
        }
    }
}
