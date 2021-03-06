package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;
import org.glowacki.core.test.MockCharacter;
import org.glowacki.core.test.MockLevel;
import org.glowacki.core.test.MockMap;

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
        assertTrue("Player should have a path", ch.hasPath());

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

        MockMap map = new MockMap(1, 1);
        MockLevel lvl = new MockLevel("empty", map);

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
        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        Direction dir = Direction.LEFT;
        do {
            MockLevel lvl = new MockLevel("empty", map);
            lvl.setMap(map);

            ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
            ch.setLevel(lvl);
            ch.setPosition(1, 1);

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
        MockMap map = new MockMap(3, 3);
        map.setUpStaircase(1, 1);
        map.setDownStaircase(2, 2);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);
        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(bottomLvl);
        ch.setPosition(1, 1);

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
        MockMap map = new MockMap(4, 4);
        map.setDownStaircase(2, 2);
        map.setUpStaircase(3, 3);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);

        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(bottomLvl);
        ch.setPosition(1, 1);

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
        MockMap map = new MockMap(4, 4);
        map.setUpStaircase(3, 3);

        MockLevel lvl = new MockLevel("lvl", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(lvl);
        ch.setPosition(3, 3);

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
        MockMap map = new MockMap(4, 4);
        map.setUpStaircase(2, 2);
        map.setDownStaircase(3, 3);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);
        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(bottomLvl);
        ch.setPosition(2, 2);

        final ILevel expLvl = ch.getLevel();
        final int expX = ch.getX();
        final int expY = ch.getY();

        topLvl.setOccupied();

        Direction dir = Direction.CLIMB;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Climb to occupied square should not succeed");
        } catch (CoreException ex) {
            assertNotNull("Null exception message", ex.getMessage());

            final String msg = "Occupied";
            assertEquals("Bad message", msg, ex.getMessage());
        }

        assertEquals("Bad level", expLvl, ch.getLevel());
        assertEquals("Bad X coord", expX, ch.getX());
        assertEquals("Bad Y coord", expY, ch.getY());
    }

    public void testDescend()
        throws CoreException
    {
        MockMap map = new MockMap(4, 4);
        map.setUpStaircase(2, 2);
        map.setDownStaircase(3, 3);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);
        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(topLvl);
        ch.setPosition(3, 3);

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
        MockMap map = new MockMap(4, 4);
        map.setUpStaircase(1, 1);
        map.setDownStaircase(2, 2);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);
        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(bottomLvl);
        ch.setPosition(1, 1);

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
        MockMap map = new MockMap(4, 4);
        map.setDownStaircase(3, 3);

        MockLevel topLvl = new MockLevel("top", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(topLvl);
        ch.setPosition(3, 3);

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
        MockMap map = new MockMap(4, 4);
        map.setUpStaircase(2, 2);
        map.setDownStaircase(3, 3);

        MockLevel topLvl = new MockLevel("top", map);
        MockLevel bottomLvl = new MockLevel("bottom", map);
        topLvl.setNextLevel(bottomLvl);
        bottomLvl.setPreviousLevel(topLvl);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(topLvl);
        ch.setPosition(3, 3);

        final ILevel expLvl = ch.getLevel();
        final int expX = ch.getX();
        final int expY = ch.getY();

        bottomLvl.setOccupied();

        Direction dir = Direction.DESCEND;

        int turns;
        try {
            turns = ch.move(dir);
            fail("Descend to occupied square should not succeed");
        } catch (CoreException ex) {
            assertNotNull("Null exception message", ex.getMessage());

            final String msg = "Occupied";
            assertEquals("Bad message", msg, ex.getMessage());
        }

        assertEquals("Bad level", expLvl, ch.getLevel());
        assertEquals("Bad X coord", expX, ch.getX());
        assertEquals("Bad Y coord", expY, ch.getY());
    }

    public void testSeenArray()
        throws CoreException
    {
        MockMap map = new MockMap(4, 4);

        MockLevel lvl = new MockLevel("level", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);

        assertNull("'Seen' array is not null", ch.getSeenArray());

        ch.setLevel(lvl);
        ch.setPosition(2, 2);

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
        MockMap map = new MockMap(4, 4);

        MockLevel lvl = new MockLevel("level", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(lvl);
        ch.setPosition(2, 2);

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

        MockMap map = new MockMap(4, 4);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("level", map);

        ch.setLevel(lvl);
        ch.setPosition(2, 2);

        assertFalse("Player should not have a defined path", ch.hasPath());

        ch.buildPath(goal);
        assertTrue("Player should have a defined path", ch.hasPath());

        ch.clearPath();
        assertFalse("Player should not have a defined path", ch.hasPath());
    }

    public void testMovePathBad()
        throws CoreException
    {
        MockMap map = new MockMap(4, 4);

        MockLevel lvl = new MockLevel("level", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        ch.setLevel(lvl);

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

        MockMap map = new MockMap(4, 4);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("level", map);

        ICharacter ch = new PlayerCharacter("foo", 1, 2, 10, 10);
        assertEquals("Bad initial X", -1, ch.getX());
        assertEquals("Bad initial Y", -1, ch.getY());

        ch.setLevel(lvl);
        ch.setPosition(1, 1);

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
