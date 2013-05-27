package org.glowacki.core.dungen;

import java.io.PrintStream;
import java.util.ArrayList;

import org.glowacki.core.dungen.IRoom;
import org.glowacki.core.dungen.RoomException;
import org.glowacki.core.util.IRandom;

import org.glowacki.core.test.MockRandom;

import org.junit.*;
import static org.junit.Assert.*;

class CPoint
    implements Comparable
{
    private int x;
    private int y;
    private GeneratorException exc;

    CPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int compareTo(Object obj)
    {
        if (obj == null) {
            return 1;
        } else if (!(obj instanceof CPoint)) {
            return obj.getClass().getName().compareTo(getClass().getName());
        }

        CPoint other = (CPoint) obj;
        int val = other.getX() - getX();
        if (val == 0) {
            val = other.getY() - getY();
        }

        return val;
    }

    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    public GeneratorException getException()
    {
        return exc;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean hasException()
    {
        return exc != null;
    }

    public int hashCode()
    {
        return (x & 0xffff) << 16 + (y & 0xffff);
    }
}

class CRoom
    implements IRoom
{
    private int x;
    private int y;
    private int width;
    private int height;

    CRoom()
    {
        this(0, 0, 10, 10);
    }

    CRoom(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addStairs(int i0, int i1, boolean b2)
        throws RoomException
    {
        throw new Error("Unimplemented");
    }

    public void changeHeight(int i0)
    {
        throw new Error("Unimplemented");
    }

    public void changeWidth(int i0)
    {
        throw new Error("Unimplemented");
    }

    public void decX()
    {
        x--;
    }

    public void decY()
    {
        y--;
    }

    public char getChar()
    {
        throw new Error("Unimplemented");
    }

    public int getHeight()
    {
        return height;
    }

    public int getNumber()
    {
        throw new Error("Unimplemented");
    }

    public int getStaircaseX()
    {
        throw new Error("Unimplemented");
    }

    public int getStaircaseY()
    {
        throw new Error("Unimplemented");
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
        throw new Error("Unimplemented");
    }

    public void incX()
    {
        x++;
    }

    public void incY()
    {
        y++;
    }

    public boolean isUpStaircase()
    {
        throw new Error("Unimplemented");
    }
}

class CMapArray
    implements IMapArray
{
    private ArrayList<CPoint> doors = new ArrayList<CPoint>();
    private ArrayList<CPoint> stairs = new ArrayList<CPoint>();
    private ArrayList<CPoint> tunnel = new ArrayList<CPoint>();

    public void addDoor(int x, int y)
        throws GeneratorException
    {
        for (CPoint p : doors) {
            if (p.getX() == x && p.getY() == y) {
                if (p.hasException()) {
                    throw p.getException();
                }

                doors.remove(p);
                return;
            }
        }

        throw new Error(String.format("Bad door %d,%d", x, y));
    }

    public void addExpectedDoor(int x, int y)
    {
        doors.add(new CPoint(x, y));
    }

    public void addExpectedStairs(int x, int y)
    {
        stairs.add(new CPoint(x, y));
    }

    public void addExpectedTunnel(int x, int y)
    {
        tunnel.add(new CPoint(x, y));
    }

    public void addStaircase(int x, int y, boolean isUp)
        throws GeneratorException
    {
        for (CPoint p : stairs) {
            if (p.getX() == x && p.getY() == y) {
                if (p.hasException()) {
                    throw p.getException();
                }

                stairs.remove(p);
                return;
            }
        }

        throw new Error(String.format("Bad staircase %d,%d", x, y));
    }

    public String[] getStrings()
    {
        throw new Error("Unimplemented");
    }

    boolean hasExpectedData()
    {
        return doors.size() > 0 || stairs.size() > 0 || tunnel.size() > 0;
    }

    public void show()
    {
        throw new Error("Unimplemented");
    }

    public void show(PrintStream x0)
    {
        throw new Error("Unimplemented");
    }

    public void tunnel(int x, int y)
        throws GeneratorException
    {
        for (CPoint p : tunnel) {
            if (p.getX() == x && p.getY() == y) {
                if (p.hasException()) {
                    throw p.getException();
                }

                tunnel.remove(p);
                return;
            }
        }

        throw new Error(String.format("Bad tunnel %d,%d", x, y));
    }
}

public class ConnectionTest
{
    @Test
    public void testMatches()
    {
        IRoom r1 = new CRoom();
        IRoom r2 = new CRoom();
        IRoom r3 = new CRoom();

        Connection c1 = new Connection(r1, r2);
        assertTrue("Compare failed", c1.matches(r1, r2));
        assertTrue("Compare failed", c1.matches(r2, r1));
        assertFalse("Compare failed", c1.matches(r1, r3));
        assertFalse("Compare failed", c1.matches(r2, r3));
    }

    @Test
    public void testTunnelEW()
        throws GeneratorException
    {
        MockRandom random = new MockRandom();
        CMapArray map = new CMapArray();

        for (int i = 0; i < 4; i++) {
            if ((i & 2) == 0) {
                random.add(0);
                random.add(1);

                map.addExpectedDoor(9, 1);
                map.addExpectedDoor(12, 2);
                map.addExpectedTunnel(10, 1);
                map.addExpectedTunnel(11, 1);
                map.addExpectedTunnel(11, 1);
                map.addExpectedTunnel(11, 2);
            } else {
                random.add(5);
                random.add(9);

                map.addExpectedDoor(9, 6);
                map.addExpectedDoor(12, 2);
                map.addExpectedTunnel(10, 6);
                map.addExpectedTunnel(11, 6);
                map.addExpectedTunnel(11, 2);
                map.addExpectedTunnel(11, 3);
                map.addExpectedTunnel(11, 4);
                map.addExpectedTunnel(11, 5);
                map.addExpectedTunnel(11, 6);
            }

            CRoom r1 = new CRoom(12, 0, 10, 10);
            CRoom r2 = new CRoom(0, 0, 10, 10);
            Connection c1;
            if ((i & 1) == 0) {
                c1 = new Connection(r1, r2);
            } else {
                c1 = new Connection(r2, r1);
            }

            c1.tunnel(random, map);
        }

        assertFalse("random has more data", random.hasData());
        assertFalse("map has more expected data", map.hasExpectedData());
    }

    @Test
    public void testTunnelNS()
        throws GeneratorException
    {
        MockRandom random = new MockRandom();
        CMapArray map = new CMapArray();
        CRoom r1 = new CRoom(0, 0, 10, 10);
        CRoom r2 = new CRoom(0, 12, 10, 10);

        for (int i = 0; i < 4; i++) {
            if ((i & 2) == 0) {
                random.add(0);
                random.add(1);

                map.addExpectedDoor(1, 9);
                map.addExpectedDoor(2, 12);
                map.addExpectedTunnel(1, 10);
                map.addExpectedTunnel(1, 11);
                map.addExpectedTunnel(1, 11);
                map.addExpectedTunnel(2, 11);
            } else {
                random.add(5);
                random.add(9);

                map.addExpectedDoor(6, 9);
                map.addExpectedDoor(2, 12);
                map.addExpectedTunnel(6, 10);
                map.addExpectedTunnel(6, 11);
                map.addExpectedTunnel(2, 11);
                map.addExpectedTunnel(3, 11);
                map.addExpectedTunnel(4, 11);
                map.addExpectedTunnel(5, 11);
                map.addExpectedTunnel(6, 11);
            }

            Connection c1;
            if ((i & 1) == 0) {
                c1 = new Connection(r1, r2);
            } else {
                c1 = new Connection(r2, r1);
            }

            c1.tunnel(random, map);
        }

        assertFalse("random has more data", random.hasData());
        assertFalse("map has more expected data", map.hasExpectedData());
    }
}
