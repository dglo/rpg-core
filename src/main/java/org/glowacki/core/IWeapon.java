package org.glowacki.core;

import org.glowacki.core.util.IRandom;

/**
 * Weapon methods.
 */
public interface IWeapon
{
    /**
     * Compute the damage dealt by this weapon
     *
     * @param random random number generator
     *
     * @return hit point damage
     */
    int getDamage(IRandom random);
}
