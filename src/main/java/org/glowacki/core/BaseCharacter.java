package org.glowacki.core;

public abstract class BaseCharacter
    implements ICharacter
{
    /** Used to compute movement cost */
    public static final double SQRT_2 = 1.41421356;

    private int str;
    private int dex;
    private int spd;

    private int x;
    private int y;

    private double timeLeft;

    public BaseCharacter(int str, int dex, int spd)
    {
        this.str = str;
        this.dex = dex;
        this.spd = spd;

        x = -1;
        y = -1;
    }

    /**
     * Compute the cost of moving to the specified terrain.
     *
     * @param terrain terrain being moved to
     * @param diagonal <tt>true</tt> if this is a diagonal move
     *
     * @return movement cost
     */
    private double computeMoveCost(Terrain terrain, boolean diagonal)
    {
        if (!terrain.isMovable()) {
            return Integer.MAX_VALUE;
        }

        double cost = 10.0 * terrain.getCost();
        if (diagonal) {
            cost *= SQRT_2;
        }

        return cost;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    int move(IMap map, Direction dir)
        throws CoreException
    {
        int newX = x;
        int newY = y;

        Terrain t;

        boolean moved = true;
        if (dir == Direction.LEFT_UP || dir == Direction.LEFT ||
            dir == Direction.LEFT_DOWN)
        {
            newX -= 1;
            moved &= (newX >= 0);
        } else if (dir == Direction.RIGHT_UP || dir == Direction.RIGHT ||
                   dir == Direction.RIGHT_DOWN)
        {
            newX += 1;
            moved &= (newX <= map.getMaxX());
        }

        if (dir == Direction.LEFT_UP || dir == Direction.UP ||
            dir == Direction.RIGHT_UP)
        {
            newY -= 1;
            moved &= (newY >= 0);
        } else if (dir == Direction.LEFT_DOWN || dir == Direction.DOWN ||
                   dir == Direction.RIGHT_DOWN)
        {
            newY += 1;
            moved &= (newY <= map.getMaxY());
        }

        if (!moved) {
            return -1;
        }

        try {
            map.moveTo(this, newX, newY);
        } catch (CoreException ce) {
            return -1;
        }

        t = map.getTerrain(newX, newY);

        x = newX;
        y = newY;

        boolean diagonal =
            (dir == Direction.LEFT_UP || dir == Direction.LEFT_DOWN ||
             dir == Direction.RIGHT_UP || dir == Direction.RIGHT_DOWN);

        return moveInternal(t, diagonal);
    }

    int moveInternal(Terrain t, boolean diagonal)
    {
        final double cost = computeMoveCost(t, diagonal);

        int turns = 0;
        while (cost > timeLeft) {
            timeLeft += (double) spd;
            turns++;
        }

        timeLeft -= cost;
        return turns;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public String toString()
    {
        return String.format("(%d/%d/%d)@[%d,%d]", str, dex, spd, x, y);
    }
}
