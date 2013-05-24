package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * State change event
 */
public class StateEvent
    extends CoreEvent
{
    private ICharacter eChar;
    private ICharacter.State fromState;
    private ICharacter.State toState;

    /**
     * Create a state event
     *
     * @param eChar character
     * @param fromState old state
     * @param toState new state
     */
    public StateEvent(ICharacter eChar, ICharacter.State fromState,
                      ICharacter.State toState)
    {
        super(Type.STATE);

        this.eChar = eChar;
        this.fromState = fromState;
        this.toState = toState;
    }

    /**
     * Get the character whose state changed
     *
     * @return character
     */
    public ICharacter getCharacter()
    {
        return eChar;
    }

    /**
     * Get the old state
     *
     * @return old state
     */
    public ICharacter.State getFromState()
    {
        return fromState;
    }

    /**
     * Get the new state
     *
     * @return new state
     */
    public ICharacter.State getToState()
    {
        return toState;
    }

    /**
     * Return a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("State[%s %s->%s]", eChar.getName(), fromState,
                             toState);
    }
}
