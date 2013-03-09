package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

class MockMovable
    implements MovableCharacter
{
    private String name;
    private int x;
    private int y;

    public MockMovable(String name)
    {
        this.name = name;
    }

    public Level getLevel()
    {
        throw new Error("Unimplemented");
    }

    public String getName()
    {
        return name;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int move(MovableCharacter.Direction x0)
        throws LevelException
    {
        throw new Error("Unimplemented");
    }

    public void position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}

public class LevelTest
    extends TestCase
{
    public LevelTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(LevelTest.class);
    }

    public void testCreate()
        throws CoreException
    {
        final String name = "Create";
        final String[] map = new String[] {
            "---",
            "|.|",
            "---",
        };

        Level lvl = new Level(name, map);
        assertEquals("Bad name", name, lvl.getName());
        assertEquals("Bad max X", map[0].length() - 1, lvl.getMaxX());
        assertEquals("Bad max Y", map.length - 1, lvl.getMaxY());
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());
        assertNotNull("Null string", lvl.toString());
    }

    public void testBadCreate()
        throws CoreException
    {
        try {
            new Level(null, null);
            fail("Should not be able to create level from null map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message", "Null map", ce.getMessage());
        }

        try {
            new Level(null, new String[0]);
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [0, ?]", ce.getMessage());
        }

        try {
            new Level(null, new String[] { null, });
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [1, ?]", ce.getMessage());
        }

        try {
            new Level(null, new String[] { "", });
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [1, 0]", ce.getMessage());
        }
    }

    public void testBuild()
        throws CoreException
    {
        String[] map = new String[] {
            "------",
            "|....|                  ----------",
            "|....+#######           |.....>..|",
            "|.<..|      ############+......~.|",
            "|....|                  |.X....~~|",
            "------                  ----------",
            null,
        };

        Level lvl = new Level("Sample", map);

        String[] pic = lvl.getPicture().split("\n");
        assertEquals("Bad number of lines", pic.length, map.length);

        for (int i = 0; i < pic.length; i++) {
            String mtrim;
            if (map[i] == null) {
                mtrim = "";
            } else {
                mtrim = map[i].replaceAll("\\s+$", "").replaceAll("X", " ");
            }

            String ptrim = pic[i].replaceAll("\\s+$", "");

            assertEquals("Bad line #" + i, ptrim, mtrim);
        }
    }

    public void testMultiLevel()
        throws CoreException
    {
        String[] map1 = new String[] {
            "-----",
            "|...|",
            "|.>.|",
            "|...|",
            "-----",
        };

        String[] map2 = new String[] {
            "-----",
            "|...|",
            "|.<.|",
            "|...|",
            "-----",
        };

        Level l1 = new Level("1", map1);

        try {
            l1.addNextLevel(null);
            fail("Should not be able to add null level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Next level cannot be null", ce.getMessage());
        }

        Level l2 = new Level("2", map2);

        assertNull("Next level for level 1 is not null", l1.getNextLevel());
        assertNull("Previous level for level 1 is not null",
                   l1.getPreviousLevel());
        assertNull("Next level for level 2 is not null", l2.getNextLevel());
        assertNull("Previous level for level 2 is not null",
                   l2.getPreviousLevel());

        l1.addNextLevel(l2);

        assertEquals("Bad next level for level 1",
                     l2, l1.getNextLevel());
        assertNull("Previous level for level 1 is not null",
                   l1.getPreviousLevel());
        assertNull("Next level for level 2 is not null", l2.getNextLevel());
        assertEquals("Bad previous level for level 2",
                     l1, l2.getPreviousLevel());

        Level l3 = new Level("Extra", map2);

        try {
            l1.addNextLevel(l3);
            fail("Should not be able to add multiple levels");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Cannot overwrite existing level", ce.getMessage());
        }

        try {
            l3.addNextLevel(l2);
            fail("Should not be able to add level multiple times");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Cannot overwrite previous level", ce.getMessage());
        }
    }

    public void testGet()
        throws CoreException
    {
        StringBuilder buf = new StringBuilder();
        for (Terrain t : Terrain.values()) {
            buf.append(Terrain.getCharacter(t));
        }

        String[] map = new String[] { buf.toString(), };

        Level lvl = new Level("MapValues", map);

        int n = 0;
        for (Terrain t : Terrain.values()) {
            Terrain actual = lvl.get(n, 0);
            assertEquals("Bad terrain for " + map[0].charAt(n), t, actual);
            n++;
        }

        for (int i = 0; i < 4; i++) {
            int x, y;
            String expMsg;
            switch (i) {
            case 0:
                x = 0;
                y = -1;
                expMsg = String.format("Bad Y coordinate in (%d,%d)," +
                                       " max is %d", x, y, lvl.getMaxY());
                break;
            case 1:
                x = 0;
                y = 99;
                expMsg = String.format("Bad Y coordinate in (%d,%d)," +
                                       " max is %d", x, y, lvl.getMaxY());
                break;
            case 2:
                x = -1;
                y = 0;
                expMsg = String.format("Bad X coordinate in (%d,%d)," +
                                       " max is %d", x, y, lvl.getMaxX());
                break;
            case 3:
                x = 99;
                y = 0;
                expMsg = String.format("Bad X coordinate in (%d,%d)," +
                                       " max is %d", x, y, lvl.getMaxX());
                break;
            default:
                fail("There is no choice for " + i);
                // not reachable, but compiler doesn't know that
                x = -1;
                y = -1;
                expMsg = null;
                break;
            }

            final String coordStr = "(" + x + "," + y + ")";

            try {
                lvl.get(x, y);
                fail("Should not have terrain for " + coordStr);
            } catch (CoreException ce) {
                assertNotNull("Null exception message when getting " +
                              coordStr, ce.getMessage());
                assertEquals("Bad exception when getting " + coordStr,
                             expMsg, ce.getMessage());
            }
        }
    }

    public void testEnterExit()
        throws CoreException
    {
        String[] map = new String[] {
            "------",
            "|....|",
            "|.<>.|",
            "|....|",
            "------",
        };

        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        Level lvl = new Level("enterExit", map);
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        MockMovable ch = new MockMovable("joe");

        lvl.enterDown(ch);
        assertEquals("Bad X from enterDown", upX, ch.getX());
        assertEquals("Bad Y from enterDown", row, ch.getY());
        assertEquals("Empty character list", 1, lvl.getCharacters().size());

        lvl.exit(ch);
        assertEquals("Bad X after exit", -1, ch.getX());
        assertEquals("Bad Y after exit", -1, ch.getY());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        lvl.enterUp(ch);
        assertEquals("Bad X from enterDown", downX, ch.getX());
        assertEquals("Bad Y from enterDown", row, ch.getY());
        assertEquals("Empty character list", 1, lvl.getCharacters().size());

        lvl.exit(ch);
        assertEquals("Bad X after exit", -1, ch.getX());
        assertEquals("Bad Y after exit", -1, ch.getY());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());
    }

    public void testNoEnterExit()
        throws CoreException
    {
        String[] map = new String[] {
            "------",
            "|....|",
            "|....|",
            "|....|",
            "------",
        };

        Level lvl = new Level("godot", map);

        final String name = "xyz";

        MockMovable ch = new MockMovable(name);

        try {
            lvl.enterDown(ch);
            fail("Should not be able to enter level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map has no up staircase", ce.getMessage());
        }

        try {
            lvl.enterUp(ch);
            fail("Should not be able to enter level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map has no down staircase", ce.getMessage());
        }

        try {
            lvl.exit(ch);
            fail("Should not be able to exit level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         name + " was not on this level", ce.getMessage());
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
