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

    int move(Level level, Direction dir)
        throws CoreException
    {
        int newX = x;
        int newY = y;

        Terrain t;

        switch (dir) {
        case LEFT:
            newX -= 1;
            if (newX < 0) {
                return -1;
            }
            break;
        case RIGHT:
            newX += 1;
            if (newX > level.getMaxX()) {
                return -1;
            }
            break;
        case UP:
            newY -= 1;
            if (newY < 0) {
                return -1;
            }
            break;
        case DOWN:
            newY += 1;
            if (newY > level.getMaxY()) {
                return -1;
            }
            break;
        case CLIMB:
            // non-players cannot roam the dungeon freely
            return -1;
        case DESCEND:
            // non-players cannot roam the dungeon freely
            return -1;
        }

        t = level.get(newX, newY);

        if (!t.isMovable()) {
            return -1;
        }

        x = newX;
        y = newY;

        return moveInternal(t, false);
    }

    int moveInternal(Terrain t, boolean diagonal)
    {
        final double cost = computeMoveCost(t, false);

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
        return String.format("[%d/%d/%d]", str, dex, spd);
    }
}
