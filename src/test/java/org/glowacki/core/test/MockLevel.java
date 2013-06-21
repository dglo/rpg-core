package org.glowacki.core.test;

import java.util.List;

import org.glowacki.core.ComputerCharacter;
import org.glowacki.core.CoreException;
import org.glowacki.core.ICharacter;
import org.glowacki.core.ILevel;
import org.glowacki.core.IMap;
import org.glowacki.core.IMapPoint;
import org.glowacki.core.Level;
import org.glowacki.core.MapException;
import org.glowacki.core.Terrain;
import org.glowacki.core.UnimplementedError;

public class MockLevel
    implements ILevel
{
    private String name;
    private IMap map;
    private ILevel prevLevel;
    private ILevel nextLevel;

    private boolean occupied;

    public MockLevel(String name, IMap map)
    {
        this.name = name;
        this.map = map;
    }

    public void addNonplayer(ComputerCharacter ch, int x, int y)
        throws CoreException
    {
        // do nothing
    }

    public IMapPoint enterDown(ICharacter ch)
        throws CoreException
    {
        if (map == null) {
            throw new Error("Map has not been set for " + name);
        }

        if (occupied) {
            throw new CoreException("Occupied");
        }

        return map.enterDown(ch);
    }

    public IMapPoint enterUp(ICharacter ch)
        throws CoreException
    {
        if (map == null) {
            throw new Error("Map has not been set for " + name);
        }

        if (occupied) {
            throw new CoreException("Occupied");
        }

        return map.enterUp(ch);
    }

    public void exit(ICharacter ch)
        throws CoreException
    {
        ch.setLevel(null);
    }

    public Iterable<ICharacter> listCharacters()
    {
        throw new UnimplementedError();
    }

    public IMap getMap()
    {
        return map;
    }

    public int getMaxX()
    {
        if (map == null) {
            throw new Error("Map has not been set for " + name);
        }

        return map.getMaxX();
    }

    public int getMaxY()
    {
        if (map == null) {
            throw new Error("Map has not been set for " + name);
        }

        return map.getMaxY();
    }

    public String getName()
    {
        throw new UnimplementedError();
    }

    public ILevel getNextLevel()
    {
        return nextLevel;
    }

    /**
     * Get the number of non-player characters on this level
     *
     * @return number of non-player characters
     */
    public int getNumberOfNonPlayerCharacters()
    {
        throw new UnimplementedError();
    }

    /**
     * Get the number of player characters on this level
     *
     * @return number of player characters
     */
    public int getNumberOfPlayerCharacters()
    {
        throw new UnimplementedError();
    }

    public ILevel getPreviousLevel()
    {
        return prevLevel;
    }

    public Terrain getTerrain(int x, int y)
        throws MapException
    {
        if (map == null) {
            throw new Error("Map has not been set");
        }

        return map.getTerrain(x, y);
    }

    public boolean isOccupied(int x, int y)
        throws MapException
    {
        throw new UnimplementedError();
    }

    public void moveTo(ICharacter ch, int x, int y)
        throws MapException
    {
        // do nothing
    }

    public void setMap(IMap map)
    {
        this.map = map;
    }

    public void setNextLevel(ILevel lvl)
    {
        nextLevel = lvl;
    }

    public void setOccupied()
    {
        occupied = true;
    }

    public void setPreviousLevel(ILevel lvl)
    {
        prevLevel = lvl;
    }
}
