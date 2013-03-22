package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;

public class MapTest
    extends TestCase
{
    public MapTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MapTest.class);
    }

    public void testCreate()
        throws CoreException
    {
        final String[] map = new String[] {
            "---",
            "|.|",
            "---",
        };

        Map tmap = new Map(map);
        assertEquals("Bad max X", map[0].length() - 1, tmap.getMaxX());
        assertEquals("Bad max Y", map.length - 1, tmap.getMaxY());
        assertNotNull("Null string", tmap.toString());
    }

    public void testBadCreate()
        throws CoreException
    {
        try {
            new Map(null);
            fail("Should not be able to create level from null map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map template cannot be null", ce.getMessage());
        }

        try {
            new Map(new String[0]);
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map template cannot be empty", ce.getMessage());
        }

        try {
            new Map(new String[] { null, });
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map template cannot be empty", ce.getMessage());
        }

        try {
            new Map(new String[] { "", });
            fail("Should not be able to create level from empty map");
        } catch (CoreException ce) {
            assertNotNull("Null exception message", ce.getMessage());
            assertEquals("Bad exception message",
                         "Map template cannot be empty", ce.getMessage());
        }
    }

    public void testBuild()
        throws CoreException
    {
        String[] map = new String[] {
            "------",
            "|....|                  ----------",
            "|....+#######           |.....>..|",
            "|.<..|      ############+......~.|",
            "|....|                  |.X....~~|",
            "------                  ----------",
            null,
        };

        Map tmap = new Map(map);

        String[] pic = tmap.getPicture().split("\n");
        assertEquals("Bad number of lines", pic.length, map.length);

        for (int i = 0; i < pic.length; i++) {
            String mtrim;
            if (map[i] == null) {
                mtrim = "";
            } else {
                mtrim = map[i].replaceAll("\\s+$", "").replaceAll("X", " ");
            }

            String ptrim = pic[i].replaceAll("\\s+$", "");

            assertEquals("Bad line #" + i, ptrim, mtrim);
        }
    }

    public void testBuildMap()
        throws CoreException
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

    public void testGet()
        throws CoreException
    {
        StringBuilder buf = new StringBuilder();
        for (Terrain t : Terrain.values()) {
            buf.append(MapCharRepresentation.getCharacter(t));
        }

        String[] map = new String[] { buf.toString(), };

        Map tmap = new Map(map);

        int n = 0;
        for (Terrain t : Terrain.values()) {
            Terrain actual = tmap.getTerrain(n, 0);
            assertEquals("Bad terrain for " + map[0].charAt(n), t, actual);
            n++;
        }

        for (int i = 0; i < 4; i++) {
            int x, y;
            String expMsg;
            switch (i) {
            case 0:
                x = 0;
                y = -1;
                expMsg = String.format("Bad Y coordinate in (%d,%d)," +
                                       " max is %d", x, y, tmap.getMaxY());
                break;
            case 1:
                x = 0;
                y = 99;
                expMsg = String.format("Bad Y coordinate in (%d,%d)," +
                                       " max is %d", x, y, tmap.getMaxY());
                break;
            case 2:
                x = -1;
                y = 0;
                expMsg = String.format("Bad X coordinate in (%d,%d)," +
                                       " max is %d", x, y, tmap.getMaxX());
                break;
            case 3:
                x = 99;
                y = 0;
                expMsg = String.format("Bad X coordinate in (%d,%d)," +
                                       " max is %d", x, y, tmap.getMaxX());
                break;
            default:
                fail("There is no choice for " + i);
                // not reachable, but compiler doesn't know that
                x = -1;
                y = -1;
                expMsg = null;
                break;
            }

            final String coordStr = "(" + x + "," + y + ")";

            try {
                tmap.getTerrain(x, y);
                fail("Should not have terrain for " + coordStr);
            } catch (CoreException ce) {
                assertNotNull("Null exception message when getting " +
                              coordStr, ce.getMessage());
                assertEquals("Bad exception when getting " + coordStr,
                             expMsg, ce.getMessage());
            }
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}