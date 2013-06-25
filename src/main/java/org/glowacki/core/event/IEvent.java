package org.glowacki.core.event;

/**
 * Event
 */
public interface IEvent
{
    /** All possible event types */
    public enum Type {
        /** Character hit event */
        ATTACK_HIT,
        /** Character killed event */
        ATTACK_KILLED,
        /** Missed attack event */
        ATTACK_MISSED,
        /** Parried attack event */
        ATTACK_PARRIED,
        /** Change level event */
        CHANGE_LEVEL,
        /** Move event */
        MOVE,
        /** Change state event */
        STATE,
    };

    /**
     * Get the event type
     *
     * @return event type
     */
    Type getType();
}
