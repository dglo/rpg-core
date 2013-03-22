package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class MapPointTest
    extends TestCase
{
    public MapPointTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MapPointTest.class);
    }

    public void testCreate()
        throws CoreException
    {
        for (int x = -11; x < 20; x += 7) {
            for (int y = -12; y < 20; y += 6) {
                MapPoint pt = new MapPoint(x, y);

                assertEquals("Bad X value", x, pt.x);
                assertEquals("Bad Y value", y, pt.y);
                assertEquals("Bad string",
                             String.format("(%d,%d)", x, y), pt.toString());
            }
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
