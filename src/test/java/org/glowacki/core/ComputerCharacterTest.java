package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MockLevel;
import org.glowacki.core.test.MockMap;
import org.glowacki.core.test.MockRandom;

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
        MockRandom random = new MockRandom();

        final int str = 1;
        final int dex = 2;
        final int pcp = 3;
        final int spd = 4;

        random.addDouble(0.111);

        ComputerCharacter ch =
            new ComputerCharacter(random, str, dex, spd, pcp);

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
        MockRandom random = new MockRandom();

        MockMap map = new MockMap(4, 4);
        map.setTerrain(Terrain.FLOOR);

        Direction dir = Direction.LEFT;
        do {
            random.addDouble(0.111);

            ComputerCharacter ch = new ComputerCharacter(random, 1, 2, 10, 10);

            MockLevel lvl = new MockLevel("empty", map);
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
