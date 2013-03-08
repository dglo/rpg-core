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

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
