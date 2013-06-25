package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public class AttackMissedEvent
    extends AttackEvent
{
    /**
     * Create a missed attack event
     *
     * @param attacker attacker
     * @param defender defender
     */
    public AttackMissedEvent(ICharacter attacker, ICharacter defender)
    {
        super(Type.ATTACK_MISSED, attacker, defender);
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
        return super.compareBasic(evt);
    }
}
