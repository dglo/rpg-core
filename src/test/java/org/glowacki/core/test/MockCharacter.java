package org.glowacki.core.test;

import org.glowacki.core.CoreException;
import org.glowacki.core.Direction;
import org.glowacki.core.ICharacter;
import org.glowacki.core.IMapPoint;
import org.glowacki.core.Level;
import org.glowacki.core.UnimplementedError;
import org.glowacki.core.event.EventListener;

public class MockCharacter
    implements ICharacter
{
    private static int nextId;
    private int id;

    private String name;
    private boolean player;
    private int x;
    private int y;
    private Level level;

    public MockCharacter(String name)
    {
        this(name, false);
    }

    public MockCharacter(String name, boolean player)
    {
        this.id = nextId++;
        this.name = name;
        this.player = player;
    }

    /**
     * Add an event listener.
     *
     * @param listener new listener
     */
    public void addEventListener(EventListener listener)
    {
        throw new UnimplementedError();
    }

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
     * Clear the current position.
     */
    public void clearPosition()
    {
        x = -1;
        y = -1;
    }

    public int getId()
    {
        return id;
    }

    public Level getLevel()
    {
        return level;
    }

    public String getName()
    {
        return name;
    }

    public boolean[][] getSeenArray()
    {
        throw new UnimplementedError();
    }

    public int getSightDistance()
    {
        throw new UnimplementedError();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public boolean hasPath()
    {
        return false;
    }

    public boolean isPlayer()
    {
        return player;
    }

    public int move(Direction dir)
        throws CoreException
    {
        throw new UnimplementedError();
    }

    public int movePath()
        throws CoreException
    {
        throw new UnimplementedError();
    }

    public void setLevel(Level level)
    {
        this.level = level;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void takeTurn()
    {
        throw new UnimplementedError();
    }

    public String toString()
    {
        return "Mock:" + name;
    }
}
