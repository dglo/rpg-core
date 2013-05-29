package org.glowacki.core.dungen;

import org.glowacki.core.test.MockRandom;

import org.glowacki.core.UnimplementedError;

import org.junit.*;
import static org.junit.Assert.*;

class TTRoom
    implements IRoom
{
    private int num;
    private int x;
    private int y;
    private int width;
    private int height;

    TTRoom(int num, int x, int y, int width, int height)
    {
        this.num = num;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addStairs(int x, int y, boolean up)
        throws RoomException
    {
        throw new UnimplementedError();
    }

    public void changeHeight(int height)
    {
        throw new UnimplementedError();
    }

    public void changeWidth(int width)
    {
        throw new UnimplementedError();
    }

    public void decX()
    {
        throw new UnimplementedError();
    }

    public void decY()
    {
        throw new UnimplementedError();
    }

    public void incX()
    {
        throw new UnimplementedError();
    }

    public void incY()
    {
        throw new UnimplementedError();
    }

    public char getChar()
    {
        throw new UnimplementedError();
    }

    public int getHeight()
    {
        return height;
    }

    public int getNumber()
    {
        return num;
    }

    public int getStaircaseX()
    {
        throw new UnimplementedError();
    }

    public int getStaircaseY()
    {
        throw new UnimplementedError();
    }

    public int getWidth()
    {
        return width;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean hasStaircase()
    {
        return false;
    }

    public boolean isUpStaircase()
    {
        throw new UnimplementedError();
    }
}

public class TunnelerTest
{
    @Test
    public void testCreateNull()
    {
        try {
            new Tunneler(null, 0);
            fail("Should not succeed");
        } catch (TunnelerException te) {
            assertNotNull("Message is null", te.getMessage());
            assertEquals("Unexpected exception",
                         "Expect at least 2 rooms, not 0", te.getMessage());
        }
    }

    @Test
    public void testCreateBadRoom()
    {
        IRoom[] list = new IRoom[2];

        try {
            new Tunneler(list, 0);
            fail("Should not succeed");
        } catch (TunnelerException te) {
            assertNotNull("Message is null", te.getMessage());
            assertEquals("Unexpected exception",
                         "List entry 0 is null", te.getMessage());
        }

        list[0] = new TTRoom(-1, 0, 0, 0, 0);
        try {
            new Tunneler(list, 0);
            fail("Should not succeed");
        } catch (TunnelerException te) {
            assertNotNull("Message is null", te.getMessage());
            assertEquals("Unexpected exception",
                         "Bad room number -1 (must be between 0 and 2)",
                         te.getMessage());
        }

        list[0] = new TTRoom(1, 0, 0, 0, 0);
        list[1] = new TTRoom(2, 0, 0, 0, 0);
        try {
            new Tunneler(list, 0);
            fail("Should not succeed");
        } catch (TunnelerException te) {
            assertNotNull("Message is null", te.getMessage());
            assertEquals("Unexpected exception",
                         "Bad room number 2 (must be between 0 and 2)",
                         te.getMessage());
        }
    }

    @Test
    public void createFourMaxZero()
    {
        IRoom[] list = new IRoom[] {
            new TTRoom(0, 1, 1, 4, 4),
            new TTRoom(1, 1, 7, 4, 4),
            new TTRoom(2, 7, 1, 4, 4),
            new TTRoom(3, 7, 7, 4, 4)
        };

        MockRandom r = new MockRandom();

        Tunneler t;
        try {
            t = new Tunneler(list, 0);
        } catch (TunnelerException te) {
            fail("Unexpected exception " + te);
            return;
        }

        // add random values
        r.add(0).add(1).add(2).add(3);

        try {
            t.dig(12, 12, r);
            fail("Should not succeed");
        } catch (TunnelerException te) {
            assertNotNull("Message is null", te.getMessage());
            assertEquals("Unexpected exception",
                         "Cannot find unconnected room", te.getMessage());
        }

        assertFalse("Did not use " + r.remaining() + " random values",
                    r.hasData());
    }

    @Test
    public void createFourMaxTwo()
    {
        IRoom[] list = new IRoom[] {
            new TTRoom(0, 1, 1, 4, 4),
            new TTRoom(1, 1, 7, 4, 4),
            new TTRoom(2, 7, 1, 4, 4),
            new TTRoom(3, 7, 7, 4, 4)
        };

        MockRandom r = new MockRandom();

        Tunneler t;
        try {
            t = new Tunneler(list, 2);
        } catch (TunnelerException te) {
            fail("Unexpected exception " + te);
            return;
        }

        // add random values
        r.add(0).add(1).add(2).add(3);

        try {
            String[] map = t.dig(12, 12, r);
            //for (int i = 0; i < map.length; i++) {
            //    System.out.println(map[i]);
            //}
        } catch (TunnelerException te) {
            fail("Unexpected exception " + te);
            return;
        }

        assertFalse("Did not use " + r.remaining() + " random values",
                    r.hasData());
    }
}
