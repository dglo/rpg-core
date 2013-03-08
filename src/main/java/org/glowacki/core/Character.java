package org.glowacki.core;

public class Character
{
    private static final double SQRT_2 = 1.41421356;

    private String name;
    private int str;
    private int dex;
    private int qik;

    private double timeLeft;

    public Character(String name, int str, int dex, int qik)
    {
        this.name = name;
        this.str = str;
        this.dex = dex;
        this.qik = qik;
    }

    public void attack(Character ch)
    {
        throw new UnimplementedError();
    }

    public String getName()
    {
        return name;
    }

    public int move(Terrain terrain, boolean diagonal)
    {
        double cost = 10.0 * terrain.getCost();
        if (diagonal) {
            cost *= SQRT_2;
        }

        int turns = 0;
        while (cost > timeLeft) {
            timeLeft += (double) qik;
            turns += 1;
        }

        timeLeft -= cost;
        return turns;
    }

    public String toString()
    {
        return String.format("%s[%d/%d/%d mv=%4.2f]", name, str, dex, qik,
                             timeLeft);
    }
}