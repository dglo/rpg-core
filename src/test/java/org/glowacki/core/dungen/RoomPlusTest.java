package org.glowacki.core.dungen;

import org.junit.*;
import static org.junit.Assert.*;

public class RoomPlusTest
{
    @Test
    public void testCreate()
    {
        final int num = 12;
        final int x = 3;
        final int y = 4;
        final int width = 5;
        final int height = 6;
        final String name = "foo";
        final int maxConn = 7;

        RoomPlus room = new RoomPlus(num, x, y, width, height, name, maxConn);

        assertEquals("Bad number", num, room.getNumber());
        assertEquals("Bad x", x, room.getX());
        assertEquals("Bad y", y, room.getY());
        assertEquals("Bad width", width, room.getWidth());
        assertEquals("Bad height", height, room.getHeight());
        assertEquals("Bad name", name, room.getName());
        assertEquals("Bad max connections", maxConn, room.getMaxConnections());

        RoomPlus[] conn = room.getConnections();
        for (int c = 0; c < conn.length; c++) {
            assertNull("Non-null connection #" + c, conn[c]);
        }

        assertFalse("Room should not be full", room.isFull());
    }

    @Test
    public void testConnect()
    {
        final int num = 0;
        final int x = 3;
        final int y = 4;
        final int width = 5;
        final int height = 6;
        final String name = "foo";
        final int maxConn = 3;

        RoomPlus room = null;
        for (int i = 0; i <= maxConn; i++) {
            RoomPlus r = new RoomPlus(num + i, x + i, y + i, width + i,
                                      height + i, name + i, maxConn);

            assertEquals("Bad number", num + i, r.getNumber());
            assertEquals("Bad x", x + i, r.getX());
            assertEquals("Bad y", y + i, r.getY());
            assertEquals("Bad width", width + i, r.getWidth());
            assertEquals("Bad height", height + i, r.getHeight());
            assertEquals("Bad name", name + i, r.getName());
            assertEquals("Bad max connections", maxConn,
                         r.getMaxConnections());

            RoomPlus[] conn = r.getConnections();
            for (int c = 0; c < conn.length; c++) {
                assertNull("Non-null connection #" + c, conn[c]);
            }

            assertFalse("Room should not be full", r.isFull());

            if (i == 0) {
                assertNull("Room should be null", room);
                room = r;
            } else {
                assertNotNull("Room should not be null", room);
                assertFalse("Room should not be full", room.isFull());
                assertTrue("Failed to add connection #" + i,
                           room.addConnection(r));
            }

            RoomPlus[] rc = room.getConnections();
            for (int c = 0; c < rc.length; c++) {
                if (c >= i) {
                    assertNull("Non-null connection #" + c, rc[c]);
                } else {
                    assertNotNull("Null connection #" + c, rc[c]);
                    assertFalse("Adding previously added connection #" + c +
                                " should fail", room.addConnection(rc[c]));
                }
            }

            boolean[] seen = new boolean[5];
            if (i == 0) {
                assertFalse("initial markConnections() succeeded",
                            room.markConnections(seen));
            } else {
                assertTrue("markConnections() #" + i + " failed",
                            room.markConnections(seen));
            }
            for (int s = 0; s < maxConn; s++) {
                if (s != 0 && s <= i) {
                    assertTrue("Bad mark #" + s, seen[s]);
                } else {
                    assertFalse("Entry #" + s + " should not be marked",
                                seen[s]);
                }
            }

            assertFalse("second markConnections() succeeded",
                        room.markConnections(seen));
        }

        assertTrue("Room should be full", room.isFull());

        RoomPlus extra = new RoomPlus(17, 17, 17, 17, 17, "extra", 17);

        assertFalse("Should not be able to add a connection to a full room",
                    room.addConnection(extra));
    }

    @Test
    public void testOverlapX()
    {
        final int x = 10;
        final int y = 10;
        final int width = 5;
        final int height = 6;

        RoomPlus room = new RoomPlus(0, x, y, width, height, "foo", 3);

        final int otherWidth = 3;
        for (int i = x - (otherWidth + 1); i < x + (width + 1); i++) {
            RoomPlus other =
                new RoomPlus(1, i, y, otherWidth, height, "other", 1);
            boolean exp =
                room.getX() > (other.getX() + other.getWidth() - 1) ||
                (room.getX() + room.getWidth() - 1) < other.getX();

            assertEquals("Room " + room + " should" + (exp ? "" : " not") +
                         " overlap " + other, exp, !room.overlapX(other));
        }
    }

    @Test
    public void testOverlapY()
    {
        final int x = 10;
        final int y = 10;
        final int width = 5;
        final int height = 6;

        RoomPlus room = new RoomPlus(0, x, y, width, height, "foo", 3);

        final int otherHeight = 3;
        for (int i = y - (otherHeight + 1); i < y + (height + 1); i++) {
            RoomPlus other =
                new RoomPlus(1, x, i, width, otherHeight, "other", 1);
            boolean exp =
                room.getY() > (other.getY() + other.getHeight() - 1) ||
                (room.getY() + room.getHeight() - 1) < other.getY();

            assertEquals("Room " + room + " should" + (exp ? "" : " not") +
                         " overlap " + other, exp, !room.overlapY(other));
        }
    }
}
