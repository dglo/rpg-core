package org.glowacki.core.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class MapBuilderTest
    extends TestCase
{
    public MapBuilderTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MapBuilderTest.class);
    }

    public void testBuildMap()
        throws MapBuilderException
    {
        String[] map = new String[] {
            "------",
            "|....|",
            "|.<>.|",
            "|....|",
            "------",
        };

        final int row = 2;
        final int upX = 2;
        final int downX = 3;

        String[] built = MapBuilder.buildMap(upX, row, downX, row);
        assertNotNull("buildMap() returned null", built);
        assertEquals("Bad map size", map.length, built.length);

        for (int i = 0; i < map.length; i++) {
            assertEquals("Bad map line #" + i, map[i], built[i]);
        }

        String[] map1 = new String[] {
            "-----",
            "|...|",
            "|.>.|",
            "|...|",
            "-----",
        };

        String[] built1 = MapBuilder.buildMap(-1, -1, 2, 2);
        assertNotNull("buildMap() returned null", built1);
        assertEquals("Bad map size", map1.length, built1.length);

        for (int i = 0; i < map1.length; i++) {
            assertEquals("Bad map line #" + i, map1[i], built1[i]);
        }

        String[] map2 = new String[] {
            "-----",
            "|...|",
            "|.<.|",
            "|...|",
            "-----",
        };

        String[] built2 = MapBuilder.buildMap(2, 2, -1, -1);
        assertNotNull("buildMap() returned null", built2);
        assertEquals("Bad map size", map2.length, built2.length);

        for (int i = 0; i < map2.length; i++) {
            assertEquals("Bad map line #" + i, map2[i], built2[i]);
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
