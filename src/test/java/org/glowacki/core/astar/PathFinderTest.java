package org.glowacki.core.astar;

import org.glowacki.core.UnimplementedError;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

class MyNode
    implements INode
{
    private String name;
    private int x;
    private int y;

    private INode parent;
    private boolean start;
    private boolean end;

    MyNode(String name, int x, int y)
    {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public INode getParent()
    {
        return parent;
    }

    public double getParentCost()
    {
        throw new UnimplementedError();
    }

    public double getPassThrough(INode node)
    {
        if (node.getX() != x) {
            return 1.5;
        }

        return Math.abs(node.getY() - y);
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean isEnd()
    {
        return end;
    }

    public boolean isStart()
    {
        return start;
    }

    public void setEnd()
    {
        end = true;
    }

    public void setParent(INode node)
    {
        parent = node;
    }

    public void setStart()
    {
        start = true;
    }

    public String toString()
    {
        return String.format("%s[%d,%d]", name, x, y);
    }
}

class MyPathFinder
    extends PathFinder
{
    private INode start;
    private INode mid;
    private INode extra;
    private INode goal;

    MyPathFinder(INode start, INode mid, INode extra, INode goal)
    {
        this.start = start;
        this.mid = mid;
        this.extra = extra;
        this.goal = goal;
    }

    public INode createTempNode(INode node)
    {
        return new MyNode("tmp", node.getX(), node.getY());
    }

    public Set<INode> getAdjacencies(INode node)
    {
        HashSet<INode> set = new HashSet<INode>();

        if (node == start) {
            set.add(mid);
            set.add(extra);
        } else if (node == mid) {
            set.add(start);
            set.add(extra);
            set.add(goal);
        }

        return set;
    }
}

public class PathFinderTest
    extends TestCase
{
    public PathFinderTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(PathFinderTest.class);
    }

    public void testSimple()
        throws PathException
    {
        MyNode start = new MyNode("start", 0, 0);
        start.setStart();

        MyNode mid = new MyNode("mid", 0, 1);
        MyNode extra = new MyNode("extra", 1, 0);

        MyNode goal = new MyNode("goal", 0, 2);
        goal.setEnd();

        PathFinder pf = new MyPathFinder(start, mid, extra, goal);

        pf.findBestPath(start, goal);
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
