package org.glowacki.core.dungen;

import org.junit.*;
import static org.junit.Assert.*;

public class RoomTest
{
    @Test
    public void testCreate()
    {
        final int num = 0;
        final int x = 1;
        final int y = 2;
        final int width = 3;
        final int height = 4;
        final String name = "foo";

        char ch = (char)('A' + num);

        Room r = new Room(num, x, y, width, height, name);
        assertEquals("Bad number", num, r.getNumber());
        assertEquals("Bad X", x, r.getX());
        assertEquals("Bad Y", y, r.getY());
        assertEquals("Bad width", width, r.getWidth());
        assertEquals("Bad height", height, r.getHeight());
        assertEquals("Bad name", name, r.getName());
        assertEquals("Bad char", (char)('A' + r.getNumber()), r.getChar());

        final String str = String.format("%c[%d,%d]%dx%d", ch, x, y, width,
                                         height);
        assertEquals("Bad string", str, r.toString());

        r.setNumber(num + 10);
        assertEquals("Bad number", num + 10, r.getNumber());
        assertEquals("Bad char", (char)('A' + r.getNumber()), r.getChar());
    }

    @Test
    public void testIncDec()
    {
        final int num = 0;
        final int x = 1;
        final int y = 2;
        final int width = 3;
        final int height = 4;
        final String name = "foo";

        Room r = new Room(num, x, y, width, height, name);
        assertEquals("Bad X", x, r.getX());
        assertEquals("Bad Y", y, r.getY());

        r.incX();
        r.incY();
        r.incY();
        assertEquals("Bad X", x + 1, r.getX());
        assertEquals("Bad Y", y + 2, r.getY());

        r.decX();
        r.decX();
        r.decY();
        assertEquals("Bad X", x - 1, r.getX());
        assertEquals("Bad Y", y + 1, r.getY());
    }

    @Test
    public void testAddStairsBad()
    {
        final int num = 0;
        final int x = 1;
        final int y = 2;
        final int width = 3;
        final int height = 4;
        final String name = "foo";

        Room r = new Room(num, x, y, width, height, name);

        try {
            r.addStairs(0, 0, false);
            fail("Should not succeed");
        } catch (RoomException re) {
            assertNotNull("Message is null", re.getMessage());
            assertEquals("Unexpected exception",
                         "Bad staircase X 0", re.getMessage());
        }

        try {
            r.addStairs(width - 1, 0, false);
            fail("Should not succeed");
        } catch (RoomException re) {
            assertNotNull("Message is null", re.getMessage());
            assertEquals("Unexpected exception",
                         "Bad staircase X " + (width - 1), re.getMessage());
        }

        try {
            r.addStairs(1, 0, false);
            fail("Should not succeed");
        } catch (RoomException re) {
            assertNotNull("Message is null", re.getMessage());
            assertEquals("Unexpected exception",
                         "Bad staircase Y 0", re.getMessage());
        }

        try {
            r.addStairs(1, height - 1, false);
            fail("Should not succeed");
        } catch (RoomException re) {
            assertNotNull("Message is null", re.getMessage());
            assertEquals("Unexpected exception",
                         "Bad staircase Y " + (height - 1), re.getMessage());
        }
    }

    @Test
    public void testAddStairsUp()
        throws RoomException
    {
        final int num = 0;
        final int x = 1;
        final int y = 2;
        final int width = 3;
        final int height = 4;
        final String name = "foo";

        Room r = new Room(num, x, y, width, height, name);

        assertFalse("Should not have staircase", r.hasStaircase());
        assertFalse("Should have up staircase", r.isUpStaircase());
        assertEquals("Bad staircase X", Integer.MIN_VALUE, r.getStaircaseX());
        assertEquals("Bad staircase Y", Integer.MIN_VALUE, r.getStaircaseY());

        final int sx = 1;
        final int sy = 1;

        r.addStairs(sx, sy, true);
        assertTrue("Should have staircase", r.hasStaircase());
        assertTrue("Should have up staircase", r.isUpStaircase());
        assertEquals("Bad staircase X", sx, r.getStaircaseX());
        assertEquals("Bad staircase Y", sy, r.getStaircaseY());

        try {
            r.addStairs(sx, sy + 1, true);
            fail("Should not succeed");
        } catch (RoomException re) {
            assertNotNull("Message is null", re.getMessage());
            assertEquals("Unexpected exception",
                         String.format("Staircase is already located at %d,%d",
                                       sx, sy), re.getMessage());
        }
    }
}
