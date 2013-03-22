package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class MapCharRepresentationTest
    extends TestCase
{
    public MapCharRepresentationTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(MapCharRepresentationTest.class);
    }

    public void testInOut()
        throws CoreException
    {
        for (Terrain t : Terrain.values()) {
            char ch = MapCharRepresentation.getCharacter(t);
            Terrain t2 = MapCharRepresentation.getTerrain(ch);

            assertEquals("Converting Terrain " + t + "->'" + ch + "'", t, t2);
        }
    }

    public void testEdges()
        throws CoreException
    {
        boolean running = true;
        for (int i = 0; running; i++) {
            char ch;
            Terrain t;
            switch (i) {
            case 0:
                ch = '|';
                t = Terrain.WALL;
                break;
            case 1:
                ch = '?';
                t = Terrain.UNKNOWN;
                break;
            case 2:
                ch = 'X';
                t = Terrain.UNKNOWN;
                break;
            default:
                running = false;
                ch = 'x';
                t = Terrain.UNKNOWN;
                break;
            }

            if (running) {
                Terrain t2 = MapCharRepresentation.getTerrain(ch);
                assertEquals("Converting '" + ch + "' to Terrain", t, t2);
            }
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
