package org.glowacki.core;

import java.util.ArrayList;
import java.util.List;

import org.glowacki.core.event.CoreEvent;
import org.glowacki.core.event.EventListener;
import org.glowacki.core.event.MoveEvent;

/**
 * Character-related exception
 */
class CharacterException
    extends CoreException
{
    CharacterException(String msg)
    {
        super(msg);
    }
}

/**
 * Base character
 */
public abstract class BaseCharacter
    implements ICharacter, IMapPoint
{
    /** Used to compute movement cost */
    public static final double SQRT_2 = 1.41421356;

    private static int nextId;

    private List<EventListener> listeners = new ArrayList<EventListener>();

    private int id;

    private int str;
    private int dex;
    private int pcp;
    private int spd;

    private int x;
    private int y;

    private double timeLeft;

    /**
     * Create a character.
     *
     * @param str strength
     * @param dex dexterity
     * @param spd speed
     * @param pcp perception
     */
    public BaseCharacter(int str, int dex, int pcp, int spd)
    {
        this.id = nextId++;

        this.str = str;
        this.dex = dex;
        this.pcp = pcp;
        this.spd = spd;

        clearPosition();
    }

    /**
     * Add an event listener.
     *
     * @param listener new listener
     */
    public void addEventListener(EventListener listener)
    {
        listeners.add(listener);
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
     * Get unique character ID.
     *
     * @return id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the distance this character can see (in number of tiles)
     *
     * @return distance
     */
    public int getSightDistance()
    {
        return pcp / 2;
    }

    /**
     * Return X coordinate.
     *
     * @return x coordinate
     */
    public int getX()
    {
        return x;
    }

    /**
     * Return Y coordinate.
     *
     * @return y coordinate
     */
    public int getY()
    {
        return y;
    }

    /**
     * Move in the specified direction.
     *
     * @param map current map
     * @param dir direction
     *
     * @return number of turns
     *
     * @throws MapException if there is a problem
     */
    int move(IMap map, Direction dir)
        throws MapException
    {
        try {
            final int fromX = x;
            final int fromY = y;
            map.moveDirection(this, dir);
            sendEvent(new MoveEvent(this, fromX, fromY, x, y));
        } catch (MapException me) {
            return -1;
        }

        return subtractMoveCost(map, dir);
    }

    /**
     * Send an event to all listeners.
     *
     * @param evt event being sent
     */
    public void sendEvent(CoreEvent evt)
    {
        for (EventListener l : listeners) {
            l.send(evt);
        }
    }

    /**
     * Set the character's position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Subtract the cost of this move from the character's time.
     *
     * @param map map
     * @param dir direction
     *
     * @return movement cost
     *
     * @throws MapException if the current position is not valid
     */
    int subtractMoveCost(IMap map, Direction dir)
        throws MapException
    {
        Terrain terrain = map.getTerrain(getX(), getY());

        final boolean diagonal =
            (dir == Direction.LEFT_UP || dir == Direction.LEFT_DOWN ||
             dir == Direction.RIGHT_UP || dir == Direction.RIGHT_DOWN);

        final double cost;
        if (!terrain.isMovable()) {
            cost = Integer.MAX_VALUE;
        } else if (!diagonal) {
            cost = 10.0 * terrain.getCost();
        } else {
            cost = 10.0 * terrain.getCost() * SQRT_2;
        }

        int turns = 0;
        while (cost > timeLeft) {
            timeLeft += (double) spd;
            turns++;
        }

        timeLeft -= cost;
        return turns;
    }

    /**
     * Return debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("(%d/%d/%d/%d)@[%d,%d]",
                             str, dex, pcp, spd, x, y);
    }
}
