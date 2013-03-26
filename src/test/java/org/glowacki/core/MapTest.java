package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.test.MapBuilder;
import org.glowacki.core.test.MockCharacter;

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

    public void testBadInsertOccupiedRemove()
        throws CoreException
    {
        final String[] map = new String[] {
            "----",
            "|..|",
            "----",
        };

        Map tmap = new Map(map);

        MockCharacter badguy = new MockCharacter("badguy");

        try {
            tmap.insertCharacter(badguy, 0, 0);
            fail("Should not be able to insert character at [0, 0]");
        } catch (CoreException ce) {
            // expect this to fail
        }

        for (int i = 0; i < 4; i++) {
            int x = 1;
            int y = 1;

            if (i == 0) {
                x = -1;
            } else if (i == 1) {
                x = tmap.getMaxX() + 1;
            } else if (i == 2) {
                y = -1;
            } else if (i == 3) {
                y = tmap.getMaxY() + 1;
            }

            try {
                tmap.insertCharacter(badguy, x, y);
                fail(String.format("Should not be able to insert character" +
                                   " at [%d,%d]", x, y));
            } catch (CoreException ce) {
                // expect this to fail
            }

            try {
                tmap.isOccupied(x, y);
                fail(String.format("Should not be able to insert character" +
                                   " at [%d,%d]", x, y));
            } catch (CoreException ce) {
                // expect this to fail
            }

            badguy.setPosition(x, y);

            try {
                tmap.removeCharacter(badguy);
                fail(String.format("Should not be able to remove character" +
                                   " from [%d,%d]", x, y));
            } catch (CoreException ce) {
                // expect this to fail
            }

        }
    }

    public void testInsertRemove()
        throws CoreException
    {
        final String[] map = new String[] {
            "----",
            "|..|",
            "----",
        };

        Map tmap = new Map(map);

        MockCharacter jumpy = new MockCharacter("jumpy");

        try {
            tmap.removeCharacter(jumpy);
            fail("Should not be able to remove character from initial map");
        } catch (CoreException ce) {
            // expect this to fail
        }

        int x = 2;
        int y = 1;

        tmap.insertCharacter(jumpy, x, y);
        jumpy.setPosition(x, y);

        MockCharacter outie = new MockCharacter("outie");

        try {
            tmap.insertCharacter(outie, x, y);
            fail("Should not be able to insert character into occupied space");
        } catch (CoreException ce) {
            // expect this to fail
        }

        tmap.removeCharacter(jumpy);
    }

    public void testIsOccupied()
        throws CoreException
    {
        final String[] map = new String[] {
            "----",
            "|<>|",
            "----",
        };

        Map tmap = new Map(map);

        for (int y = 0; y < tmap.getMaxY(); y++) {
            for (int x = 0; x < tmap.getMaxX(); x++) {
                assertFalse(String.format("[%d,%d] should not be occupied",
                                          x, y), tmap.isOccupied(x, y));
            }
        }

        // character should enter at [1,1]
        MockCharacter downCh = new MockCharacter("downer");
        final int downY = 1;
        final int downX = 1;
        tmap.enterDown(downCh);
        assertEquals("Unexpected Y coordinate " + downCh.getY(),
                     downY, downCh.getY());
        assertEquals("Unexpected X coordinate " + downCh.getX(),
                     downX, downCh.getX());
        for (int y = 0; y < tmap.getMaxY(); y++) {
            for (int x = 0; x < tmap.getMaxX(); x++) {
                if (y == downCh.getY() && x == downCh.getX()) {
                    assertTrue(String.format("[%d,%d] should be occupied",
                                             x, y), tmap.isOccupied(x, y));
                } else {
                    assertFalse(String.format("[%d,%d] should not be occupied",
                                          x, y), tmap.isOccupied(x, y));
                }
            }
        }

        // character should enter at [1,2]
        final int upY = 1;
        final int upX = 2;
        MockCharacter upCh = new MockCharacter("upper");
        tmap.enterUp(upCh);
        assertEquals("Unexpected Y coordinate " + upCh.getY(),
                     upY, upCh.getY());
        assertEquals("Unexpected X coordinate " + upCh.getX(),
                     upX, upCh.getX());
        for (int y = 0; y < tmap.getMaxY(); y++) {
            for (int x = 0; x < tmap.getMaxX(); x++) {
                if ((y == downCh.getY() && x == downCh.getX()) ||
                    (y == upCh.getY() && x == upCh.getX()))
                {
                    assertTrue(String.format("[%d,%d] should be occupied",
                                             x, y), tmap.isOccupied(x, y));
                } else {
                    assertFalse(String.format("[%d,%d] should not be occupied",
                                          x, y), tmap.isOccupied(x, y));
                }
            }
        }
    }

    public void testMoveTo()
        throws CoreException
    {
        final String[] map = new String[] {
            "----",
            "|..|",
            "----",
        };

        Map tmap = new Map(map);

        MockCharacter movie = new MockCharacter("movie");

        final int origX = 1;
        final int origY = 1;

        movie.setPosition(origX, origY);

        tmap.insertCharacter(movie, origX, origY);

        for (int i = 0; i < 4; i++) {
            assertEquals("Bad X coordinate", origX, movie.getX());
            assertEquals("Bad Y coordinate", origY, movie.getY());

            for (int y = 0; y < tmap.getMaxY(); y++) {
                for (int x = 0; x < tmap.getMaxX(); x++) {
                    if (y == movie.getY() && x == movie.getX()) {
                        assertTrue(String.format("[%d,%d] should be occupied",
                                                 x, y), tmap.isOccupied(x, y));
                    } else {
                        final String msg =
                            String.format("[%d,%d] should not be occupied",
                                          x, y);
                        assertFalse(msg, tmap.isOccupied(x, y));
                    }
                }
            }

            int x = origX;
            int y = origY;

            if (i == 0) {
                x = -1;
            } else if (i == 1) {
                x = tmap.getMaxX() + 1;
            } else if (i == 2) {
                y = -1;
            } else if (i == 3) {
                y = tmap.getMaxY() + 1;
            }

            try {
                tmap.moveTo(movie, x, y);
                fail(String.format("Should not be able to move character" +
                                   " to [%d,%d]", x, y));
            } catch (CoreException ce) {
                // expect this to fail
            }
        }

        tmap.moveTo(movie, 2, 1);
    }

    public void testBadEnter()
        throws CoreException
    {
        final String[] map = new String[] {
            "----",
            "|..|",
            "----",
        };

        Map tmap = new Map(map);

        MockCharacter entry = new MockCharacter("entry");

        try {
            tmap.enterDown(entry);
            fail("Should not be able to enter with no up staircase");
        } catch (CoreException ce) {
            // expect this to fail
        }

        try {
            tmap.enterUp(entry);
            fail("Should not be able to enter with no up staircase");
        } catch (CoreException ce) {
            // expect this to fail
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
