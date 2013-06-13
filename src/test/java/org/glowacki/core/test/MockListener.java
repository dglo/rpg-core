package org.glowacki.core.test;

import java.util.ArrayList;

import org.glowacki.core.event.CoreEvent;
import org.glowacki.core.event.EventListener;

public class MockListener
    implements EventListener
{
    private String name;
    private ArrayList<CoreEvent> expected =
        new ArrayList<CoreEvent>();

    public MockListener(String name)
    {
        this.name = name;
    }

    public void addExpectedEvent(CoreEvent evt)
    {
        expected.add(evt);
    }

    public void send(CoreEvent evt)
    {
        if (expected.size() == 0) {
            throw new Error(name + " got unexpected event " + evt);
        }

        CoreEvent exp = expected.remove(0);

        if (!exp.equals(evt)) {
            throw new Error(name + " expected " + exp + ", not " + evt);
        }
    }
}
