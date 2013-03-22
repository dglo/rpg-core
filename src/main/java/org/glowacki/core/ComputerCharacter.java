package org.glowacki.core;

import java.util.Random;

public class ComputerCharacter
    extends BaseCharacter
{
    private static final int MAX_ATTEMPTS = 20;

    enum State { ASLEEP, MEANDER, IN_PURSUIT };

    private Random random;
    private Level level;
    private State state;

    public ComputerCharacter(int str, int dex, int spd, long seed)
    {
        super(str, dex, spd);

        random = new Random(seed);
        state = generateRandomState();
    }

    private State generateRandomState()
    {
        double pct = random.nextDouble();
        if (pct < 0.333) {
            return State.MEANDER;
        }

        return State.ASLEEP;
    }

    public Level getLevel()
    {
        throw new Error("Unimplemented");
    }

    public String getName()
    {
        throw new Error("Unimplemented");
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
                dir = dir.next();
            } while (dir != startDir);
        }
    }

    private void handleInPursuitTurn()
    {
        throw new UnimplementedError();
    }

    public boolean isPlayer()
    {
        return false;
    }

    public int move(Direction dir)
        throws CoreException
    {
        throw new Error("Unimplemented");
    }

    public void setLevel(Level lvl)
        throws CoreException
    {
        boolean positioned = false;
        for (int i = 0; !positioned && i < MAX_ATTEMPTS; i++) {
            int cx = random.nextInt(lvl.getMaxX());
            int cy = random.nextInt(lvl.getMaxY());

            try {
                lvl.addNonplayer(this, cx, cy);
                positioned = true;
            } catch (CoreException ce) {
                // ignore exceptions
            }
        }

        if (!positioned) {
            throw new CoreException("Failed to position " + toString());
        }

        this.level = lvl;
    }

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
        }
    }

    public String toString()
    {
        return super.toString() + state;
    }
}
