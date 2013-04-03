package org.glowacki.core.astar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class BaseNodeTest
    extends TestCase
{
    public BaseNodeTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(BaseNodeTest.class);
    }

    public void testSimple()
    {
        BaseNode parent = new BaseNode(15, 15);

        for (int i = 0; i < 3; i++) {
            final int x = 5 * i;
            final int y = 3 * i;

            BaseNode node = new BaseNode(x, y);

            boolean isStart = (i == 0);
            if (isStart) {
                node.setStart();
            }
            boolean isEnd = (i == 2);
            if (isEnd) {
                node.setEnd();
            }

            node.setParent(parent);

            assertEquals("Bad X", x, node.getX());
            assertEquals("Bad Y", y, node.getY());
            assertEquals("Bad start", isStart, node.isStart());
            assertEquals("Bad end", isEnd, node.isEnd());
            assertEquals("Bad parent", parent, node.getParent());
        }
    }

    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
