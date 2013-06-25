package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public class AttackHitEvent
    extends AttackEvent
{
    private int damage;

    /**
     * Create a hit attack event
     *
     * @param attacker attacker
     * @param defender defender
     * @param damage hit point damage
     */
    public AttackHitEvent(ICharacter attacker, ICharacter defender, int damage)
    {
        super(Type.ATTACK_HIT, attacker, defender);

        this.damage = damage;
    }

    /**
     * Compare this object against another
     *
     * @param obj object being compared
     *
     * @return the usual comparison values
     */
    public int compareTo(IEvent evt)
    {
        int val = super.compareBasic(evt);
        if (val == 0) {
            val = ((AttackHitEvent) evt).damage - damage;
        }

        return val;
    }

    /**
     * Get the number of hit points
     *
     * @return hit point damage
     */
    public int getDamage()
    {
        return damage;
    }
}
