package org.glowacki.core.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.glowacki.core.Map;
import org.glowacki.core.MapEntry;
import org.glowacki.core.MapPoint;

class BaseNode
    implements Comparable, INode
{
    private int x;
    private int y;
    private boolean startNode;
    private boolean endNode;

    private INode parent;
    // cost of getting from this node to goal
    private double localCost = Double.MIN_VALUE;
     // cost of getting from parent node to this node
    private double parentCost = Double.MIN_VALUE;
    // cost of getting from the start to the goal through this node
    private double passThroughCost = Double.MIN_VALUE;

    BaseNode(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int compareTo(Object obj)
    {
        INode node = (INode) obj;

        int val = x - node.getX();
        if (val == 0) {
            val = y - node.getY();
        }

        return val;
    }

    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    public double getLocalCost(INode goal)
    {
        if (isStart()) {
            return 0.0;
        }

        if (localCost == Double.MIN_VALUE) {
            localCost = 1.0 * (Math.abs(x - goal.getX()) +
                               Math.abs(y - goal.getY()));
        }

        return localCost;
    }

    public INode getParent()
    {
        return parent;
    }

    public double getParentCost()
    {
        if (isStart()) {
            return 0.0;
        }

        if (parentCost == Double.MIN_VALUE) {
            parentCost = 1.0 + .5 * (parent.getParentCost() - 1.0);
        }

        return parentCost;
    }

    public double getPassThrough(INode goal)
    {
        if (isStart()) {
            return 0.0;
        }

        if (passThroughCost == Double.MIN_VALUE) {
            passThroughCost = getLocalCost(goal) + getParentCost();
        }

        return passThroughCost;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int hashCode()
    {
        return ((x & 0xffff) * 0x1000) + (y & 0xffff);
    }

    public boolean isEnd()
    {
        return endNode;
    }

    public boolean isStart()
    {
        return startNode;
    }

    public void setEnd()
    {
        endNode = true;
    }

    public void setParent(INode parent)
    {
        this.parent = parent;
        parentCost = Double.MIN_VALUE;
        passThroughCost = Double.MIN_VALUE;
    }

    public void setStart()
    {
        startNode = true;
    }

    public String toString()
    {
        final String sStr = (startNode ? "*start" : "");
        final String eStr = (endNode ? "*end" : "");

        return String.format("[%d,%d]", x, y);
    }
}

class MapNode
    extends BaseNode
{
    private MapEntry entry;

    MapNode(MapEntry entry)
    {
        super(entry.getX(), entry.getY());

        this.entry = entry;
    }

    MapEntry getEntry()
    {
        return entry;
    }
}

public class MapPathFinder
    extends PathFinder
{
    private Map map;
    private MapNode[][] nodes;

    public MapPathFinder(Map map)
    {
        this.map = map;

        nodes = new MapNode[map.getMaxX() + 1][map.getMaxY() + 1];

        for (MapEntry entry : map.getEntries()) {
            nodes[entry.getX()][entry.getY()] = new MapNode(entry);
        }
    }

    public INode createTempNode(int x, int y)
    {
        return new BaseNode(x, y);
    }

    List<MapPoint> findBestPath(MapPoint startPt, MapPoint endPt)
    {
        MapNode start = nodes[startPt.getX()][startPt.getY()];
        start.setStart();

        MapNode end = nodes[endPt.getX()][endPt.getY()];
        end.setEnd();

        List<INode> list = findBestPath(start, end);
        if (list == null) {
            return null;
        }

        List<MapPoint> bestList = new ArrayList<MapPoint>();
        for (INode node : list) {
            if (!(node instanceof MapNode)) {
                throw new Error("Found non-MapEntry node " + node + "<" +
                                node.getClass().getName() + ">");
            }
            bestList.add(((MapNode) node).getEntry());
        }

        return bestList;
    }

    public Set<INode> getAdjacencies(INode node)
    {
        if (!(node instanceof MapNode)) {
            throw new Error("Unexpected node " + node.getClass().getName());
        }

        Set<INode> sorted = new TreeSet<INode>();
        for (int x = node.getX() - 1; x <= node.getX() + 1; x++) {
            for (int y = node.getY() - 1; y <= node.getY() + 1; y++) {
                if (x < 0 || x >= nodes.length ||
                    y < 0 || y >= nodes[x].length)
                {
                    continue;
                }

                if (x == node.getX() && y == node.getY()) {
                    continue;
                }

                MapEntry entry = nodes[x][y].getEntry();
                if (!entry.getTerrain().isMovable()) {
                    continue;
                }

                sorted.add(nodes[x][y]);
            }
        }

        return sorted;
    }

    private MapNode getNode(MapEntry entry)
    {
        return nodes[entry.getX()][entry.getY()];
    }
}
