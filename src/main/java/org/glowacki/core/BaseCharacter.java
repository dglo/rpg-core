package org.glowacki.core;

import java.util.ArrayList;
import java.util.List;

import org.glowacki.core.event.AttackHitEvent;
import org.glowacki.core.event.AttackKilledEvent;
import org.glowacki.core.event.AttackMissedEvent;
import org.glowacki.core.event.AttackParriedEvent;
import org.glowacki.core.event.CoreEvent;
import org.glowacki.core.event.EventListener;
import org.glowacki.core.event.MoveEvent;
import org.glowacki.core.util.IRandom;

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

    private int hitPoints;
    private int maxHitPoints;

    private int armorPoints;

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

        maxHitPoints = computeHitPoints();
        hitPoints = maxHitPoints;

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
     * Attack the character
     *
     * @param random random number generator
     * @param ch character to attack
     * @param weapon weapon used for the attack
     */
    public void attack(IRandom random, ICharacter ch, IWeapon weapon)
    {
        final int attackPct = getAttackPercent(weapon);

        // TODO: handle critical hits and failures

        int defendPct = ch.getDefendPercent(weapon);
        if (defendPct > attackPct) {
            defendPct = attackPct;
        }

        final int rnd = random.nextInt(100);

        if (attackPct < rnd) {
            sendEvent(new AttackMissedEvent(this, ch));
        } else if (attackPct - defendPct < rnd) {
            sendEvent(new AttackParriedEvent(this, ch));
        } else {
            ch.takeDamage(random, this, weapon);
        }
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

    /**
     * Compute the maximum number of hit points for this character
     *
     * @return maximum hit points
     */
    private int computeHitPoints()
    {
        return str;
    }

    /**
     * Return <tt>true</tt> if the objects are equal
     *
     * @param obj object being compared
     *
     * @return <tt>true</tt> if objects are equal
     */
    public boolean equals(ICharacter ch)
    {
        return compareTo(ch) == 0;
    }

    /**
     * Get the attack percentage.
     *
     * @param weapon attacker's weapon
     *
     * @return percentage
     */
    public int getAttackPercent(IWeapon weapon)
    {
        int pct = (dex * 100) / 18;
        if (pct < 0) {
            return 0;
        }

        if (pct > 100) {
            return 100;
        }

        return pct;
    }

    /**
     * Get the defence percentage.
     *
     * @param weapon attacker's weapon
     *
     * @return percentage
     */
    public int getDefendPercent(IWeapon weapon)
    {
        int pct = (dex * 100) / 36;
        if (pct < 0) {
            return 0;
        }

        if (pct > 100) {
            return 100;
        }

        return pct;
    }

    /**
     * Get current hit points.
     *
     * @return hit points
     */
    public int getHitPoints()
    {
        return hitPoints;
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
     * Get maximum hit points.
     *
     * @return maximum hit points
     */
    public int getMaxHitPoints()
    {
        return maxHitPoints;
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
     * Return a hash code representing this character
     *
     * @return hash code
     */
    public int hashCode()
    {
        return id;
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
        final int fromX = x;
        final int fromY = y;
        map.moveDirection(this, dir);
        sendEvent(new MoveEvent(this, fromX, fromY, x, y));

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
     * Take damage from an attack
     *
     * @param random random number generator
     * @param ch attacker
     * @param weapon attacker's weapon
     */
    public void takeDamage(IRandom random, ICharacter ch, IWeapon weapon)
    {
        int damage = weapon.getDamage(random) - armorPoints;
        if (damage < 0) {
            damage = 0;
        }

        hitPoints -= damage;
        if (hitPoints > 0) {
            sendEvent(new AttackHitEvent(ch, this, damage));
        } else {
            sendEvent(new AttackKilledEvent(ch, this));
        }
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
