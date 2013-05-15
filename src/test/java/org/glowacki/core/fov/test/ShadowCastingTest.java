package org.glowacki.core.fov.test;

import java.util.Random;

import junit.framework.TestCase;

import org.glowacki.core.fov.IVisibilityMap;
import org.glowacki.core.fov.Point;
import org.glowacki.core.fov.ShadowCasting;

class RealMap
    implements IVisibilityMap
{
    private char[][] map;
    private boolean[][] seen;

    RealMap(String[] template)
    {
        if (template == null) {
            throw new Error("Map template cannot be null");
        }

        int maxLen = 0;
        for (int y = 0; y < template.length; y++) {
            if (template[y] != null && template[y].length() > maxLen) {
                maxLen = template[y].length();
            }
        }

        if (maxLen == 0) {
            throw new Error("Map template cannot be empty");
        }

        map = new char[maxLen][template.length];

        for (int y = 0; y < template.length; y++) {
            int x;
            for (x = 0; template[y] != null && x < template[y].length();
                 x++)
            {
                map[x][y] = template[y].charAt(x);
            }
            for ( ; x < map.length; x++) {
                map[x][y] = ' ';
            }
        }

        seen = new boolean[map.length][map[0].length];
    }

    /**
     * Is the specified point inside the map?
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>true</tt> if the specified point is inside the map
     */
    public boolean contains(int x, int y)
    {
        return x >= 0 && x < map.length && y >= 0 && y < map[x].length;
    }

    public Point find(char ch)
    {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == ch) {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }

    public char getChar(int x, int y)
    {
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            return '?';
        }

        return map[x][y];
    }

    /**
     * Is the specified point obstructed?
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>true</tt> if the specified point is obstructed
     */
    public boolean isObstructed(int x, int y)
    {
        if (!contains(x, y)) {
            return true;
        }

        return map[x][y] == '-' || map[x][y] == ' ';
    }

    public boolean isSeen(int x, int y)
    {
        return seen[x][y];
    }

    /**
     * Mark the specified point as visible
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setVisible(int x, int y)
    {
        if (!contains(x, y)) {
            throw new Error(String.format("%d,%d is not a valid point", x, y));
        }

        if (!seen[x][y]) {
            seen[x][y] = true;
        }
    }

    public String toString()
    {
        boolean needNewline = false;
        StringBuilder buf = new StringBuilder();
        for (int y = 0; y < map[0].length; y++) {
            if (needNewline) {
                buf.append('\n');
            }

            for (int x = 0; x < map.length; x++) {
                if (seen[x][y]) {
                    buf.append(map[x][y]);
                } else {
                    buf.append('?');
                }
            }

            needNewline = true;
        }

        return buf.toString();
    }
}

/**
 * Testing FOV algorithms
 * @author sdatta
 *
 */
public class ShadowCastingTest
    extends TestCase
{
    ShadowCasting a;

    public ShadowCastingTest()
    {
        a = new ShadowCasting();
    }

    public void testEmpty()
    {
        MockMap b = new MockMap(false);

        a.findVisible(b, 10, 10, 5);

        assertTrue(b.visited.contains(new Point(11, 11)));
        assertTrue(b.visited.contains(new Point(10, 11)));
        assertTrue(b.visited.contains(new Point(11, 10)));
        assertTrue(b.visited.contains(new Point(10, 15)));
        assertTrue(b.visited.contains(new Point(15, 10)));
    }

    public void testFull()
    {
        MockMap b = new MockMap(true);

        a.findVisible(b, 10, 10, 5);

        assertTrue(b.visited.contains(new Point(11, 11)));
        assertTrue(b.visited.contains(new Point(10, 11)));
        assertTrue(b.visited.contains(new Point(11, 10)));
        assertFalse(b.visited.contains(new Point(10, 15)));
        assertFalse(b.visited.contains(new Point(15, 10)));
    }

    public void testLine()
    {
        MockMap b = new MockMap(true);

        for(int i=5; i<11; i++)
            b.exception.add(new Point(i, 10));

        a.findVisible(b, 10, 10, 5);

        assertTrue(b.visited.contains(new Point(11, 11)));
        assertTrue(b.visited.contains(new Point(10, 11)));
        assertTrue(b.visited.contains(new Point(11, 10)));
        assertTrue(b.visited.contains(new Point(5, 10)));
        assertFalse(b.visited.contains(new Point(15, 10)));
    }

    public void testAcrossPillar()
    {
        MockMap b = new MockMap(false);

        b.exception.add(new Point(10, 10));

        a.findVisible(b, 9, 9, 5);

        assertTrue(b.visited.contains(new Point(10, 11)));
        assertFalse(b.visited.contains(new Point(11, 11)));
    }

    public void testDiagonalWall()
    {
        MockMap b = new MockMap(false);

        b.exception.add(new Point(11, 11));
        b.exception.add(new Point(10, 10));

        a.findVisible(b, 10, 11, 5);

        assertTrue(b.visited.contains(new Point(11, 10)));
    }

    public void testLarge()
    {
        MockMap b = new MockMap(false);

        Random rand=new Random();
        for(int i=0; i<100; i++)
            b.exception.add(new Point(rand.nextInt(81)+60,
                                        rand.nextInt(81)+60));

        long t1=System.currentTimeMillis();
        a.findVisible(b, 100, 100, 40);
        long t2=System.currentTimeMillis();

        assertEquals("Check before visit list should be empty",
                     0, b.chkb4visit.size());
if(b.visiterr.size() > 0){
    for (Point p : b.visiterr) {
        System.out.println("!! " + p);
    }
}
        assertEquals("Visit error list should be empty",
                     0, b.visiterr.size());
    }

    public void testStrange()
    {
        final String[] mapStr = new String[] {
            "      -----",
            "  ####+...-",
            "--+----...-",
            "-.....-...-",
            "-..<..+...-",
            "-.....-...-",
            "-.....-...-",
            "-.....-----",
            "-.....-",
            "-------",
        };

        RealMap m = new RealMap(mapStr);

        Point origin = m.find('<');

        ShadowCasting sc = new ShadowCasting();
        sc.findVisible(m, origin.getX(), origin.getY(), 5);
        sc.findVisible(m, origin.getX() - 1, origin.getY(), 5);
    }
}
