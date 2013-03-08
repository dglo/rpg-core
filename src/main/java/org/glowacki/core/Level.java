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
        throws LevelException
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
            if (newX > lvl.getMaxX()) {
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
            if (newY > lvl.getMaxY()) {
                return -1;
            }
            break;
        case CLIMB:
            t = lvl.get(newX, newY);
            if (t != Terrain.UPSTAIRS) {
                throw new LevelException("You cannot climb here");
            }

            return ch.move(t, false);
        case DESCEND:
            t = lvl.get(newX, newY);
            if (t != Terrain.DOWNSTAIRS) {
                throw new LevelException("You cannot descend here");
            }

            return ch.move(t, false);
        }

        t = lvl.get(newX, newY);

        if (!t.isMovable()) {
            return -1;
        }

        x = newX;
        y = newY;

        return ch.move(t, false);
    }

    public void position(Level l, int x, int y)
    {
        this.lvl = l;
        this.x = x;
        this.y = y;
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
 * A single point.
 */
class Point
{
    int x;
    int y;

    Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public String toString()
    {
        return String.format("(%d,%d)", x, y);
    }
}

/**
 * Description of a level.
 */
public class Level
{
    private String name;
    private Terrain[][] map;

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
     * @throws LevelException if there is a problem
     */
    public Level(String name, String[] rawMap)
        throws LevelException
    {
        if (rawMap == null || rawMap.length == 0 || rawMap[0] == null ||
            rawMap[0].length() == 0)
        {
            if (rawMap == null) {
                throw new LevelException("Null map");
            } else {
                String hgt = Integer.toString(rawMap.length);
                String wid = "?";
                if (rawMap.length > 0 && rawMap[0] != null) {
                    wid = Integer.toString(rawMap[0].length());
                }
                throw new LevelException("Bad map dimensions [" + hgt + ", " +
                                         wid + "]");
            }
        }

        this.name = name;
        map = buildTerrainFromMap(rawMap);
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
     * Build a terrain map.
     *
     * @param rawMap map of Strings describing the level
     */
    private static Terrain[][] buildTerrainFromMap(String[] rawMap)
    {
        int width = 0;
        for (int i = 0; i < rawMap.length; i++) {
            if (rawMap[i] != null && rawMap[i].length() > width) {
                width = rawMap[i].length();
            }
        }

        Terrain[][] map = new Terrain[rawMap.length][width];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Terrain t;
                if (rawMap[y] == null || x >= rawMap[y].length()) {
                    t = Terrain.UNKNOWN;
                } else {
                    t = Terrain.getTerrain(rawMap[y].charAt(x));
                }
                map[y][x] = t;
            }
        }

        return map;
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
        Point p = find(Terrain.UPSTAIRS);
        if (p == null) {
            throw new LevelException("Map has no up staircase");
        }

        ch.position(this, p.x, p.y);
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
        Point p = find(Terrain.DOWNSTAIRS);
        if (p == null) {
            throw new LevelException("Map has no down staircase");
        }

        ch.position(this, p.x, p.y);
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

        ch.position(null, -1, -1);
    }

    /**
     * Find the first occurrence of the specified terrain.
     *
     * @param t terrain to find
     *
     * @return null if the terrain cannot be found on this level
     */
    private Point find(Terrain t)
    {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == t) {
                    return new Point(x, y);
                }
            }
        }

        return null;
    }

    /**
     * Get the terrain found at the specified coordinates.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return terrain at the specified point
     *
     * @throws LevelException if the point is not valid
     */
    public Terrain get(int x, int y)
        throws LevelException
    {
        if (y < 0 || y >= map.length) {
            throw new LevelException("Bad Y coordinate in (" + x + "," + y +
                                     "), max is " + getMaxY());
        } else if (x < 0 || x >= map[y].length) {
            throw new LevelException("Bad X coordinate in (" + x + "," + y +
                                     "), max is " + getMaxX());
        }

        return map[y][x];
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
     * Get maximum X coordinate for this level.
     *
     * @return maximum addressable X coordinate
     */
    public int getMaxX()
    {
        return map[0].length - 1;
    }

    /**
     * Get maximum Y coordinate for this level.
     *
     * @return maximum addressable Y coordinate
     */
    public int getMaxY()
    {
        return map.length - 1;
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
     * Get a graphic representation of this level.
     *
     * @return string representation of level with embedded newlines
     */
    public String getPicture()
    {
        StringBuilder buf = new StringBuilder();

        for (int y = 0; y < map.length; y++) {
            if (y > 0) {
                buf.append('\n');
            }

            for (int x = 0; x < map[y].length; x++) {
                char ch;
                if (map[y][x] != Terrain.WALL) {
                    ch = Terrain.getCharacter(map[y][x]);
                } else {
                    if ((x > 0 && map[y][x - 1] == Terrain.WALL) ||
                        (x < map[y].length - 1 &&
                         map[y][x + 1] == Terrain.WALL))
                    {
                        ch = '-';
                    } else {
                        ch = '|';
                    }
                }

                buf.append(ch);
            }
        }

        return buf.toString();
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
        return String.format("Level[%s %dx%d ch*%d]", name, map.length,
                             map[0].length, characters.size());
    }
}

