package org.glowacki.core;

import java.util.ArrayList;
import java.util.List;

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

class LevelException
    extends CoreException
{
    LevelException(String msg) { super(msg); }
}

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

public class Level
{
    private String name;
    private Terrain[][] map;

    private Level prevLevel;
    private Level nextLevel;

    private ArrayList<MovableCharacter> characters =
        new ArrayList<MovableCharacter>();

    public Level(String name, String[] rawMap)
        throws LevelException
    {
        if (rawMap == null || rawMap.length == 0 || rawMap[0].length() == 0) {
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

    public void addNextLevel(Level l)
        throws LevelException
    {
        if (nextLevel != null) {
            throw new LevelException("Cannot overwrite existing level");
        } else if (l.prevLevel != null) {
            throw new LevelException("Cannot overwrite previous level");
        }

        nextLevel = l;
        l.prevLevel = this;
    }

    private static Terrain[][] buildTerrainFromMap(String[] rawMap)
    {
        int width = 0;
        for (int i = 0; i < rawMap.length; i++) {
            if (rawMap[i].length() > width) {
                width = rawMap[i].length();
            }
        }

        Terrain[][] map = new Terrain[rawMap.length][width];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                Terrain t;
                if (x >= rawMap[y].length()) {
                    t = Terrain.UNKNOWN;
                } else {
                    switch (rawMap[y].charAt(x)) {
                    case '+':
                        t = Terrain.DOOR;
                        break;
                    case '>':
                        t = Terrain.DOWNSTAIRS;
                        break;
                    case '.':
                        t = Terrain.FLOOR;
                        break;
                    case '#':
                        t = Terrain.TUNNEL;
                        break;
                    case '<':
                        t = Terrain.UPSTAIRS;
                        break;
                    case '-':
                    case '|':
                        t = Terrain.WALL;
                        break;
                    case '~':
                        t = Terrain.WATER;
                        break;
                    default:
                        t = Terrain.UNKNOWN;
                        break;
                    }
                }
                map[y][x] = t;
            }
        }

        return map;
    }

    public MovableCharacter enterDown(Character ch)
        throws LevelException
    {
        MovableCharacter mch = new LevelCharacter(this, ch, 0, 0);
        enterDown(mch);
        return mch;
    }

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

    public MovableCharacter enterUp(Character ch)
        throws LevelException
    {
        MovableCharacter mch = new LevelCharacter(this, ch, 0, 0);
        enterUp(mch);
        return mch;
    }

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

    public void exit(MovableCharacter ch)
        throws LevelException
    {
        if (!characters.remove(ch)) {
            throw new LevelException(ch.getName() + " was not on this level");
        }
    }

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

    public Terrain get(int x, int y)
        throws LevelException
    {
        if (y < 0 || y >= map.length) {
            throw new LevelException("Bad Y coordinate in (" + x + "," + y +
                                     "), max is " + map.length);
        } else if (x < 0 || x >= map[y].length) {
            throw new LevelException("Bad X coordinate in (" + x + "," + y +
                                     "), max is " + map[y].length);
        }

        return map[y][x];
    }

    public List<MovableCharacter> getCharacters()
    {
        return new ArrayList<MovableCharacter>(characters);
    }

    public int getMaxX()
    {
        return map[0].length - 1;
    }

    public int getMaxY()
    {
        return map.length - 1;
    }

    public String getName()
    {
        return name;
    }

    public Level getNextLevel()
    {
        return nextLevel;
    }

    public String getPicture()
    {
        StringBuilder buf = new StringBuilder();

        for (int y = 0; y < map.length; y++) {
            if (y > 0) {
                buf.append('\n');
            }

            for (int x = 0; x < map[y].length; x++) {
                char ch;
                switch (map[y][x]) {
                case DOOR:
                    ch = '+';
                    break;
                case DOWNSTAIRS:
                    ch = '<';
                    break;
                case FLOOR:
                    ch = '.';
                    break;
                case TUNNEL:
                    ch = '#';
                    break;
                case UPSTAIRS:
                    ch = '>';
                    break;
                case WALL:
                    if ((x > 0 && map[y][x-1] == Terrain.WALL) ||
                        (x < map[y].length - 1 &&
                         map[y][x+1] == Terrain.WALL))
                    {
                        ch = '-';
                    } else {
                        ch = '|';
                    }
                    break;
                case WATER:
                    ch = '~';
                    break;
                case UNKNOWN:
                    ch = ' ';
                    break;
                default:
                    ch = '?';
                    break;
                }

                buf.append(ch);
            }
        }

        return buf.toString();
    }

    public Level getPreviousLevel()
    {
        return prevLevel;
    }

    public String toString()
    {
        return String.format("Level[%s %dx%d ch*%d]", name, map.length,
                             map[0].length, characters.size());
    }
}

