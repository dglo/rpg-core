package org.glowacki.core;

public interface IMap
{
    int getMaxX();

    int getMaxY();

    Terrain getTerrain(int x, int y)
        throws MapException;

    void moveTo(ICharacter ch, int x, int y)
        throws MapException;
}
