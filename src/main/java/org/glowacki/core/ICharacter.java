package org.glowacki.core;

public interface ICharacter
{
    Level getLevel();

    String getName();

    int getX();

    int getY();

    boolean isPlayer();

    int move(Direction dir)
        throws CoreException;

    void setLevel(Level l)
        throws CoreException;

    void setPosition(int x, int y);

    void takeTurn();
}
