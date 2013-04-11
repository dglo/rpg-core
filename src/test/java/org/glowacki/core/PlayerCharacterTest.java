package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;
import org.glowacki.core.test.MockCharacter;

public class PlayerCharacterTest
    extends TestCase
{
    public PlayerCharacterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(PlayerCharacterTest.class);
    }

    public void testCreate()
    {
        final String name = "foo";
        final int str = 9;
        final int dex = 10;
        final int pcp = 11;
        final int spd = 12;

        ICharacter ch = new PlayerCharacter(name, str, dex, pcp, spd);
        assertEquals("Bad name", ch.getName(), name);

        assertTrue("Bad isPlayer() value", ch.isPlayer());

        String expStr =
            String.format("%s(%d/%d/%d/%d", name, str, dex, pcp, spd);
        assertTrue("Bad character string " + ch + " (expected " + expStr + ")",
                   ch.toString().startsWith(expStr));
    }

    public void testLevel()
        throws CoreException
    {
        ICharacter ch = new PlayerCharacter("joe", 1, 2, 3, 4);
        assertNull("Initial level is not null", ch.getLevel());

        Map map = new Map(MapBuilder.buildMap(-1, -1, -1, -2));
        Level lvl = new Level("empty", map);

        ch.setLevel(lvl);
        assertNotNull("Level is null", ch.getLevel());
        assertEquals("Bad level", lvl, ch.getLevel());
    }

    public void testTakeTurn()
    {
        ICharacter ch = new PlayerCharacter("bob", 1, 2, 3, 4);

        try {
            ch.takeTurn();
            fail("This method should not be implemented");
        } catch (UnimplementedError err) {
            // expect this to fail
        }
    }

    public void testMoveNoLevel()
    {
        ICharacter ch = new PlayerCharacter("foo", 1, 2, 3, 4);

        try {
            ch.move(Direction.LEFT);
            fail("Move without a level should not succeed");
        } catch (CoreException ex) {
            assertEquals("Bad exception message",
                         "Level cannot be null", ex.getMessage());
        }
    }

    public void testMove2D()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, -1, -1));

        Direction dir = Direction.LEFT;
        do {
            ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

            Level lvl = new Level("empty", map);
            lvl.enterDown(ch);

            int turns;
            try {
                turns = ch.move(dir);
            } catch (CoreException ex) {
                fail(ch.getName() + " move(" + dir + ") threw " + ex);
                continue;
            }

            int expTurns;
            if (dir == Direction.LEFT_UP || dir == Direction.RIGHT_UP ||
                dir == Direction.LEFT_DOWN || dir == Direction.RIGHT_DOWN)
            {
                expTurns = 2;
            } else {
                expTurns = 1;
            }

            assertEquals("Bad number of turns", expTurns, turns);

            dir = dir.next();
        } while (dir != Direction.LEFT);
    }

    public void testClimb()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        bottomLvl.enterDown(ch);

        Direction dir = Direction.CLIMB;

        int turns;
        try {
            turns = ch.move(dir);
        } catch (CoreException ex) {
            fail(ch.getName() + " move(" + dir + ") threw " + ex);
        }
    }

    public void testNoClimbOnUp()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        bottomLvl.enterUp(ch);

        Direction dir = Direction.CLIMB;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Should not be able to climb here");
        } catch (CoreException ex) {
            final String expMsg = "You cannot climb here";
            assertEquals("Bad exception", expMsg, ex.getMessage());
        }
    }

    public void testNoClimbOnTopLevel()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);

        topLvl.enterDown(ch);

        Direction dir = Direction.CLIMB;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Should not be able to climb here");
        } catch (CoreException ex) {
            final String expMsg = "You cannot exit here";
            assertEquals("Bad exception", expMsg, ex.getMessage());
        }
    }

    public void testNoClimbToOccupied()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        bottomLvl.enterDown(ch);

        final Level expLvl = ch.getLevel();
        final int expX = ch.getX();
        final int expY = ch.getY();

        MockCharacter squatter = new MockCharacter("squatter");

        topLvl.enterUp(squatter);

        Direction dir = Direction.CLIMB;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Climb to occupied square should not succeed");
        } catch (CoreException ex) {
            assertNotNull("Null exception message", ex.getMessage());

            final String msg = "Down staircase is occupied";
            assertEquals("Bad message", msg, ex.getMessage());
        }

        assertEquals("Bad level", expLvl, ch.getLevel());
        assertEquals("Bad X coord", expX, ch.getX());
        assertEquals("Bad Y coord", expY, ch.getY());
    }

    public void testDescend()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        topLvl.enterUp(ch);

        Direction dir = Direction.DESCEND;

        int turns;
        try {
            turns = ch.move(dir);
        } catch (CoreException ex) {
            fail(ch.getName() + " move(" + dir + ") threw " + ex);
        }
    }

    public void testNoDescendOnDown()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        topLvl.enterDown(ch);

        Direction dir = Direction.DESCEND;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Should not be able to descend here");
        } catch (CoreException ex) {
            final String expMsg = "You cannot descend here";
            assertEquals("Bad exception", expMsg, ex.getMessage());
        }
    }

    public void testNoDescendOnBottomLevel()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);

        topLvl.enterUp(ch);

        Direction dir = Direction.DESCEND;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Should not be able to descend here");
        } catch (CoreException ex) {
            final String expMsg = "You are at the bottom";
            assertEquals("Bad exception", expMsg, ex.getMessage());
        }
    }

    public void testNoDescendToOccupied()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level topLvl = new Level("top", map);
        Level bottomLvl = new Level("bottom", map);
        topLvl.addNextLevel(bottomLvl);

        topLvl.enterUp(ch);

        final Level expLvl = ch.getLevel();
        final int expX = ch.getX();
        final int expY = ch.getY();

        MockCharacter squatter = new MockCharacter("squatter");

        bottomLvl.enterDown(squatter);

        Direction dir = Direction.DESCEND;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Descend to occupied square should not succeed");
        } catch (CoreException ex) {
            assertNotNull("Null exception message", ex.getMessage());

            final String msg = "Up staircase is occupied";
            assertEquals("Bad message", msg, ex.getMessage());
        }

        assertEquals("Bad level", expLvl, ch.getLevel());
        assertEquals("Bad X coord", expX, ch.getX());
        assertEquals("Bad Y coord", expY, ch.getY());
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
