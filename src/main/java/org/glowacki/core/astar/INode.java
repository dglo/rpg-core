package org.glowacki.core.astar;

/**
 * Generic path node interface.
 */
public interface INode
{
    INode getParent();
    double getParentCost();
    double getPassThrough(INode goal)
        throws PathException;
    int getX();
    int getY();
    boolean isEnd();
    boolean isStart();
    void setParent(INode p)
        throws PathException;
}
