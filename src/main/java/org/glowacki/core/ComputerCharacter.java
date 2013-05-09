package org.glowacki.core;

import java.util.Random;

/**
 * Computer character.
 */
public class ComputerCharacter
    extends BaseCharacter
{
    private static final int MAX_ATTEMPTS = 20;

    /** Character states */
    private enum State { ASLEEP, MEANDER, IN_PURSUIT };

    private Random random;
    private Level level;
    private State state;

    /**
     * Create a computer character.
     *
     * @param str strength
     * @param dex dexterity
     * @param pcp perception
     * @param spd speed
     * @param seed random number seed
     */
    public ComputerCharacter(int str, int dex, int pcp, int spd, long seed)
    {
        super(str, dex, pcp, spd);

        random = new Random(seed);

        double pct = random.nextDouble();
        if (pct < 0.333) {
            state = State.MEANDER;
        }

        state = State.ASLEEP;
    }

    /**
     * Unimplemented
     *
     * @param goal target point
     *
     * @throws CoreException always
     */
    public void buildPath(IMapPoint goal)
        throws CoreException
    {
        throw new UnimplementedError();
    }

    /**
     * Clear the stored path.
     */
    public void clearPath()
    {
        throw new UnimplementedError();
    }

    /**
     * Unimplemented
     *
     * @return never
     */
    public Level getLevel()
    {
        throw new UnimplementedError();
    }

    /**
     * Unimplemented
     *
     * @return nothing
     */
    public String getName()
    {
        throw new UnimplementedError();
    }

    /**
     * Get the boolean array indicating which cells in the current level
     * have been seen.
     *
     * @return two dimensional boolean array
     */
    public boolean[][] getSeenArray()
    {
        throw new UnimplementedError();
    }

    private void handleAsleepTurn()
    {
        double pct = random.nextDouble();
        if (pct < 0.05) {
            // 5% chance of waking up
            state = State.MEANDER;
        }
    }

    private void handleMeanderTurn()
    {
        double pct = random.nextDouble();
        if (pct >= 0.99) {
            // 1% chance of falling asleep
            state = State.ASLEEP;
        } else {
            final Direction startDir = Direction.random();

            Direction dir = startDir;
            do {
                try {
                    move(level.getMap(), dir);
                    return;
                } catch (CoreException ce) {
                    // mot that way!
                }
                dir = dir.next();
            } while (dir != startDir);
        }
    }

    private void handleInPursuitTurn()
    {
        throw new UnimplementedError();
    }

    /**
     * Does this character have an existing path?
     *
     * @return <tt>false</tt>
     */
    public boolean hasPath()
    {
        return false;
    }

    /**
     * Is this character a player?
     *
     * @return <tt>false</tt>
     */
    public boolean isPlayer()
    {
        return false;
    }

    /**
     * Move the computer character.
     *
     * @param dir direction
     *
     * @return number of turns
     *
     * @throws CoreException if there is a problem
     */
    public int move(Direction dir)
        throws CoreException
    {
        return move(level.getMap(), dir);
    }

    /**
     * Unimplemented
     *
     * @return number of turns
     *
     * @throws CoreException always
     */
    public int movePath()
        throws CoreException
    {
        throw new UnimplementedError();
    }

    /**
     * Set computer character's level
     *
     * @param lvl level
     *
     * @throws CoreException if there is a problem
     */
    public void setLevel(Level lvl)
        throws CoreException
    {
        boolean positioned = false;
        for (int i = 0; !positioned && i < MAX_ATTEMPTS; i++) {
            int cx = random.nextInt(lvl.getMaxX());
            int cy = random.nextInt(lvl.getMaxY());

            try {
                setLevel(lvl, cx, cy);
                positioned = true;
            } catch (CoreException ce) {
                this.level = null;
                // ignore exceptions
            }
        }

        if (!positioned) {
            throw new CoreException("Failed to position " + toString());
        }
    }

    /**
     * Set computer character's level
     *
     * @param lvl level
     * @param x x coordinate
     * @param y y coordinate
     *
     * @throws CoreException if there is a problem
     */
    public void setLevel(Level lvl, int x, int y)
        throws CoreException
    {
        lvl.addNonplayer(this, x, y);

        this.level = lvl;

        setPosition(x, y);
    }

    /**
     * Take a computer character's turn.
     */
    public void takeTurn()
    {
        switch (state) {
        case ASLEEP:
            handleAsleepTurn();
            break;
        case MEANDER:
            handleMeanderTurn();
            break;
        case IN_PURSUIT:
            handleInPursuitTurn();
            break;
        default:
            throw new Error("Unexpected state " + state);
        }
    }

    /**
     * Return debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return super.toString() + state;
    }
}
