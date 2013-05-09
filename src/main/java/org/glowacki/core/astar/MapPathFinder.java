package org.glowacki.core.astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.glowacki.core.IMapPoint;
import org.glowacki.core.Map;
import org.glowacki.core.MapEntry;

/**
 * Node base class
 */
class BaseNode
    implements Comparable, INode
{
    private int x;
    private int y;
    private boolean startNode;
    private boolean endNode;

    private INode parent;

     // cost of getting from parent node to this node
    private double parentCost = Double.MIN_VALUE;

    // last node used to compute passthrough cost
    private INode lastNode;
    // cost of getting from the start to the goal through this node
    private double passThroughCost = Double.MIN_VALUE;

    BaseNode(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void clear()
    {
        startNode = false;
        endNode = false;

        parent = null;
        parentCost = Double.MIN_VALUE;

        lastNode = null;
        passThroughCost = Double.MIN_VALUE;
    }

    public int compareTo(Object obj)
    {
        if (obj == null) {
            return 1;
        }

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

    public INode getParent()
    {
        return parent;
    }

    public double getParentCost()
    {
        if (isStart()) {
            return 0.0;
        }

        if (Double.compare(parentCost, Double.MIN_VALUE) == 0) {
            parentCost = 1.0 + .5 * (parent.getParentCost() - 1.0);
        }

        return parentCost;
    }

    public double getPassThrough(INode goal)
    {
        if (isStart()) {
            return 0.0;
        }

        if (Double.compare(passThroughCost, Double.MIN_VALUE) == 0 ||
            lastNode == null || lastNode.getX() != goal.getX() ||
            lastNode.getY() != goal.getY())
        {
            double localCost = 1.0 * (Math.abs(x - goal.getX()) +
                                      Math.abs(y - goal.getY()));
            passThroughCost = localCost + getParentCost();
            lastNode = goal;
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

        lastNode = null;
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

        return String.format("[%d,%d]%s%s", x, y, sStr, eStr);
    }
}

/**
 * A map node
 */
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

/**
 * Find a path on the specified map.
 */
public class MapPathFinder
    extends PathFinder
{
    private MapNode[][] nodes;

    /**
     * Create a path finder for the specified map
     *
     * @param map map
     */
    public MapPathFinder(Map map)
    {
        nodes = new MapNode[map.getMaxX() + 1][map.getMaxY() + 1];

        for (MapEntry entry : map.getEntries()) {
            nodes[entry.getX()][entry.getY()] = new MapNode(entry);
        }
    }

    /**
     * Create a temporary node
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return new temporary node
     */
    public INode createTempNode(INode node)
    {
        return new BaseNode(node.getX(), node.getY());
    }

    /**
     * Find the best path from <tt>startPt</tt> to <tt>endPt</tt>.
     *
     * @param startPt starting point
     * @param endPt ending point
     *
     * @return list of points in the path
     *
     * @throws PathException if the start or end point is bad
     */
    public List<IMapPoint> findBestPath(IMapPoint startPt, IMapPoint endPt)
        throws PathException
    {
        if (startPt.getX() < 0 || startPt.getX() >= nodes.length ||
            startPt.getY() < 0 || startPt.getY() >= nodes[0].length)
        {
            final String msg =
                String.format("Bad start point [%d,%d]", startPt.getX(),
                              startPt.getY());
            throw new PathException(msg);
        }

        if (endPt.getX() < 0 || endPt.getX() >= nodes.length ||
            endPt.getY() < 0 || endPt.getY() >= nodes[0].length)
        {
            final String msg =
                String.format("Bad end point [%d,%d]", endPt.getX(),
                              endPt.getY());
            throw new PathException(msg);
        }

        MapNode start = nodes[startPt.getX()][startPt.getY()];
        MapNode end = nodes[endPt.getX()][endPt.getY()];

        start.setStart();
        end.setEnd();

        List<INode> list;
        try {
            list = findBestPath(start, end);
        } finally {
            start.clear();
            end.clear();
        }

        if (list == null) {
            return null;
        }

        List<IMapPoint> bestList = new ArrayList<IMapPoint>();
        for (INode node : list) {
            if (!(node instanceof MapNode)) {
                throw new PathException("Found non-MapEntry node " + node +
                                        "<" + node.getClass().getName() +
                                           ">");
            }
            bestList.add(((MapNode) node).getEntry());
        }

        Collections.reverse(bestList);

        return bestList;
    }

    /**
     * Get movable nodes adjacent to the specified node.
     *
     * @param node center node
     *
     * @return set of adjacent nodes
     */
    public Set<INode> getAdjacencies(INode node)
        throws PathException
    {
        if (!(node instanceof MapNode)) {
            throw new PathException("Unexpected node " +
                                    node.getClass().getName());
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
}
