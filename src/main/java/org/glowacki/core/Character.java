package org.glowacki.core;

/**
 * Core character description.
 */
public class Character
{
    /** Used to compute movement cost */
    public static final double SQRT_2 = 1.41421356;

    private String name;
    private int str;
    private int dex;
    private int spd;

    private double timeLeft;

    /**
     * Create a character.
     *
     * @param name name
     * @param str strength
     * @param dex dexterity
     * @param spd speed
     */
    public Character(String name, int str, int dex, int spd)
    {
        this.name = name;
        this.str = str;
        this.dex = dex;
        this.spd = spd;
    }

    /**
     * Unimplemented.
     *
     * @param ch unused
     */
    public void attack(Character ch)
    {
        throw new UnimplementedError();
    }

    /**
     * Get name
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Move to the specified terrain.
     *
     * @param terrain terrain being moved to
     * @param diagonal <tt>true</tt> if this is a diagonal move
     *
     * @return number of turns required to move
     */
    public int move(Terrain terrain, boolean diagonal)
    {
        if (!terrain.isMovable()) {
            return Integer.MAX_VALUE;
        }

        double cost = 10.0 * terrain.getCost();
        if (diagonal) {
            cost *= SQRT_2;
        }

        int turns = 0;
        while (cost > timeLeft) {
            timeLeft += (double) spd;
            turns++;
        }

        timeLeft -= cost;
        return turns;
    }

    /**
     * Return debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("%s[%d/%d/%d tm=%4.2f]", name, str, dex, spd,
                             timeLeft);
    }
}
