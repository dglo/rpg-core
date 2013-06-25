package org.glowacki.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.glowacki.core.event.AttackHitEvent;
import org.glowacki.core.event.AttackKilledEvent;
import org.glowacki.core.event.AttackMissedEvent;
import org.glowacki.core.event.AttackParriedEvent;
import org.glowacki.core.event.EventListener;
import org.glowacki.core.test.MapBuilder;
import org.glowacki.core.test.MockCharacter;
import org.glowacki.core.test.MockLevel;
import org.glowacki.core.test.MockListener;
import org.glowacki.core.test.MockMap;
import org.glowacki.core.test.MockRandom;
import org.glowacki.core.test.MockWeapon;

class MyCharacter
    extends BaseCharacter
{
    private String name;
    private boolean player;

    MyCharacter(String name, int str, int dex, int pcp, int spd)
    {
        this(name, str, dex, pcp, spd, false);
    }

    MyCharacter(String name, int str, int dex, int pcp, int spd,
                boolean player)
    {
        super(str, dex, pcp, spd);

        this.name = name;
        this.player = player;
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

    public String getName()
    {
        return name;
    }

    public boolean[][] getSeenArray()
    {
        throw new UnimplementedError();
    }

    public boolean hasPath()
    {
        return false;
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
     * List all characters which can be seen by this character
     *
     * @return iterable list of visible characters
     */
    public Iterable<ICharacter> listVisibleCharacters()
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

    public void takeTurn()
    {
        throw new UnimplementedError();
    }

    public int useStaircase()
    {
        throw new UnimplementedError();
    }
}

public class BaseCharacterTest
    extends TestCase
{
    public BaseCharacterTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(BaseCharacterTest.class);
    }

    public void testCreate()
        throws CoreException
    {
        MyCharacter ch = new MyCharacter("a", 1, 2, 3, 4);
        assertEquals("Bad initial X", -1, ch.getX());
        assertEquals("Bad initial Y", -1, ch.getY());

        final int x = 2;
        final int y = 2;

        ch.setPosition(x, y);
        assertEquals("Bad X", x, ch.getX());
        assertEquals("Bad Y", y, ch.getY());
    }

    public void testBadMove00()
        throws CoreException
    {
        MyCharacter ch = new MyCharacter("b", 1, 2, 3, 4);

        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        Direction dir = Direction.LEFT;
        do {
            int x = 0;
            int y = 0;

            ch.setPosition(x, y);
            ch.setLevel(lvl);

            int expTurns = -1;
            switch (dir) {
            case LEFT:
                break;
            case LEFT_UP:
                break;
            case UP:
                break;
            case RIGHT_UP:
                break;
            case RIGHT:
                x++;
                expTurns = 3;
                break;
            case RIGHT_DOWN:
                x++;
                y++;
                expTurns = 4;
                break;
            case DOWN:
                y++;
                expTurns = 2;
                break;
            case LEFT_DOWN:
                break;
            }

            int turns;
            try {
                turns = ch.move(dir);
            } catch (MapException me) {
                turns = -1;
            }

            assertEquals("Bad " + dir + " X", x, ch.getX());
            assertEquals("Bad " + dir + " Y", y, ch.getY());
            assertEquals("Bad " + dir + " number of turns", expTurns, turns);
            dir = dir.next();
        } while (dir != Direction.LEFT);
    }

    public void testBadMove22()
        throws CoreException
    {
        MyCharacter ch = new MyCharacter("c", 1, 2, 3, 4);

        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        Direction dir = Direction.LEFT;
        do {
            int x = 3;
            int y = 3;

            ch.setPosition(x, y);
            ch.setLevel(lvl);

            int expTurns = -1;
            switch (dir) {
            case LEFT:
                x--;
                expTurns = 3;
                break;
            case LEFT_UP:
                x--;
                y--;
                expTurns = 4;
                break;
            case UP:
                y--;
                expTurns = 2;
                break;
            case RIGHT_UP:
                break;
            case RIGHT:
                break;
            case RIGHT_DOWN:
                break;
            case DOWN:
                break;
            case LEFT_DOWN:
                break;
            }

            int turns;
            try {
                turns = ch.move(dir);
            } catch (MapException me) {
                turns = -1;
            }

            assertEquals("Bad " + dir + " X", x, ch.getX());
            assertEquals("Bad " + dir + " Y", y, ch.getY());
            assertEquals("Bad " + dir + " number of turns", expTurns, turns);
            dir = dir.next();
        } while (dir != Direction.LEFT);
    }

    public void testMove()
        throws CoreException
    {
        MyCharacter ch = new MyCharacter("d", 1, 2, 3, 4);

        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        Direction dir = Direction.LEFT;
        do {
            int x = 2;
            int y = 2;

            ch.setPosition(x, y);
            ch.setLevel(lvl);

            switch (dir) {
            case LEFT:
                x--;
                break;
            case LEFT_UP:
                x--;
                y--;
                break;
            case UP:
                y--;
                break;
            case RIGHT_UP:
                x++;
                y--;
                break;
            case RIGHT:
                x++;
                break;
            case RIGHT_DOWN:
                x++;
                y++;
                break;
            case DOWN:
                y++;
                break;
            case LEFT_DOWN:
                x--;
                y++;
                break;
            }

            ch.move(dir);
            assertEquals("Bad X", x, ch.getX());
            assertEquals("Bad Y", y, ch.getY());

            dir = dir.next();
        } while (dir != Direction.LEFT);
    }

    public void testAttackFail()
        throws CoreException
    {
        MyCharacter attacker = new MyCharacter("att", 11, 10, 3, 4);

        MockListener alistener = new MockListener("AListener");
        attacker.addEventListener(alistener);

        MyCharacter defender = new MyCharacter("def", 11, 9, 3, 4);

        MockListener dlistener = new MockListener("DListener");
        defender.addEventListener(dlistener);

        MockRandom random = new MockRandom();

        MockWeapon weapon = new MockWeapon();

        random.add(99);
        alistener.addExpectedEvent(new AttackMissedEvent(attacker, defender));

        attacker.attack(random, defender, weapon);
    }

    public void testAttackParried()
        throws CoreException
    {
        MyCharacter attacker = new MyCharacter("att", 11, 10, 3, 4);

        MockListener alistener = new MockListener("AListener");
        attacker.addEventListener(alistener);

        MyCharacter defender = new MyCharacter("def", 11, 9, 3, 4);

        MockListener dlistener = new MockListener("DListener");
        defender.addEventListener(dlistener);

        MockRandom random = new MockRandom();

        MockWeapon weapon = new MockWeapon();

        random.add(31);
        alistener.addExpectedEvent(new AttackParriedEvent(attacker, defender));

        attacker.attack(random, defender, weapon);
    }

    public void testAttackHit()
        throws CoreException
    {
        MyCharacter attacker = new MyCharacter("att", 11, 10, 3, 4);

        MockListener alistener = new MockListener("AListener");
        attacker.addEventListener(alistener);

        MyCharacter defender = new MyCharacter("def", 11, 9, 3, 4);

        MockListener dlistener = new MockListener("DListener");
        defender.addEventListener(dlistener);

        MockRandom random = new MockRandom();

        MockWeapon weapon = new MockWeapon();

        final int damage = 5;

        random.add(1);
        weapon.setDamage(damage);
        dlistener.addExpectedEvent(new AttackHitEvent(attacker, defender,
                                                      damage));

        attacker.attack(random, defender, weapon);
    }

    public void testAttackKilled()
        throws CoreException
    {
        MyCharacter attacker = new MyCharacter("att", 11, 10, 3, 4);

        MockListener alistener = new MockListener("AListener");
        attacker.addEventListener(alistener);

        MyCharacter defender = new MyCharacter("def", 11, 9, 3, 4);

        MockListener dlistener = new MockListener("DListener");
        defender.addEventListener(dlistener);

        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        defender.setLevel(lvl);
        defender.setPosition(1, 1);

        MockRandom random = new MockRandom();

        MockWeapon weapon = new MockWeapon();

        random.add(1);
        weapon.setDamage(defender.getMaxHitPoints() * 2);
        dlistener.addExpectedEvent(new AttackKilledEvent(attacker, defender));

        attacker.attack(random, defender, weapon);
    }

    public void testAttackPercent()
    {
        MockWeapon weapon = new MockWeapon();

        for (int dex = -1; dex < 25; dex += 25) {
            MyCharacter guy = new MyCharacter("guy", 11, dex, 3, 4);

            int expPct;
            if (dex < 0) {
                expPct = 0;
            } else if (dex > 18) {
                expPct = 100;
            } else {
                expPct = dex;
            }

            assertEquals("Bad attack percent for dex " + dex,
                         expPct, guy.getAttackPercent(weapon));
        }
    }

    public void testDefendPercent()
    {
        MockWeapon weapon = new MockWeapon();

        for (int dex = -1; dex < 40; dex += 40) {
            MyCharacter guy = new MyCharacter("guy", 11, dex, 3, 4);

            int expPct;
            if (dex < 0) {
                expPct = 0;
            } else if (dex > 36) {
                expPct = 100;
            } else {
                expPct = dex;
            }

            assertEquals("Bad defend percent for dex " + dex,
                         expPct, guy.getDefendPercent(weapon));
        }
    }

    public void testLevel()
        throws CoreException
    {
        MyCharacter ch = new MyCharacter("l", 1, 2, 3, 4);
        assertNull("Level should be null", ch.getLevel());

        MockMap map = new MockMap(3, 3);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        ch.setLevel(lvl);
        assertNotNull("Level should not be null", ch.getLevel());
        assertEquals("Bad level", lvl, ch.getLevel());

        ch.clearLevel();
        assertNull("Level should be null", ch.getLevel());
    }

    public void testCompareTo()
    {
        MyCharacter ch = new MyCharacter("one", 1, 2, 3, 4);
        assertEquals("Bad compare result", 1, ch.compareTo(null));
        assertEquals("Bad compare result", 0, ch.compareTo(ch));
        assertFalse("Bad equals result", ch.equals(null));
        assertTrue("Bad equals result", ch.equals(ch));

        MyCharacter ch2 = new MyCharacter("two", 2, 3, 4, 5);
        assertEquals("Bad compare result", 1, ch.compareTo(ch2));
        assertEquals("Bad compare result", -1, ch2.compareTo(ch));
        assertFalse("Bad equals result", ch.equals(ch2));
        assertFalse("Bad equals result", ch2.equals(ch));
    }

    public void testVisible()
        throws CoreException
    {
        final int pcp = 3;

        MyCharacter ch = new MyCharacter("l", 1, 2, pcp, 4);
        assertNull("Level should be null", ch.getLevel());
        assertNull("Visible map should be null", ch.getVisible());

        final int maxX = 3;
        final int maxY = 5;

        MockMap map = new MockMap(maxX, maxY);
        map.setTerrain(Terrain.FLOOR);

        MockLevel lvl = new MockLevel("move", map);

        ch.setLevel(lvl);
        ch.setPosition(1, 1);

        boolean[][] visible = ch.getVisible();
        assertNotNull("Visible map is null", visible);

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                assertEquals(String.format("Bad visibility for %d,%d", x, y),
                             visible[x][y], ch.isVisible(x, y));
            }
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
