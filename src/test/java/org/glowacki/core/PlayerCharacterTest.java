package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;
import org.glowacki.core.test.MockCharacter;

class PlayerPoint
    implements IMapPoint
{
    private int x;
    private int y;

    PlayerPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public String toString()
    {
        return String.format("[%d,%d]", x, y);
    }
}

public class PlayerCharacterTest
    extends TestCase
{
    public PlayerCharacterTest(String name)
    {
        super(name);
    }

    private void runPath(ICharacter ch, int fromX, int fromY, int toX, int toY,
                         int numMoves)
        throws CoreException
    {
        ch.getLevel().getMap().removeObject(ch);
        ch.getLevel().getMap().insertObject(ch, fromX, fromY);

        ch.setPosition(fromX, fromY);

        try {
            ch.movePath();
            fail("Expect this to fail");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Unexpected exception",
                         "No current path", ce.getMessage());
        }

        assertEquals("Bad starting X", fromX, ch.getX());
        assertEquals("Bad starting Y", fromY, ch.getY());

        ch.buildPath(new PlayerPoint(toX, toY));

        int moves = 0;
        while (ch.hasPath()) {
            int rtnval = ch.movePath();
            assertFalse("Move failed", rtnval < 0);
            moves++;
        }

        assertEquals(String.format("Bad number of moves from %d,%d to %d,%d",
                                   fromX, fromY, toX, toY),  numMoves, moves);
        assertEquals("Bad final X", toX, ch.getX());
        assertEquals("Bad final Y", toY, ch.getY());
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
                         "Level has not been set for " + ch.getName(),
                         ex.getMessage());
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

        final ILevel expLvl = ch.getLevel();
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

        final ILevel expLvl = ch.getLevel();
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

    public void testSeenArray()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 4, 4));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level lvl = new Level("level", map);

        assertNull("'Seen' array is not null", ch.getSeenArray());

        lvl.enterDown(ch);

        boolean[][] seen = ch.getSeenArray();
        assertNotNull("'Seen' array should not be null", seen);
        assertEquals("Bad 'seen' width", lvl.getMaxX() + 1, seen.length);
        assertEquals("Bad 'seen' height", lvl.getMaxY() + 1, seen[0].length);

        for (int x = 0; x < seen.length; x++) {
            for (int y = 0; y < seen[0].length; y++) {
                assertFalse(String.format("seen[%d][%d] is not false", x, y),
                            seen[x][y]);
            }
        }

        // refetch to fill out branch coverage
        seen = ch.getSeenArray();
        assertNotNull("'Seen' array should not be null", seen);
        assertEquals("Bad 'seen' width", lvl.getMaxX() + 1, seen.length);
        assertEquals("Bad 'seen' height", lvl.getMaxY() + 1, seen[0].length);
    }

    public void testBuildPathBad()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, 2, 4));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        Level lvl = new Level("level", map);

        lvl.enterDown(ch);
        assertFalse("Player should not have a defined path", ch.hasPath());

        try {
            ch.buildPath(new PlayerPoint(-1, 0));
            fail("This should not succeed");
        } catch (CoreException ce) {
            assertNotNull("Exception message is null", ce.getMessage());
            assertTrue("Unexpected exception " + ce,
                       ce.getMessage().startsWith("Bad goal "));
        }

        try {
            ch.buildPath(new PlayerPoint(0, -1));
            fail("This should not succeed");
        } catch (CoreException ce) {
            assertNotNull("Exception message is null", ce.getMessage());
            assertTrue("Unexpected exception " + ce,
                       ce.getMessage().startsWith("Bad goal "));
        }

        try {
            ch.buildPath(new PlayerPoint(lvl.getMaxX() + 1, 1));
            fail("This should not succeed");
        } catch (CoreException ce) {
            assertNotNull("Exception message is null", ce.getMessage());
            assertTrue("Unexpected exception " + ce,
                       ce.getMessage().startsWith("Bad goal "));
        }

        try {
            ch.buildPath(new PlayerPoint(1, lvl.getMaxY() + 1));
            fail("This should not succeed");
        } catch (CoreException ce) {
            assertNotNull("Exception message is null", ce.getMessage());
            assertTrue("Unexpected exception " + ce,
                       ce.getMessage().startsWith("Bad goal "));
        }
    }

    public void testClearPath()
        throws CoreException
    {
        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        assertFalse("Player should not have a defined path", ch.hasPath());

        ch.clearPath();
        assertFalse("Player should not have a defined path", ch.hasPath());

        PlayerPoint goal = new PlayerPoint(4, 4);

        try {
            ch.buildPath(goal);
            fail("This should not succeed");
        } catch (PlayerException ex) {
            assertEquals("Bad exception message",
                         "Level has not been set for " + ch.getName(),
                         ex.getMessage());
        }

        Map map = new Map(MapBuilder.buildMap(2, 2, 10, 10));

        Level lvl = new Level("level", map);

        lvl.enterDown(ch);
        assertFalse("Player should not have a defined path", ch.hasPath());

        ch.buildPath(goal);
        assertTrue("Player should have a defined path", ch.hasPath());

        ch.clearPath();
        assertFalse("Player should not have a defined path", ch.hasPath());
    }

    public void testMovePathBad()
        throws CoreException
    {
        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        try {
            ch.movePath();
            fail("Move without path should fail");
        } catch (CoreException ce) {
            assertNotNull("Exception message is null", ce.getMessage());
            assertTrue("Unexpected exception " + ce,
                       ce.getMessage().equals("No current path"));
        }
    }

    public void testBuildPath()
        throws CoreException
    {
        final int upX = 2;
        final int upY = 2;
        final int downX = 4;
        final int downY = 4;

        Map map = new Map(MapBuilder.buildMap(upX, upY, downX, downY));

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        assertEquals("Bad initial X", -1, ch.getX());
        assertEquals("Bad initial Y", -1, ch.getY());

        Level lvl = new Level("level", map);

        lvl.enterDown(ch);
        assertFalse("Player should not have a defined path", ch.hasPath());

        runPath(ch, 1, 1, 2, 3, 2);
        runPath(ch, 2, 3, 1, 1, 2);
        runPath(ch, 1, 1, 3, 2, 2);
        runPath(ch, 3, 2, 1, 1, 2);
        runPath(ch, 1, 2, 2, 1, 1);
        runPath(ch, 2, 1, 1, 2, 1);
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
