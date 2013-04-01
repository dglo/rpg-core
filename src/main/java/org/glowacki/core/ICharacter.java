package org.glowacki.core;

public interface ICharacter
{
    void buildPath(MapPoint goal)
        throws CoreException;

    Level getLevel();

    String getName();

    int getX();

    int getY();

    boolean hasPath();

    boolean isPlayer();

    int move(Direction dir)
        throws CoreException;

    int movePath()
        throws CoreException;

    void setLevel(Level l)
        throws CoreException;

    void setPosition(int x, int y);

    void takeTurn();
}
