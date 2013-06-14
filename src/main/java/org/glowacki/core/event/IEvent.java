package org.glowacki.core.event;

/**
 * Event
 */
public interface IEvent
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

    /**
     * Get the event type
     *
     * @return event type
     */
    Type getType();
}
