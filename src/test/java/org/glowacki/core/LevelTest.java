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

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        throw new Error("Unimplemented");
    }
}

public class LevelTest
    extends TestCase
{
    public LevelTest(String name)
    {
        super(name);
    }

    private String[] buildMap(int upX, int upY, int downX, int downY)
        throws CoreException
    {
        if (upX == 0) {
            throw new CoreException("Up X " + upX +
                                    " would be embedded in the wall");
        } else if (downX == 0) {
            throw new CoreException("Down X " + downX +
                                    " would be embedded in the wall");
        } else if (upY == 0) {
            throw new CoreException("Up Y " + upY +
                                    " would be embedded in the wall");
        } else if (downY == 0) {
            throw new CoreException("Down Y " + downY +
                                    " would be embedded in the wall");
        } else if (upX == downX && upY == downY) {
            final String fmt = "Up (%d,%d) and down (%d,%d) are the same";
            throw new CoreException(String.format(fmt, upX, upY, downX, downY));
        }

        int maxX = (upX > downX ? upX : downX);
        int maxY = (upY > downY ? upY : downY);

        String[] map = new String[maxY + 3];

        final String wallFmt = String.format("%%%ds", maxX + 3);
        final String allWall = String.format(wallFmt, "").replace(' ', '-');

        StringBuilder buf = new StringBuilder(maxX + 3);
        for (int i = 0; i < map.length; i++) {
            if (i == 0 || i == map.length - 1) {
                map[i] = allWall;
                continue;
            }

            buf.setLength(0);
            buf.append('|');

            int floorEnd = maxX + 1;
            for (int j = 1; j < maxX + 2; j++) {
                if (i == upY && upX == j) {
                    buf.append('<');
                } else if (i == downY && downX == j) {
                    buf.append('>');
                } else {
                    buf.append('.');
                }
            }

            buf.append('|');

            map[i] = buf.toString();
        }

        return map;
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

    public void testBuildMap()
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

        String[] built = buildMap(upX, row, downX, row);
        assertNotNull("buildMap() returned null", built);
        assertEquals("Bad map size", map.length, built.length);

        for (int i = 0; i < map.length; i++) {
            assertEquals("Bad map line #" + i, map[i], built[i]);
        }

        String[] map1 = new String[] {
            "-----",
            "|...|",
            "|.>.|",
            "|...|",
            "-----",
        };

        String[] built1 = buildMap(-1, -1, 2, 2);
        assertNotNull("buildMap() returned null", built1);
        assertEquals("Bad map size", map1.length, built1.length);

        for (int i = 0; i < map1.length; i++) {
            assertEquals("Bad map line #" + i, map1[i], built1[i]);
        }

        String[] map2 = new String[] {
            "-----",
            "|...|",
            "|.<.|",
            "|...|",
            "-----",
        };

        String[] built2 = buildMap(2, 2, -1, -1);
        assertNotNull("buildMap() returned null", built2);
        assertEquals("Bad map size", map2.length, built2.length);

        for (int i = 0; i < map2.length; i++) {
            assertEquals("Bad map line #" + i, map2[i], built2[i]);
        }
    }

    public void testMultiLevel()
        throws CoreException
    {
        Level l1 = new Level("1", buildMap(-1, -1, 2, 2));

        try {
            l1.addNextLevel(null);
            fail("Should not be able to add null level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Next level cannot be null", ce.getMessage());
        }

        Level l2 = new Level("2", buildMap(2, 2, -1, -1));

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

        Level l3 = new Level("Extra", buildMap(4, 3, 2, 1));

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

    public void testEnter()
        throws CoreException
    {
        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        Level lvl = new Level("enterExit", buildMap(upX, row, downX, row));
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

    public void testEnterChar()
        throws CoreException
    {
        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        Level lvl = new Level("enterExit", buildMap(upX, row, downX, row));
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        Character real = new MockCharacter("joe", 3, 4, 5);

        MovableCharacter ch = lvl.enterDown(real);
        assertEquals("Bad X from enterDown", upX, ch.getX());
        assertEquals("Bad Y from enterDown", row, ch.getY());
        assertEquals("Bad character list", 1, lvl.getCharacters().size());

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

    public void testMoveChar()
        throws CoreException
    {
        final int row = 1;
        final int upX = 1;
        final int downX = 2;

        Level top = new Level("top", buildMap(upX, row, downX, row));
        Level bottom = new Level("bottom", buildMap(upX, row, downX, row));
        top.addNextLevel(bottom);

        Character real = new MockCharacter("joe", 3, 4, 5);

        MovableCharacter ch = top.enterDown(real);
        assertEquals("Bad X from enterDown", upX, ch.getX());
        assertEquals("Bad Y from enterDown", row, ch.getY());
        assertEquals("Bad level from enterDown", top, ch.getLevel());
        assertEquals("Bad initial character list",
                     1, top.getCharacters().size());
        assertEquals("Bad initial character list",
                     0, bottom.getCharacters().size());

        try {
            ch.move(MovableCharacter.Direction.CLIMB);
            fail("Should not be able to ascend from upper level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "You cannot exit here", ce.getMessage());
        }

        try {
            ch.move(MovableCharacter.Direction.DESCEND);
            fail("Should not be able to descend from up staircase");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "You cannot descend here", ce.getMessage());
        }

        ch.move(MovableCharacter.Direction.LEFT);
        assertEquals("Bad X after wall move", upX, ch.getX());
        assertEquals("Bad Y after wall move", row, ch.getY());
        assertEquals("Bad level after wall move", top, ch.getLevel());

        ch.move(MovableCharacter.Direction.RIGHT);
        assertEquals("Bad X after move right", downX, ch.getX());
        assertEquals("Bad Y after move right", row, ch.getY());
        assertEquals("Bad level after move right", top, ch.getLevel());

        try {
            ch.move(MovableCharacter.Direction.CLIMB);
            fail("Should not be able to ascend from down staircase");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "You cannot climb here", ce.getMessage());
        }

        ch.move(MovableCharacter.Direction.DESCEND);
        assertEquals("Bad X after descent", upX, ch.getX());
        assertEquals("Bad Y after descent", row, ch.getY());
        assertEquals("Bad level after descent", bottom, ch.getLevel());
        assertEquals("Bad character list after descent",
                     0, top.getCharacters().size());
        assertEquals("Bad character list after descent",
                     1, bottom.getCharacters().size());

        ch.move(MovableCharacter.Direction.RIGHT);
        assertEquals("Bad X after move right", downX, ch.getX());
        assertEquals("Bad Y after move right", row, ch.getY());
        assertEquals("Bad level after move right", bottom, ch.getLevel());

        try {
            ch.move(MovableCharacter.Direction.DESCEND);
            fail("Should not be able to descend from lower level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "You are at the bottom", ce.getMessage());
        }

        ch.move(MovableCharacter.Direction.LEFT);
        assertEquals("Bad X after move left", upX, ch.getX());
        assertEquals("Bad Y after move left", row, ch.getY());
        assertEquals("Bad level after move left", bottom, ch.getLevel());

        ch.move(MovableCharacter.Direction.CLIMB);
        assertEquals("Bad X after ascent", downX, ch.getX());
        assertEquals("Bad Y after ascent", row, ch.getY());
        assertEquals("Bad level after ascent", top, ch.getLevel());
        assertEquals("Bad character list after ascent",
                     1, top.getCharacters().size());
        assertEquals("Bad character list after ascent",
                     0, bottom.getCharacters().size());

    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
