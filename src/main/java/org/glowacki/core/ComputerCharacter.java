package org.glowacki.core;

import org.glowacki.core.event.StateEvent;
import org.glowacki.core.util.IRandom;

/**
 * Computer character.
 */
public class ComputerCharacter
    extends BaseCharacter
{
    private static final int MAX_ATTEMPTS = 20;

    private IRandom random;
    private State state;

    /**
     * Create a computer character.
     *
     * @param random random number generator
     * @param str strength
     * @param dex dexterity
     * @param pcp perception
     * @param spd speed
     */
    public ComputerCharacter(IRandom random, int str, int dex, int pcp,
                             int spd)
    {
        super(str, dex, pcp, spd);

        this.random = random;

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

    /**
     * Get the visible cell array
     *
     * @return array of visible cells
     */
    public boolean[][] getVisible()
    {
        throw new UnimplementedError();
    }

    private void handleAsleepTurn()
    {
        double pct = random.nextDouble();
        if (pct < 0.05) {
            // 5% chance of waking up
            final State oldState = state;
            state = State.MEANDER;
            sendEvent(new StateEvent(this, oldState, state));
        }
    }

    private void handleMeanderTurn()
    {
        double pct = random.nextDouble();
        if (pct >= 0.99) {
            // 1% chance of falling asleep
            final State oldState = state;
            state = State.ASLEEP;
            sendEvent(new StateEvent(this, oldState, state));
        } else {
            final Direction startDir =
                Direction.getDirection(random.nextInt());

            Direction dir = startDir;
            do {
                try {
                    move(dir);
                    return;
                } catch (CoreException ce) {
                    // not that way!
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
     * Has the specified point been seen?
     *
     * @param px X coordinate
     * @param py Y coordinate
     *
     * @return <tt>true</tt> if the point has been seen
     */
    public boolean isSeen(int px, int py)
    {
        throw new UnimplementedError();
    }

    /**
     * Is the specified point visible?
     *
     * @param px X coordinate
     * @param py Y coordinate
     *
     * @return <tt>true</tt> if the point is visible
     */
    public boolean isVisible(int px, int py)
    {
        throw new UnimplementedError();
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
     * Unimplemented
     *
     * @return nothing
     */
    public boolean onStaircase()
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
    public void setLevel(ILevel lvl)
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
                // ignore exceptions
                clearLevel();
            }
        }

        if (!positioned) {
            throw new CharacterException("Failed to position " + toString());
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
    public void setLevel(ILevel lvl, int x, int y)
        throws CoreException
    {
        lvl.addNonplayer(this, x, y);

        super.setLevel(lvl);

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
     * Unimplemented
     *
     * @return nothing
     */
    public int useStaircase()
    {
        throw new UnimplementedError();
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
