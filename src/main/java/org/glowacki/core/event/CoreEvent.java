package org.glowacki.core.event;

/**
 * Base event
 */
public abstract class CoreEvent
    implements Comparable<IEvent>, IEvent
{
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
    public int compareBasic(IEvent evt)
    {
        if (evt == null) {
            return 1;
        }

        return type.compareTo(evt.getType());
    }

    /**
     * Return <tt>true</tt> if the objects are equal
     *
     * @param obj object being compared
     *
     * @return <tt>true</tt> if objects are equal
     */
    public boolean equals(IEvent evt)
    {
        return compareTo(evt) == 0;
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
