package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class LevelTest
    extends TestCase
{
    public LevelTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(LevelTest.class);
    }

    public void testCreate()
        throws CoreException
    {
        final String name = "Create";
        final String[] map = new String[] {
            "---",
            "|.|",
            "---",
        };

        Level lvl = new Level(name, map);
        assertEquals("Bad name", name, lvl.getName());
        assertEquals("Bad max X", lvl.getMaxX(), map[0].length() - 1);
        assertEquals("Bad max Y", lvl.getMaxY(), map.length - 1);
        assertNotNull("Null character list", lvl.getCharacters());
        assertEquals("Non-empty character list",
                     lvl.getCharacters().size(), 0);
        assertNotNull("Null string", lvl.toString());
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
        };

        Level lvl = new Level("Sample", map);

        String[] pic = lvl.getPicture().split("\n");
        assertEquals("Bad number of lines", pic.length, map.length);

        for (int i = 0; i < pic.length; i++) {
            String mtrim = map[i].replaceAll("\\s+$", "").replaceAll("X", " ");
            String ptrim = pic[i].replaceAll("\\s+$", "");

            assertEquals("Bad line #" + i, mtrim, ptrim);
        }
    }

    public void testMultiLevel()
        throws CoreException
    {
        String[] map1 = new String[] {
            "-----",
            "|...|",
            "|.>.|",
            "|...|",
            "-----",
        };

        String[] map2 = new String[] {
            "-----",
            "|...|",
            "|.<.|",
            "|...|",
            "-----",
        };

        Level l1 = new Level("1", map1);
        Level l2 = new Level("2", map2);

        assertNull("Next level for level 1 is not null", l1.getNextLevel());
        assertNull("Previous level for level 1 is not null",
                   l1.getPreviousLevel());
        assertNull("Next level for level 2 is not null", l2.getNextLevel());
        assertNull("Previous level for level 2 is not null",
                   l2.getPreviousLevel());

        l1.addNextLevel(l2);

        assertEquals("Bad next level for level 1",
                     l1.getNextLevel(), l2);
        assertNull("Previous level for level 1 is not null",
                   l1.getPreviousLevel());
        assertNull("Next level for level 2 is not null", l2.getNextLevel());
        assertEquals("Bad previous level for level 2",
                   l2.getPreviousLevel(), l1);
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
