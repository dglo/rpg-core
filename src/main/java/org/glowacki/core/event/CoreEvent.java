package org.glowacki.core.event;

/**
 * Base event
 */
public abstract class CoreEvent
{
    /** All possible event types */
    public enum Type {
        /** Change level event */
        CHANGE_LEVEL,
        /** Move event */
        MOVE,
        /** Change state event */
        STATE,
    };

    private Type type;

    /**
     * Base event
     *
     * @param type event type
     */
    CoreEvent(Type type)
    {
        this.type = type;
    }

    /**
     * Get the event type
     *
     * @return event type
     */
    public Type getType()
    {
        return type;
    }
}
