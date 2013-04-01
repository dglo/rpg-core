package org.glowacki.core;

/**
 * A map entry.
 */
public class MapEntry
    implements MapPoint
{
    private int x;
    private int y;
    private Terrain terrain;
    private ICharacter character;

    /**
     * Create a map entry.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param t terrain
     */
    MapEntry(int x, int y, Terrain t)
    {
        this.x = x;
        this.y = y;
        this.terrain = t;
    }

    /**
     * Remove the character from this position.
     */
    public void clearCharacter()
    {
        character = null;
    }

    /**
     * Get the character occupying this entry.
     *
     * @return <tt>null</tt> if there is no character at this position
     */
    public ICharacter getCharacter()
    {
        return character;
    }

    /**
     * Get this entry's terrain.
     *
     * @return terrain
     */
    public Terrain getTerrain()
    {
        return terrain;
    }

    /**
     * Get this entry's X coordinate.
     *
     * @return X coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get this entry's Y coordinate.
     *
     * @return Y coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Set the character which occupies this entry.
     *
     * @param ch character
     *
     * @throws OccupiedException if this entry is occupied
     */
    public void setCharacter(ICharacter ch)
        throws OccupiedException
    {
        if (character != null) {
            throw new OccupiedException();
        }

        character = ch;
    }

    /**
     * Return a debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("[%d,%d]", x, y);
    }
}
