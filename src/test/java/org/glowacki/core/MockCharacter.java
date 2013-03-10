package org.glowacki.core;

public class MockCharacter
    extends Character
{
    /**
     * Create a character.
     *
     * @param name name
     * @param str strength
     * @param dex dexterity
     * @param spd speed
     */
    public MockCharacter(String name, int str, int dex, int spd)
    {
        super(name, str, dex, spd);
    }

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        throw new UnimplementedError();
    }
}
