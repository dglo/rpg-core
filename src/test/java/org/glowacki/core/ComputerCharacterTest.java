package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;

public class ComputerCharacterTest
    extends TestCase
{
    public ComputerCharacterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(ComputerCharacterTest.class);
    }

    public void testCreate()
    {
        final int str = 1;
        final int dex = 2;
        final int spd = 3;

        ComputerCharacter ch = new ComputerCharacter(str, dex, spd, 0L);

        try {
            ch.getLevel();
            fail("This should not succeed");
        } catch (Error err) {
            assertEquals("Unexpected error",
                         "Unimplemented", err.getMessage());
        }

        try {
            ch.getName();
            fail("This should not succeed");
        } catch (Error err) {
            assertEquals("Unexpected error",
                         "Unimplemented", err.getMessage());
        }


        assertFalse("Bad isPlayer() value", ch.isPlayer());

        String expStr = String.format("%s(%d/%d/%d", "", str, dex, spd);
        assertTrue("Bad character string " + ch,
                   ch.toString().startsWith(expStr));
    }

    public void testMove2D()
        throws CoreException
    {
        Map map = new Map(MapBuilder.buildMap(2, 2, -1, -1));

        Direction dir = Direction.LEFT;
        do {
            ComputerCharacter ch = new ComputerCharacter(1, 2, 10, 0L);

            Level lvl = new Level("empty", map);
            ch.setLevel(lvl, 2, 2);

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

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
