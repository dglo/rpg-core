package org.glowacki.core.fov.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.glowacki.core.fov.IVisibilityMap;
import org.glowacki.core.fov.Point;

public class MockMap
    implements IVisibilityMap
{
    public boolean def; // true => obstacle

    public Set<Point> exception = new HashSet<Point>();

    public Set<Point> visited = new HashSet<Point>();

    public Set<Point> chkb4visit = new HashSet<Point>();

    public Set<Point> visiterr = new HashSet<Point>();

    public Set<Point> prjPath = new HashSet<Point>();

    public MockMap(boolean defaultObscured)
    {
        this.def = defaultObscured;
    }

    public boolean contains(int x, int y)
    {
        return true;
    }

    public boolean isObstructed(int x, int y)
    {
        Point p = new Point(x, y);
        if (!visited.contains(p))
            chkb4visit.add(p);
        return def ^ exception.contains(p);
    }

    public void setVisible(int x, int y)
    {
        Point p = new Point(x, y);
        if (visited.contains(p))
            visiterr.add(p);
        visited.add(p);
    }
}
