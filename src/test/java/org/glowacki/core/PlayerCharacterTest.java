package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;

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
        final int spd = 11;

        ICharacter ch = new PlayerCharacter(name, str, dex, spd);
        assertEquals("Bad name", ch.getName(), name);

        assertTrue("Bad isPlayer() value", ch.isPlayer());

        String expStr = String.format("%s(%d/%d/%d", name, str, dex, spd);
        assertTrue("Bad character string " + ch,
                   ch.toString().startsWith(expStr));
    }

    public void testLevel()
        throws CoreException
    {
        ICharacter ch = new PlayerCharacter("joe", 1, 2, 3);
        assertNull("Initial level is not null", ch.getLevel());

        Map map = new Map(MapBuilder.buildMap(-1, -1, -1, -2));
        Level lvl = new Level("empty", map);

        ch.setLevel(lvl);
        assertNotNull("Level is null", ch.getLevel());
        assertEquals("Bad level", lvl, ch.getLevel());
    }

    public void testTakeTurn()
    {
        ICharacter ch = new PlayerCharacter("bob", 1, 2, 3);

        try {
            ch.takeTurn();
            fail("This method should not be implemented");
        } catch (UnimplementedError err) {
            // expect this to fail
        }
    }

    public void testMoveNoLevel()
    {
        ICharacter ch = new PlayerCharacter("foo", 1, 2, 3);

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
            ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

    public void testDescend()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 3));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10);

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

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
