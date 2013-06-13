package org.glowacki.core.event;

/**
 * Base event
 */
public abstract class CoreEvent
    implements Comparable, IEvent
{
    /** All possible event types */
    public enum Type {
        /** Character hit event */
        ATTACK_HIT_EVENT,
        /** Character killed event */
        ATTACK_KILLED_EVENT,
        /** Missed attack event */
        ATTACK_MISSED_EVENT,
        /** Parried attack event */
        ATTACK_PARRIED_EVENT,
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
     * Perform basic event comparison
     *
     * @param obj object being compared
     *
     * @return the usual comparison values
     */
    public int compareBasic(Object obj)
    {
        if (obj == null) {
            return 1;
        }

        if (!(obj instanceof CoreEvent)) {
            return getClass().getName().compareTo(obj.getClass().getName());
        }

        return type.compareTo(((CoreEvent) obj).type);
    }

    /**
     * Return <tt>true</tt> if the objects are equal
     *
     * @param obj object being compared
     *
     * @return <tt>true</tt> if objects are equal
     */
    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
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

    /**
     * Return a hash code representing this event
     *
     * @return hash code
     */
    public int hashCode()
    {
        return type.ordinal();
    }
}
