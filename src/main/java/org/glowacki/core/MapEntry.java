package org.glowacki.core;

public class MapEntry
    implements MapPoint
{
    private int x;
    private int y;
    private Terrain terrain;
    private ICharacter character;

    MapEntry(int x, int y, Terrain t)
    {
        this.x = x;
        this.y = y;
        this.terrain = t;
    }

    public void clearCharacter()
    {
        character = null;
    }

    public ICharacter getCharacter()
    {
        return character;
    }

    public Terrain getTerrain()
    {
        return terrain;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setCharacter(ICharacter ch)
        throws OccupiedException
    {
        if (character != null) {
            throw new OccupiedException();
        }

        character = ch;
    }

    public String toString()
    {
        return String.format("[%d,%d]", x, y);
    }
}
