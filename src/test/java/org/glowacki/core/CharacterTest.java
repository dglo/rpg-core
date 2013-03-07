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

    public void testBasic()
    {
        Character ch1 = new Character("foo", 10, 10, 10);

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
