package org.glowacki.core.test;

import org.glowacki.core.IWeapon;
import org.glowacki.core.util.IRandom;

public class MockWeapon
    implements IWeapon
{
    private int damage = Integer.MIN_VALUE;

    public int getDamage(IRandom random)
    {
        if (damage < 0) {
            throw new Error("Weapon damage has not been set");
        }

        return damage;
    }

    public void setDamage(int damage)
    {
        if (damage < 0) {
            throw new Error("Weapon damage cannot be negative");
        }

        this.damage = damage;
    }
}
