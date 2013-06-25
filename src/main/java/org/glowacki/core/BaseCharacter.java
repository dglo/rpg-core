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
    private int id;

    private List<EventListener> listeners = new ArrayList<EventListener>();

    private int str;
    private int dex;
    private int pcp;
    private int spd;

    private ILevel level;
    private VisibleMap vmap;

    private int x;
    private int y;

    private double timeLeft;

    private int hitPoints;
    private int maxHitPoints;

    private int armorPoints;

    private IWeapon weapon;

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
     *
     * @throws CoreException if there is a problem
     */
    public void attack(IRandom random, ICharacter ch)
        throws CoreException
    {
        attack(random, ch, weapon);
    }

    /**
     * Attack the character
     *
     * @param random random number generator
     * @param ch character to attack
     * @param weapon weapon used for the attack
     *
     * @throws CoreException if there is a problem
     */
    public void attack(IRandom random, ICharacter ch, IWeapon weapon)
        throws CoreException
    {
        if (weapon == null) {
            throw new CharacterException(getName() +
                                         " does not have a weapon");
        }

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
     * Clear the current level.
     */
    public void clearLevel()
    {
        level = null;
        clearPosition();
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
     * Get character's current level
     *
     * @return level
     */
    public ILevel getLevel()
    {
        return level;
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
     * Get the visible cell array
     *
     * @return array of visible cells
     */
    public boolean[][] getVisible()
    {
        if (level == null) {
            return null;
        }

        if (vmap == null) {
            vmap = new VisibleMap(level.getMap());
        }

        return vmap.getVisible(getX(), getY(), getSightDistance());
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
     * Is this character in a neighboring cell?
     *
     * @param ch character
     *
     * @return <tt>true</tt> if this character is a neighbor
     */
    public boolean isNeighbor(ICharacter ch)
    {
        if (ch.getX() < x - 1 || ch.getX() > x + 1) {
            return false;
        } else if (ch.getY() < y - 1 || ch.getY() > y + 1) {
            return false;
        }

        return true;
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
        if (level == null) {
            throw new CharacterException("Level has not been set");
        }

        Object obj = level.getMap().getOccupant(px, py);
        if (obj == null || !(obj instanceof ICharacter)) {
            return null;
        }

        return (ICharacter) obj;
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
     * Move in the specified direction.
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
        if (level == null) {
            throw new CharacterException("Level has not been set");
        }

        IMap map = level.getMap();

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
     * Set character's current level
     *
     * @param lvl level
     */
    public void setLevel(ILevel lvl)
        throws CoreException
    {
        level = lvl;

        vmap = null;
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
        throws CoreException
    {
        int damage = weapon.getDamage(random) - armorPoints;
        if (damage < 0) {
            damage = 0;
        }

        hitPoints -= damage;
        if (hitPoints > 0) {
            sendEvent(new AttackHitEvent(ch, this, damage));
        } else {
            CoreException delayed = null;
            try {
                level.exit(this);
            } catch (CoreException cex) {
                delayed = cex;
            }

            clearLevel();
            sendEvent(new AttackKilledEvent(ch, this));

            if (delayed != null) {
                throw delayed;
            }
        }
    }

    /**
     * Wield a weapon.
     *
     * @param weapon weapon to wield
     */
    public void wield(IWeapon weapon)
    {
        this.weapon = weapon;
    }

    /**
     * Return debugging string.
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("(%d/%d/%d/%d)hp%d@[%d,%d]",
                             str, dex, pcp, spd, hitPoints, x, y);
    }
}
