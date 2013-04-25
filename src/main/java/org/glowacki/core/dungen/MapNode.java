package org.glowacki.core.dungen;

import org.glowacki.core.astar.INode;

public class MapNode
    implements Comparable, INode
{
    enum EndPoint
    {
        START, END, NONE;
    }

    private int x;
    private int y;
    private EndPoint endPt;
    private boolean tempNode;
    private RoomType type;

    private INode parent;

     // cost of getting from parent node to this node
    private double parentCost = Double.MIN_VALUE;

    // last node used to compute passthrough cost
    private INode lastNode;
    // cost of getting from the start to the goal through this node
    private double passThroughCost = Double.MIN_VALUE;

    MapNode(int x, int y)
    {
        this(x, y, false);
    }

    MapNode(int x, int y, boolean tempNode)
    {
        this(x, y, EndPoint.NONE, tempNode);
    }

    MapNode(int x, int y, EndPoint endPt)
    {
        this(x, y, endPt, false);
    }

    MapNode(int x, int y, EndPoint endPt, boolean tempNode)
    {
        this.x = x;
        this.y = y;
        this.endPt = endPt;
        this.tempNode = tempNode;

        type = RoomType.EMPTY;
    }

    public void clear()
    {
        endPt = EndPoint.NONE;

        parent = null;
        parentCost = Double.MIN_VALUE;

        lastNode = null;
        passThroughCost = Double.MIN_VALUE;
    }

    public int compareTo(Object obj)
    {
        if (obj == null) {
            return -1;
        }

        if (!(obj instanceof MapNode)) {
            return getClass().getName().compareTo(obj.getClass().getName());
        }

        MapNode n = (MapNode) obj;

        int val = x - n.x;
        if (val == 0) {
            val = y - n.y;
        }

        return val;
    }

    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    public char getChar()
    {
if (hackChar != (char) 0) return hackChar;
        return type.getChar();
    }

private char hackChar;
    void hackChar(char ch)
    {
        hackChar = ch;
    }

    public INode getParent()
    {
        return parent;
    }

    public double getParentCost()
    {
        if (isStart()) {
            return 0.0;
        } else if (parent == null) {
            return 999.0;
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

            double extraCost;
            if (isMovable()) {
                extraCost = 0.0;
            } else if (type == RoomType.EMPTY) {
                extraCost = 5.0;
            } else if (isWall()) {
                MapNode p = (MapNode) parent;
                if (p != null && (p.isWall() || p.isDoor())) {
                    extraCost = 20.0;
                } else {
                    extraCost = 5.5;
                }
            } else {
                throw new Error("Not handling " + type);
            }

            passThroughCost = localCost + extraCost + getParentCost();
            lastNode = goal;
        }

        return passThroughCost;
    }

    public RoomType getType()
    {
        return type;
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
        return (x & 0xffff) << 16 | (y & 0xffff);
    }

    private static boolean isAncestor(INode node, INode other)
    {
        INode prev = node;

        int loopCnt = 0;
        while (prev != null) {
            if (prev.getX() == other.getX() && prev.getY() == other.getY()) {
                return true;
            }

            prev = prev.getParent();
            if (loopCnt++ > 500) {
                throw new Error("Aborting loop while setting " + node +
                                " parent to " + other + " (currently " +
                                node.getParent() + ")");
            }
        }

        return false;
    }

    public boolean isDoor()
    {
        return type == RoomType.DOOR;
    }

    public boolean isEmpty()
    {
        return type == RoomType.EMPTY;
    }

    public boolean isEnd()
    {
        return endPt == EndPoint.END;
    }

    public boolean isMovable()
    {
        return type == RoomType.FLOOR || type == RoomType.DOOR ||
            type == RoomType.TUNNEL;
    }

    public boolean isStart()
    {
        return endPt == EndPoint.START;
    }

    public boolean isWall()
    {
        return type == RoomType.WALL;// || type == RoomType.SIDEWALL;
    }

    void setEndPoint(EndPoint pt)
    {
        endPt = pt;
    }

    public void setParent(INode node)
    {
        if (!tempNode && parent != null &&
            (parent.getX() != node.getX() || parent.getY() != node.getY()) &&
            (isAncestor(this, node) || isAncestor(node, this)))
        {
            throw new Error("Setting " + toString() + " parent to " +
                            node + " would create a loop");
        }

        parent = node;
        parentCost = Double.MIN_VALUE;

        lastNode = null;
        passThroughCost = Double.MIN_VALUE;
    }

    public void setType(RoomType type)
    {
        this.type = type;
    }

    public String toString()
    {
        return String.format("%d,%d:%s%s", x, y, type,
                             (endPt == EndPoint.START ? "start" :
                              (endPt == EndPoint.END ? "end" : "")));
    }
}
