package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MockCharacter;
import org.glowacki.core.test.MockMap;

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

    public void testNextLevel()
        throws CoreException
    {
        MockMap map1 = new MockMap(2, 2);
        Level l1 = new Level("1", map1);

        try {
            l1.addNextLevel(null);
            fail("Should not be able to add null level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Next level cannot be null", ce.getMessage());
        }

        MockMap map2 = new MockMap(2, 2);
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

        MockMap map3 = new MockMap(5, 4);
        Level l3 = new Level("Extra", map3);

        try {
            l1.addNextLevel(l3);
            fail("Should not be able to add multiple levels");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Level " + l1.getName() + " already has a next level",
                         ce.getMessage());
        }

        try {
            l3.addNextLevel(l2);
            fail("Should not be able to add level multiple times");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Level " + l2.getName() +
                         " already has a previous level", ce.getMessage());
        }
    }

    public void testPreviousLevel()
        throws CoreException
    {
        MockMap map1 = new MockMap(3, 3);
        Level l1 = new Level("1", map1);

        try {
            l1.addPreviousLevel(null);
            fail("Should not be able to add null level");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Previous level cannot be null", ce.getMessage());
        }

        MockMap map2 = new MockMap(4, 4);
        Level l2 = new Level("2", map2);

        assertNull("Previous level for level 1 is not null",
                   l1.getPreviousLevel());
        assertNull("Next level for level 1 is not null",
                   l1.getNextLevel());
        assertNull("Previous level for level 2 is not null",
                   l2.getPreviousLevel());
        assertNull("Next level for level 2 is not null",
                   l2.getNextLevel());

        l1.addPreviousLevel(l2);

        assertEquals("Bad previous level for level 1",
                     l2, l1.getPreviousLevel());
        assertNull("Next level for level 1 is not null",
                   l1.getNextLevel());
        assertNull("Previous level for level 2 is not null",
                   l2.getPreviousLevel());
        assertEquals("Bad next level for level 2",
                     l1, l2.getNextLevel());

        MockMap map3 = new MockMap(5, 4);
        Level l3 = new Level("Extra", map3);

        try {
            l1.addPreviousLevel(l3);
            fail("Should not be able to add multiple levels");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Level " + l1.getName() +
                         " already has a previous level",
                         ce.getMessage());
        }

        try {
            l3.addPreviousLevel(l2);
            fail("Should not be able to add level multiple times");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Level " + l2.getName() +
                         " already has a next level", ce.getMessage());
        }
    }

    public void testEnter()
        throws CoreException
    {
        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        MockMap map = new MockMap(downX + 2, row + 2);
        map.setUpStaircase(upX, row);
        map.setDownStaircase(downX, row);

        Level lvl = new Level("enterExit", map);
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        MockCharacter ch = new MockCharacter("joe");

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
        MockMap map = new MockMap(5, 5);
        Level lvl = new Level("godot", map);

        final String name = "xyz";

        MockCharacter ch = new MockCharacter(name);

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
                         "Character " + name + " is not on level " +
                         lvl.getName(), ce.getMessage());
        }
    }

    public void testEnterChar()
        throws CoreException
    {
        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        MockMap map = new MockMap(downX + 2, row + 2);
        map.setUpStaircase(upX, row);
        map.setDownStaircase(downX, row);

        Level lvl = new Level("enterExit", map);
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     0, lvl.getCharacters().size());

        ICharacter ch = new MockCharacter("joe");

        lvl.enterDown(ch);
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

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
