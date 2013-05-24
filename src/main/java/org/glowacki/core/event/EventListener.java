package org.glowacki.core.event;

/**
 * Listen for events
 */
public interface EventListener
{
    /**
     * Receive an event
     *
     * @param evt event
     */
    void send(CoreEvent evt);
}
