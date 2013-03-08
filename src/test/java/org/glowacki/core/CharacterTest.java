package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class CharacterTest
    extends TestCase
{
    public CharacterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(CharacterTest.class);
    }

    public void testCreate()
    {
        final String name = "foo";
        final int str = 9;
        final int dex = 10;
        final int qik = 11;

        Character ch = new Character(name, str, dex, qik);
        assertEquals("Bad name", ch.getName(), name);

        String expStr = String.format("%s[%d/%d/%d", name, str, dex, qik);
        assertTrue("Bad character string", ch.toString().startsWith(expStr));
    }

    public void testMove()
    {
        int speed = 10;

        Terrain[] allTerrain = new Terrain[] {
            Terrain.DOOR, Terrain.DOWNSTAIRS, Terrain.FLOOR, Terrain.TUNNEL,
            Terrain.UPSTAIRS, Terrain.WALL, Terrain.WATER, Terrain.UNKNOWN,
        };

        for (int i = 0; i < allTerrain.length; i++) {
            for (int b = 0; b < 2; b++) {
                Character ch = new Character("foo", 1, 2, speed);

                boolean diagonal = b == 1;

                double mult = (diagonal ? Character.SQRT_2 : 1.0);

                int turns = ch.move(allTerrain[i], diagonal);

                int expTurns;

                if (allTerrain[i].getCost() == Terrain.IMPASSABLE) {
                    expTurns = Integer.MAX_VALUE;
                } else {
                    double myCost = allTerrain[i].getCost() * 10.0 * mult;

                    expTurns = 0;

                    int myTime = 0;
                    while (myCost > myTime) {
                        myTime += (double) speed;
                        expTurns++;
                    }
                }

                assertEquals(String.format("Bad number of turns for %s%s",
                                           allTerrain[i],
                                           (diagonal ? " diagonal" : "")),
                             turns, expTurns);
            }
        }
    }

    private void foo(Character ch1)
    {
        Terrain[] allTerrain = new Terrain[] {
            Terrain.FLOOR, Terrain.WATER, Terrain.DOOR
        };
        for (Terrain t : allTerrain) {
            for (int i = 0; i < 2; i++) {
                boolean diagonal = i == 1;

                System.out.print(ch1 + " -> " + t);
                if (diagonal) System.out.print("(diagonal)");
                System.out.print(" = " + ch1.move(t, diagonal));
                System.out.println(" # " + ch1);
            }
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
