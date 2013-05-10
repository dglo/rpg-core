package org.glowacki.core.dungen;

import org.glowacki.core.astar.INode;
import org.glowacki.core.astar.PathException;

/**
 * MapNode exception
 */
class MapNodeException
    extends PathException
{
    MapNodeException(String msg)
    {
        super(msg);
    }
}

/**
 * A map node
 */
public class MapNode
    implements Comparable, INode
{
    /**
     * Point types
     */
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

    /**
     * Create a map node
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    MapNode(int x, int y)
    {
        this(x, y, false);
    }

    /**
     * Create a map node
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param tempNode if <tt>true</tt>, this is a temporary node
     */
    MapNode(int x, int y, boolean tempNode)
    {
        this(x, y, EndPoint.NONE, tempNode);
    }

    /**
     * Create a map node
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param endPt marker for starting/end points
     */
    MapNode(int x, int y, EndPoint endPt)
    {
        this(x, y, endPt, false);
    }

    /**
     * Create a map node
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param endPt marker for starting/end points
     * @param tempNode if <tt>true</tt>, this is a temporary node
     */
    MapNode(int x, int y, EndPoint endPt, boolean tempNode)
    {
        this.x = x;
        this.y = y;
        this.endPt = endPt;
        this.tempNode = tempNode;

        type = RoomType.EMPTY;
    }

    /**
     * Clear this node
     */
    public void clear()
    {
        endPt = EndPoint.NONE;

        parent = null;
        parentCost = Double.MIN_VALUE;

        lastNode = null;
        passThroughCost = Double.MIN_VALUE;
    }

    /**
     * Compare this node to the object
     *
     * @param obj object being compared
     *
     * @return the usual values
     */
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

    /**
     * Does this node match the object?
     *
     * @param obj object being compared
     *
     * @return <tt>true</tt> if the objects are equal
     */
    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    /**
     * Get a character representation of this node
     *
     * @return character
     */
    public char getChar()
    {
        return type.getChar();
    }

    /**
     * Get the parent node
     *
     * @return parent
     */
    public INode getParent()
    {
        return parent;
    }

    /**
     * Get the parent cost for this node
     *
     * @return parent cost
     */
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

    /**
     * Get the pass-through cost from this node to the goal
     *
     * @param goal target node
     *
     * @return pass-through cost
     *
     * @throws MapNodeException if there is a problem
     */
    public double getPassThrough(INode goal)
        throws MapNodeException
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
                throw new MapNodeException("Not handling " + type);
            }

            passThroughCost = localCost + extraCost + getParentCost();
            lastNode = goal;
        }

        return passThroughCost;
    }

    /**
     * Get this nodes room type
     *
     * @return type
     */
    public RoomType getType()
    {
        return type;
    }

    /**
     * Get this node's X coordinate
     *
     * @return X coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get this node's Y coordinate
     *
     * @return Y coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get this node's hash code
     *
     * @return hash code
     */
    public int hashCode()
    {
        return (x & 0xffff) << 16 | (y & 0xffff);
    }

    private static boolean isAncestor(INode node, INode other)
        throws MapNodeException
    {
        INode prev = node;

        int loopCnt = 0;
        while (prev != null) {
            if (prev.getX() == other.getX() && prev.getY() == other.getY()) {
                return true;
            }

            prev = prev.getParent();
            if (loopCnt++ > 500) {
                final String msg = "Aborting loop while setting " + node +
                    " parent to " + other + " (currently " +
                    node.getParent() + ")";
                throw new MapNodeException(msg);
            }
        }

        return false;
    }

    /**
     * Is this node a door?
     *
     * @return <tt>true</tt> if this is a door
     */
    public boolean isDoor()
    {
        return type == RoomType.DOOR;
    }

    /**
     * Is this node empty?
     *
     * @return <tt>true</tt> if this is empty
     */
    public boolean isEmpty()
    {
        return type == RoomType.EMPTY;
    }

    /**
     * Is this an ending node?
     *
     * @return <tt>true</tt> if this is an ending node
     */
    public boolean isEnd()
    {
        return endPt == EndPoint.END;
    }

    /**
     * Can this node be moved to?
     *
     * @return <tt>true</tt> if this node can be occupied
     */
    public boolean isMovable()
    {
        return type == RoomType.FLOOR || type == RoomType.DOOR ||
            type == RoomType.DOWNSTAIRS || type == RoomType.TUNNEL ||
            type == RoomType.UPSTAIRS;
    }

    /**
     * Is this a starting node?
     *
     * @return <tt>true</tt> if this is a starting node
     */
    public boolean isStart()
    {
        return endPt == EndPoint.START;
    }

    /**
     * Is this node a wall?
     *
     * @return <tt>true</tt> if this is a wall
     */
    public boolean isWall()
    {
        // XXX should also check RoomType.SIDEWALL
        return type == RoomType.WALL;
    }

    void setEndPoint(EndPoint pt)
    {
        endPt = pt;
    }

    /**
     * Set the parent node
     *
     * @param node parent
     *
     * @throws MapNodeException if this would cause a loop
     */
    public void setParent(INode node)
        throws MapNodeException
    {
        if (!tempNode && parent != null &&
            (parent.getX() != node.getX() || parent.getY() != node.getY()) &&
            (isAncestor(this, node) || isAncestor(node, this)))
        {
            throw new MapNodeException("Setting " + toString() +
                                         " parent to " + node +
                                         " would create a loop");
        }

        parent = node;
        parentCost = Double.MIN_VALUE;

        lastNode = null;
        passThroughCost = Double.MIN_VALUE;
    }

    /**
     * Set the room type
     *
     * @param type room type
     */
    public void setType(RoomType type)
    {
        this.type = type;
    }

    /**
     * Get a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("%d,%d:%s%s", x, y, type,
                             (endPt == EndPoint.START ? "start" :
                              (endPt == EndPoint.END ? "end" : "")));
    }
}
