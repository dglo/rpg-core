package org.glowacki.core.test;

import org.glowacki.core.CoreException;
import org.glowacki.core.Direction;
import org.glowacki.core.ICharacter;
import org.glowacki.core.ILevel;
import org.glowacki.core.IMapPoint;
import org.glowacki.core.IWeapon;
import org.glowacki.core.Level;
import org.glowacki.core.UnimplementedError;
import org.glowacki.core.event.EventListener;
import org.glowacki.core.util.IRandom;

public class MockCharacter
    implements ICharacter
{
    private static int nextId;
    private int id;

    private String name;
    private boolean player;
    private int x;
    private int y;
    private ILevel level;

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

    public void attack(IRandom random, ICharacter ch)
    {
        attack(random, ch, null);
    }

    public void attack(IRandom random, ICharacter ch, IWeapon weapon)
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

    /**
     * Compare this object against another
     *
     * @param obj object being compared
     *
     * @return the usual comparison values
     */
    public int compareTo(ICharacter ch)
    {
        if (ch == null) {
            return 1;
        }

        return ch.getId() - id;
    }

    public int getAttackPercent(IWeapon weapon)
    {
        throw new UnimplementedError();
    }

    public int getDefendPercent(IWeapon weapon)
    {
        throw new UnimplementedError();
    }

    public int getId()
    {
        return id;
    }

    public ILevel getLevel()
    {
        return level;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Get the character which occupies the specified point
     *
     * @param px X coordinate
     * @param py Y coordinate
     *
     * @return <tt>null</tt> if the point is not occupied
     */
    public ICharacter getOccupant(int px, int py)
        throws CoreException
    {
        throw new UnimplementedError();
    }

    public boolean[][] getSeenArray()
    {
        throw new UnimplementedError();
    }

    public int getSightDistance()
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

    /**
     * Is this character in a neighboring cell?
     *
     * @param ch character
     *
     * @return <tt>true</tt> if this character is a neighbor
     */
    public boolean isNeighbor(ICharacter ch)
    {
        throw new UnimplementedError();
    }

    public boolean isPlayer()
    {
        return player;
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
     * List all characters which can be seen by this character
     *
     * @return iterable list of visible characters
     */
    public Iterable<ICharacter> listVisibleCharacters()
        throws CoreException
    {
        throw new UnimplementedError();
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

    public boolean onStaircase()
    {
        throw new UnimplementedError();
    }

    public void setLevel(ILevel level)
    {
        this.level = level;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void takeDamage(IRandom random, ICharacter ch, IWeapon weapon)
    {
        throw new UnimplementedError();
    }

    public void takeTurn()
    {
        throw new UnimplementedError();
    }

    public int useStaircase()
    {
        throw new UnimplementedError();
    }

    public String toString()
    {
        return "Mock:" + name;
    }
}
