package org.glowacki.core.astar;

import java.util.Set;

interface INode
{
    int getX();
    int getY();
    INode getParent();
    double getParentCost();
    double getPassThrough(INode goal);
    boolean isEnd();
    boolean isStart();
    void setParent(INode p);
}
