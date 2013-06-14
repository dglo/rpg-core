package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public class AttackParriedEvent
    extends AttackEvent
{
    /**
     * Create a parried attack event
     *
     * @param attacker attacker
     * @param defender defender
     */
    public AttackParriedEvent(ICharacter attacker, ICharacter defender)
    {
        super(Type.ATTACK_PARRIED_EVENT, attacker, defender);
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
