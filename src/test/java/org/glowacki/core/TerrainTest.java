package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TerrainTest
    extends TestCase
{
    public TerrainTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TerrainTest.class);
    }

    public void testCost()
        throws CoreException
    {
        for (Terrain t : Terrain.values()) {
            assertEquals("Terrain " + t + " has unexpected isMovable() value",
                         t.getCost() != TerrainConst.IMPASSABLE, t.isMovable());
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
