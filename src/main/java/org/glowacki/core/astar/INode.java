package org.glowacki.core.astar;

/**
 * Generic path node interface.
 */
public interface INode
{
    /**
     * Get the parent node
     *
     * @return parent
     */
    INode getParent();

    /**
     * Get the parent cost for this node
     *
     * @return parent cost
     */
    double getParentCost();

    /**
     * Get the pass-through cost from this node to the goal
     *
     * @param goal target node
     *
     * @return pass-through cost
     *
     * @throws PathException if there is a problem
     */
    double getPassThrough(INode goal)
        throws PathException;

    /**
     * Get this node's X coordinate
     *
     * @return X coordinate
     */
    int getX();

    /**
     * Get this node's Y coordinate
     *
     * @return Y coordinate
     */
    int getY();

    /**
     * Is this the ending node for the path?
     *
     * @return <tt>true</tt> if this is the ending node
     */
    boolean isEnd();

    /**
     * Is this the starting node for the path?
     *
     * @return <tt>true</tt> if this is the starting node
     */
    boolean isStart();

    /**
     * Set the parent node
     *
     * @param p parent
     *
     * @throws PathException if there is a problem
     */
    void setParent(INode p)
        throws PathException;
}
