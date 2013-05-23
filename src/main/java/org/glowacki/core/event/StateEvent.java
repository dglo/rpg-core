package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

public class StateEvent
    extends CoreEvent
{
    private ICharacter eChar;
    private ICharacter.State fromState;
    private ICharacter.State toState;

    public StateEvent(ICharacter eChar, ICharacter.State fromState,
                      ICharacter.State toState)
    {
        super(Type.STATE);

        this.eChar = eChar;
        this.fromState = fromState;
        this.toState = toState;
    }

    public ICharacter getCharacter()
    {
        return eChar;
    }

    public ICharacter.State getFromState()
    {
        return fromState;
    }

    public ICharacter.State getToState()
    {
        return toState;
    }

    public String toString()
    {
        return String.format("State[%s %s->%s]", eChar.getName(), fromState,
                             toState);
    }
}
