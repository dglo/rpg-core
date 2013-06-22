package org.glowacki.core;

import java.util.HashMap;
import java.util.List;

import org.glowacki.core.astar.MapPathFinder;
import org.glowacki.core.event.ChangeLevelEvent;

/**
 * Player-related exceptions
 */
class PlayerException
    extends CharacterException
{
    PlayerException(String msg)
    {
        super(msg);
    }
}

/**
 * Player character
 */
public class PlayerCharacter
    extends BaseCharacter
{
    private String name;

    private List<IMapPoint> path;

    private HashMap<ILevel, boolean[][]> seenMap;

    /**
     * Create a player character.
     *
     * @param name player name
     * @param str strength
     * @param dex dexterity
     * @param pcp perception
     * @param spd speed
     */
    public PlayerCharacter(String name, int str,
                           int dex, int pcp, int spd)
    {
        super(str, dex, pcp, spd);

        this.name = name;
    }

    /**
     * Build a path from the current position to the goal.
     *
     * @param goal target point
     *
     * @throws CoreException if there is a problem
     */
    public void buildPath(IMapPoint goal)
        throws CoreException
    {
        ILevel level = getLevel();
        if (level == null) {
            throw new PlayerException("Level has not been set for " + name);
        } else if (goal.getX() < 0 || goal.getX() > level.getMap().getMaxX() ||
            goal.getY() < 0 || goal.getY() > level.getMap().getMaxY())
        {
            final String msg =
                String.format("Bad goal [%d,%d]", goal.getX(), goal.getY());
            throw new PlayerException(msg);
        }

        MapPathFinder pathFinder = new MapPathFinder(level.getMap());
        path = pathFinder.findBestPath(this, goal);
    }

    /**
     * Clear the stored path.
     */
    public void clearPath()
    {
        path = null;
    }

    private int climbStairs()
        throws CoreException
    {
        ILevel level = getLevel();
        if (level == null) {
            throw new PlayerException("Level has not been set for " + name);
        }

        ILevel prevLevel = level.getPreviousLevel();
        if (prevLevel == null) {
            throw new PlayerException("You cannot exit here");
        }

        final ILevel oldLevel = level;
        final int oldX = getX();
        final int oldY = getY();

        level.exit(this);
        try {
            prevLevel.enterUp(this);

            setLevel(prevLevel);

            sendEvent(new ChangeLevelEvent(this, oldLevel, oldX, oldY,
                                           prevLevel, getX(), getY()));
        } catch (CoreException ce) {
            oldLevel.moveTo(this, oldX, oldY);
            setPosition(oldX, oldY);
            setLevel(oldLevel);
            throw ce;
        }

        return subtractMoveCost(level.getMap(), Direction.CLIMB);
    }

    private int descendStairs()
        throws CoreException
    {
        ILevel level = getLevel();
        if (level == null) {
            throw new PlayerException("Level has not been set for " + name);
        }

        ILevel nextLevel = level.getNextLevel();
        if (nextLevel == null) {
            throw new PlayerException("You are at the bottom");
        }

        final ILevel oldLevel = level;
        final int oldX = getX();
        final int oldY = getY();

        level.exit(this);
        try {
            nextLevel.enterDown(this);

            setLevel(nextLevel);

            sendEvent(new ChangeLevelEvent(this, oldLevel, oldX, oldY,
                                           nextLevel, getX(), getY()));
        } catch (CoreException ce) {
            oldLevel.moveTo(this, oldX, oldY);
            setPosition(oldX, oldY);
            setLevel(oldLevel);
            throw ce;
        }

        return subtractMoveCost(level.getMap(), Direction.DESCEND);
    }

    private Direction findDirection(IMapPoint goal)
        throws PlayerException
    {
        final int x = goal.getX() - getX();
        final int y = goal.getY() - getY();

        if (x == -1) {
            if (y == -1) {
                return Direction.LEFT_UP;
            } else if (y == 0) {
                return Direction.LEFT;
            } else if (y == 1) {
                return Direction.LEFT_DOWN;
            }
        } else if (x == 0) {
            if (y == -1) {
                return Direction.UP;
            } else if (y == 1) {
                return Direction.DOWN;
            } else if (y == 0) {
                final String msg =
                    String.format("Goal [%d,%d] is current position of %s",
                                  goal.getX(), goal.getY(), name);
                throw new PlayerException(msg);
            }
        } else if (x == 1) {
            if (y == -1) {
                return Direction.RIGHT_UP;
            } else if (y == 0) {
                return Direction.RIGHT;
            } else if (y == 1) {
                return Direction.RIGHT_DOWN;
            }
        }

        final String msg =
            String.format("Goal [%d,%d] for %s is more than one square" +
                          " from [%d,%d]", goal.getX(), goal.getY(), name,
                          getX(), getY());
        throw new PlayerException(msg);
    }

    /**
     * Return character's name.
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the boolean array indicating which cells in the current level
     * have been seen.
     *
     * @return two dimensional boolean array
     */
    public boolean[][] getSeenArray()
    {
        ILevel level = getLevel();
        if (level == null) {
            return null;
        }

        if (seenMap == null) {
            seenMap = new HashMap<ILevel, boolean[][]>();
        }

        boolean[][] seen = seenMap.get(level);
        if (seen == null) {
            seen = new boolean[level.getMaxX() + 1][level.getMaxY() + 1];
            seenMap.put(level, seen);
        }

        return seen;
    }

    /**
     * Does this character have an existing path?
     *
     * @return <tt>true</tt> if this character has an ongoing path
     */
    public boolean hasPath()
    {
        return path != null && path.size() > 0;
    }

    /**
     * Is this character a player?
     *
     * @return <tt>true</tt> if this character is a player
     */
    public boolean isPlayer()
    {
        return true;
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
        boolean[][] seen = getSeenArray();
        if (seen == null) {
            return false;
        }

        return seen[px][py];
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
        boolean[][] visible = getVisible();
        if (visible == null) {
            return false;
        }

        return visible[px][py];
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
        ILevel level = getLevel();
        if (level == null) {
            throw new PlayerException("Level has not been set for " + name);
        }

        if (dir == Direction.CLIMB) {
            Terrain t = level.getTerrain(getX(), getY());
            if (t != Terrain.UPSTAIRS) {
                throw new PlayerException("You cannot climb here");
            }

            return climbStairs();
        } else if (dir == Direction.DESCEND) {
            Terrain t = level.getTerrain(getX(), getY());
            if (t != Terrain.DOWNSTAIRS) {
                throw new PlayerException("You cannot descend here");
            }

            return descendStairs();
        } else {
            return super.move(dir);
        }
    }

    /**
     * Move to the next point in the path.
     *
     * @return number of turns
     *
     * @throws CoreException always
     */
    public int movePath()
        throws CoreException
    {
        if (path == null || path.size() == 0) {
            throw new PlayerException("No current path");
        }

        IMapPoint nextPt = path.remove(0);
        Direction dir = findDirection(nextPt);

        int rtnval;
        try {
            rtnval = move(dir);
        } catch (CoreException ce) {
            path = null;
            throw ce;
        }

        return rtnval;
    }

    /**
     * Is the player on a staircase?
     *
     * @return <tt>true</tt> if player is on a staircase
     */
    public boolean onStaircase()
    {
        ILevel level = getLevel();
        if (level == null) {
            return false;
        }

        Terrain t;
        try {
            t = level.getTerrain(getX(), getY());
        } catch (MapException me) {
            return false;
        }

        return t == Terrain.UPSTAIRS || t == Terrain.DOWNSTAIRS;
    }

    /**
     * Set character's current level
     *
     * @param lvl level
     */
    public void setLevel(ILevel lvl)
        throws CoreException
    {
        super.setLevel(lvl);
        clearPath();
    }

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        throw new UnimplementedError();
    }

    /**
     * Use the staircase in the player's current position.
     *
     * @return number of turns
     *
     * @throws CoreException if there is a problem
     */
    public int useStaircase()
        throws CoreException
    {
        ILevel level = getLevel();
        if (level == null) {
            throw new PlayerException("Level has not been set for " + name);
        }

        Terrain t = level.getTerrain(getX(), getY());
        if (t == Terrain.UPSTAIRS) {
            return climbStairs();
        } else if (t == Terrain.DOWNSTAIRS) {
            return descendStairs();
        }

        throw new PlayerException(name + " is not on a staircase");
    }

    /**
     * Return a debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return name + super.toString();
    }
}
