package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public abstract class AttackEvent
    extends CoreEvent
{
    private ICharacter attacker;
    private ICharacter defender;

    /**
     * Create an attack event
     *
     * @param type event type
     * @param attacker attacker
     * @param defender defender
     */
    public AttackEvent(Type type, ICharacter attacker, ICharacter defender)
    {
        super(type);

        this.attacker = attacker;
        this.defender = defender;
    }

    /**
     * Perform basic event comparison
     *
     * @param obj object being compared
     *
     * @return the usual comparison values
     */
    public int compareBasic(Object obj)
    {
        int val = super.compareBasic(obj);
        if (val == 0) {
            AttackEvent evt = (AttackEvent) obj;

            val = attacker.compareTo(evt.attacker);
            if (val == 0) {
                val = defender.compareTo(evt.defender);
            }
        }

        return val;
    }

    /**
     * Get the attacker
     *
     * @return character
     */
    public ICharacter getAttacker()
    {
        return attacker;
    }

    /**
     * Get the defender
     *
     * @return character
     */
    public ICharacter getDefender()
    {
        return defender;
    }

    /**
     * Return a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        final String name;
        switch (getType()) {
        case ATTACK_HIT_EVENT:
            name = "AttackHit";
            break;
        case ATTACK_KILLED_EVENT:
            name = "AttackKilled";
            break;
        case ATTACK_MISSED_EVENT:
            name = "AttackMissed";
            break;
        case ATTACK_PARRIED_EVENT:
            name = "AttackParried";
            break;
        default:
            name = "??" + getType() + "??";
            break;
        }

        return String.format("%s[%s->%s]", name, attacker.getName(),
                             defender.getName());
    }
}
