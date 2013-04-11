package org.glowacki.core.fov.test;

import java.util.Random;

import junit.framework.TestCase;

import org.glowacki.core.fov.Point;
import org.glowacki.core.fov.ShadowCasting;

/**
 * Testing FOV algorithms
 * @author sdatta
 *
 */
public class ShadowCastingTest
    extends TestCase
{
    ShadowCasting a;

    public ShadowCastingTest() {
        a = new ShadowCasting();
    }

    public void testEmpty() {
        MockMap b = new MockMap(false);

        a.findVisible(b, 10, 10, 5);

        assertTrue(b.visited.contains(new Point(11, 11)));
        assertTrue(b.visited.contains(new Point(10, 11)));
        assertTrue(b.visited.contains(new Point(11, 10)));
        assertTrue(b.visited.contains(new Point(10, 15)));
        assertTrue(b.visited.contains(new Point(15, 10)));
    }

    public void testFull() {
        MockMap b = new MockMap(true);

        a.findVisible(b, 10, 10, 5);

        assertTrue(b.visited.contains(new Point(11, 11)));
        assertTrue(b.visited.contains(new Point(10, 11)));
        assertTrue(b.visited.contains(new Point(11, 10)));
        assertFalse(b.visited.contains(new Point(10, 15)));
        assertFalse(b.visited.contains(new Point(15, 10)));
    }

    public void testLine() {
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

    public void testAcrossPillar() {
        MockMap b = new MockMap(false);

        b.exception.add(new Point(10, 10));

        a.findVisible(b, 9, 9, 5);

        assertTrue(b.visited.contains(new Point(10, 11)));
        assertFalse(b.visited.contains(new Point(11, 11)));
    }

    public void testDiagonalWall() {
        MockMap b = new MockMap(false);

        b.exception.add(new Point(11, 11));
        b.exception.add(new Point(10, 10));

        a.findVisible(b, 10, 11, 5);

        assertTrue(b.visited.contains(new Point(11, 10)));
    }

    public void testLarge() {
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
        assertEquals("Visit error list should be empty",
                     0, b.visiterr.size());
    }
}
