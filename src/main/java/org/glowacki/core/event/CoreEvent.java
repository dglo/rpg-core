package org.glowacki.core.event;

public abstract class CoreEvent
{
    public enum Type {
        CHANGE_LEVEL,
        CREATE_MONSTER,
        CREATE_PLAYER,
        MOVE,
        STATE,
    };

    private Type type;

    CoreEvent(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }
}
