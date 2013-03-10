package org.glowacki.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal class describing a movable character.
 */
class LevelCharacter
    implements MovableCharacter
{
    private Level lvl;
    private Character ch;
    private int x;
    private int y;

    LevelCharacter(Level lvl, Character ch, int x, int y)
    {
        this.lvl = lvl;
        this.ch = ch;
        this.x = x;
        this.y = y;
    }

    public Level getLevel()
    {
        return lvl;
    }

    public String getName()
    {
        return ch.getName();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int move(Direction dir)
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
            if (newX > lvl.getMap().getMaxX()) {
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
            if (newY > lvl.getMap().getMaxY()) {
                return -1;
            }
            break;
        case CLIMB:
            t = lvl.getMap().get(newX, newY);
            if (t != Terrain.UPSTAIRS) {
                throw new LevelException("You cannot climb here");
            }

            Level prevLevel = lvl.getPreviousLevel();
            if (prevLevel == null) {
                throw new LevelException("You cannot exit here");
            }

            lvl.exit(this);
            prevLevel.enterUp(this);

            lvl = prevLevel;

            return ch.move(t, false);
        case DESCEND:
            t = lvl.getMap().get(newX, newY);
            if (t != Terrain.DOWNSTAIRS) {
                throw new LevelException("You cannot descend here");
            }

            Level nextLevel = lvl.getNextLevel();
            if (nextLevel == null) {
                throw new LevelException("You are at the bottom");
            }

            lvl.exit(this);
            nextLevel.enterDown(this);

            lvl = nextLevel;

            return ch.move(t, false);
        }

        t = lvl.getMap().get(newX, newY);

        if (!t.isMovable()) {
            return -1;
        }

        x = newX;
        y = newY;

        return ch.move(t, false);
    }

    public void position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        ch.takeTurn();
    }

    public String toString()
    {
        return String.format("%s->%s[%d,%d]", ch.getName(), lvl.getName(), x,
                             y);
    }
}

/**
 * Exceptions for this class.
 */
class LevelException
    extends CoreException
{
    /**
     * Create a level exception.
     *
     * @param msg error message
     */
    LevelException(String msg)
    {
        super(msg);
    }
}

/**
 * Description of a level.
 */
public class Level
{
    private String name;
    private TerrainMap map;

    private Level prevLevel;
    private Level nextLevel;

    private ArrayList<MovableCharacter> characters =
        new ArrayList<MovableCharacter>();

    /**
     * Create a level.
     *
     * @param name level name
     * @param rawMap string description of this level
     *
     * @throws CoreException if there is a problem
     */
    public Level(String name, String[] rawMap)
        throws CoreException
    {
        this.name = name;
        this.map = new TerrainMap(rawMap);
    }

    /**
     * Add the level below this one.
     *
     * @param l lower level
     *
     * @throws LevelException if there is a problem
     */
    public void addNextLevel(Level l)
        throws LevelException
    {
        if (l == null) {
            throw new LevelException("Next level cannot be null");
        } else if (nextLevel != null) {
            throw new LevelException("Cannot overwrite existing level");
        } else if (l.prevLevel != null) {
            throw new LevelException("Cannot overwrite previous level");
        }

        nextLevel = l;
        l.prevLevel = this;
    }

    /**
     * This character is entering this level from above.
     *
     * @param ch character
     *
     * @return wrapped character
     *
     * @throws LevelException if the level doesn't have an up staircase
     */
    public MovableCharacter enterDown(Character ch)
        throws LevelException
    {
        MovableCharacter mch = new LevelCharacter(this, ch, 0, 0);
        enterDown(mch);
        return mch;
    }

    /**
     * This character is entering this level from above.
     *
     * @param ch character
     *
     * @throws LevelException if the level doesn't have an up staircase
     */
    public void enterDown(MovableCharacter ch)
        throws LevelException
    {
        MapPoint p = map.find(Terrain.UPSTAIRS);
        if (p == null) {
            throw new LevelException("Map has no up staircase");
        }

        ch.position(p.x, p.y);
        characters.add(ch);
    }

    /**
     * This character is entering this level from below.
     *
     * @param ch character
     *
     * @throws LevelException if the level doesn't have a down staircase
     */
    public void enterUp(MovableCharacter ch)
        throws LevelException
    {
        MapPoint p = map.find(Terrain.DOWNSTAIRS);
        if (p == null) {
            throw new LevelException("Map has no down staircase");
        }

        ch.position(p.x, p.y);
        characters.add(ch);
    }

    /**
     * Remove the character from this level.
     *
     * @param ch character to remove
     *
     * @throws LevelException if the character is not on this level
     */
    public void exit(MovableCharacter ch)
        throws LevelException
    {
        if (!characters.remove(ch)) {
            throw new LevelException(ch.getName() + " was not on this level");
        }

        ch.position(-1, -1);
    }

    /**
     * Get a list of characters on this level.
     *
     * @return list of characters
     */
    public List<MovableCharacter> getCharacters()
    {
        return new ArrayList<MovableCharacter>(characters);
    }

    /**
     * Get terrain map.
     *
     * @return map
     */
    public TerrainMap getMap()
    {
        return map;
    }

    /**
     * Get level name
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get next level
     *
     * @return next level (may be null)
     */
    public Level getNextLevel()
    {
        return nextLevel;
    }

    /**
     * Get previous level
     *
     * @return previous level (may be null)
     */
    public Level getPreviousLevel()
    {
        return prevLevel;
    }

    /**
     * Return debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("Level[%s %s ch*%d]", name, map,
                             characters.size());
    }
}
