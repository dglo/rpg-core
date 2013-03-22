package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;

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

        Level lvl = new Level(name, new Map(map));
        assertEquals("Bad name", name, lvl.getName());
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());
        assertNotNull("Null string", lvl.toString());
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

        String[] built = MapBuilder.buildMap(upX, row, downX, row);
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

        String[] built1 = MapBuilder.buildMap(-1, -1, 2, 2);
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

        String[] built2 = MapBuilder.buildMap(2, 2, -1, -1);
        assertNotNull("buildMap() returned null", built2);
        assertEquals("Bad map size", map2.length, built2.length);

        for (int i = 0; i < map2.length; i++) {
            assertEquals("Bad map line #" + i, map2[i], built2[i]);
        }
    }

/*
    public void testBadCreate()
        throws CoreException
    {
        try {
            new Level(null, null, 0L);
            fail("Should not be able to create level from null map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message", "Null map", ce.getMessage());
        }

        try {
            new Level(null, new String[0], 0L);
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [0, ?]", ce.getMessage());
        }

        try {
            new Level(null, new String[] { null, }, 0L);
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [1, ?]", ce.getMessage());
        }

        try {
            new Level(null, new String[] { "", }, 0L);
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Bad map dimensions [1, 0]", ce.getMessage());
        }
    }

    public void testMultiLevel()
        throws CoreException
    {
        Level l1 = new Level("1", MapBuilder.buildMap(-1, -1, 2, 2), 0L);

        try {
            l1.addNextLevel(null);
            fail("Should not be able to add null level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Next level cannot be null", ce.getMessage());
        }

        Level l2 = new Level("2", MapBuilder.buildMap(2, 2, -1, -1), 0L);

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

        Level l3 = new Level("Extra", MapBuilder.buildMap(4, 3, 2, 1), 0L);

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

    public void testEnter()
        throws CoreException
    {
        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        Level lvl = new Level("enterExit", MapBuilder.buildMap(upX, row, downX, row), 0L);
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

        Level lvl = new Level("godot", map, 0L);

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

        Level lvl = new Level("enterExit", MapBuilder.buildMap(upX, row, downX, row), 0L);
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        ICharacter real = new MockCharacter("joe", 3, 4, 5);

        ICharacter ch = lvl.enterDown(real);
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
        final long seed = 1234L;

        Level top = new Level("top", MapBuilder.buildMap(upX, row, downX, row), seed);
        Level bottom = new Level("bottom", MapBuilder.buildMap(upX, row, downX, row),
                                                    seed + 1);
        top.addNextLevel(bottom);

        ICharacter real = new MockCharacter("joe", 3, 4, 5);

        ICharacter ch = top.enterDown(real);
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
*/

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
