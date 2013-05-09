package org.glowacki.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Level
 */
public class Level
{
    private String name;
    private Map map;

    private Level prevLevel;
    private Level nextLevel;

    private List<ICharacter> players = new ArrayList<ICharacter>();
    private List<ICharacter> nonplayers = new ArrayList<ICharacter>();

    /**
     * Create a level.
     *
     * @param name level name
     * @param map map of this level
     */
    public Level(String name, Map map)
    {
        this.name = name;
        this.map = map;
    }

    /**
     * Add the next level.
     *
     * @param lvl next level
     *
     * @throws LevelException if there is a problem
     */
    public void addNextLevel(Level lvl)
        throws LevelException
    {
        if (lvl == null) {
            throw new LevelException("Next level cannot be null");
        } else if (nextLevel != null) {
            throw new LevelException("Level " + name +
                                     " already has a next level");
        } else if (lvl.prevLevel != null) {
            throw new LevelException("Level " + lvl.name +
                                     " already has a previous level");
        }

        nextLevel = lvl;
        lvl.prevLevel = this;
    }

    /**
     * Add a nonplayer to this level.
     *
     * @param ch character
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws CoreException if there is a problem
     */
    public void addNonplayer(ComputerCharacter ch, int x, int y)
        throws CoreException
    {
        map.insertObject(ch, x, y);
        nonplayers.add(ch);
    }

    /**
     * Add the previous level.
     *
     * @param lvl previous level
     *
     * @throws LevelException if there is a problem
     */
    public void addPreviousLevel(Level lvl)
        throws LevelException
    {
        if (lvl == null) {
            throw new LevelException("Previous level cannot be null");
        } else if (prevLevel != null) {
            throw new LevelException("Level " + name +
                                     " already has a previous level");
        } else if (lvl.nextLevel != null) {
            throw new LevelException("Level " + lvl.name +
                                     " already has a next level");
        }

        prevLevel = lvl;
        lvl.nextLevel = this;
    }

    /**
     * This character is entering this level from above.
     *
     * @param ch character
     *
     * @throws CoreException if the level doesn't have an up staircase
     */
    public void enterDown(ICharacter ch)
        throws CoreException
    {
        if (ch.getLevel() != null) {
            throw new LevelException("Character " + ch.getName() +
                                     " cannot be on level " + ch.getLevel() +
                                     " and " + name);
        }

        IMapPoint p = map.enterDown(ch);

        if (ch.isPlayer()) {
            players.add(ch);
        } else {
            nonplayers.add(ch);
        }

        ch.setLevel(this);
    }

    /**
     * This character is entering this level from below.
     *
     * @param ch character
     *
     * @throws CoreException if the level doesn't have a down staircase
     */
    public void enterUp(ICharacter ch)
        throws CoreException
    {
        if (ch.getLevel() != null) {
            throw new LevelException("Character " + ch.getName() +
                                     " cannot be on level " + ch.getLevel() +
                                     " and " + name);
        }

        IMapPoint p = map.enterUp(ch);

        if (ch.isPlayer()) {
            players.add(ch);
        } else {
            nonplayers.add(ch);
        }

        ch.setLevel(this);
    }

    /**
     * Remove the character from this level.
     *
     * @param ch character to remove
     *
     * @throws CoreException if the character is not on this level
     */
    public void exit(ICharacter ch)
        throws CoreException
    {
        if (ch.getLevel() == null) {
            throw new LevelException("Character " + ch.getName() +
                                     " is not on level " + name);
        }

        map.removeObject(ch);
        ch.setLevel(null);

        boolean result;
        if (ch.isPlayer()) {
            result = players.remove(ch);
        } else {
            result = nonplayers.remove(ch);
        }

        if (!result) {
            throw new LevelException(ch.getName() + " was not on this level");
        }
    }

    /**
     * Get a list of characters on this level.
     *
     * @return list of characters
     */
    public List<ICharacter> getCharacters()
    {
        List<ICharacter> characters =
            new ArrayList<ICharacter>(players);
        characters.addAll(nonplayers);
        return characters;
    }

    /**
     * Get the map of this level.
     *
     * @return map
     */
    public Map getMap()
    {
        return map;
    }

    /**
     * Get the maximum X coordinate.
     *
     * @return maximum X coordinate
     */
    public int getMaxX()
    {
        return map.getMaxX();
    }

    /**
     * Get the maximum Y coordinate.
     *
     * @return maximum Y coordinate
     */
    public int getMaxY()
    {
        return map.getMaxY();
    }

    /**
     * Get the level name.
     *
     * @return name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the next level.
     *
     * @return next level
     */
    public Level getNextLevel()
    {
        return nextLevel;
    }

    /**
     * Get an ASCII representation of the map.
     *
     * @return string representation of the map
     */
    public String getPicture()
    {
        return map.getPicture();
    }

    /**
     * Get the previous level.
     *
     * @return previous level
     */
    public Level getPreviousLevel()
    {
        return prevLevel;
    }

    /**
     * Get the terrain at the specified point.
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return terrain
     *
     * @throws MapException if there is a problem
     */
    public Terrain getTerrain(int x, int y)
        throws MapException
    {
        return map.getTerrain(x, y);
    }

    /**
     * Is the specified point occupied?
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return <tt>true</tt>if the point is occupied
     *
     * @throws MapException if there is a problem
     */
    public boolean isOccupied(int x, int y)
        throws MapException
    {
        return map.isOccupied(x, y);
    }

    /**
     * Move the character to the specified point.
     *
     * @param ch character
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws MapException if there is a problem
     */
    public void moveTo(ICharacter ch, int x, int y)
        throws MapException
    {
        map.moveTo(ch, x, y);
    }

    /**
     * Return a debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        String pStr;
        if (players.size() == 0) {
            pStr = "";
        } else {
            pStr = " p*" + players.size();
        }

        String nStr;
        if (nonplayers.size() == 0) {
            nStr = "";
        } else {
            nStr = " n*" + nonplayers.size();
        }

        return name + "|" + map + "|" + pStr + nStr;
    }
}
