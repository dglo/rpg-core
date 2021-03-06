package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public class AttackKilledEvent
    extends AttackEvent
{
    /**
     * Create a killed attack event
     *
     * @param attacker attacker
     * @param defender defender
     */
    public AttackKilledEvent(ICharacter attacker, ICharacter defender)
    {
        super(Type.ATTACK_KILLED, attacker, defender);
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
