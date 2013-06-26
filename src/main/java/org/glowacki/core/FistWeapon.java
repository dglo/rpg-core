package org.glowacki.core;

import org.glowacki.core.util.IRandom;

/**
 * Player's fist.
 */
public class FistWeapon
    implements IWeapon
{
    /**
     * Compute the damage dealt by this weapon
     *
     * @param random random number generator
     *
     * @return hit point damage
     */
    public int getDamage(IRandom random)
    {
        return random.nextInt(4);
    }
}
