package org.glowacki.core.test;

import org.glowacki.core.util.IRandom;

public class MockRandom
    implements IRandom
{
    private static final boolean DEBUG = false;
    private static final int SIZE_INC = 16;

    private int[] queue;
    private int totVals;;
    private int nextVal;

    public MockRandom add(int val)
    {
        if (queue == null) {
            queue = new int[SIZE_INC];
        } else if (queue.length <= totVals) {
            if (nextVal > SIZE_INC / 2) {
                // more than half of the array is empty; don't grow it
                System.arraycopy(queue, nextVal, queue, 0,
                                 (totVals - nextVal));
            } else {
                int[] tmpQueue = new int[queue.length + SIZE_INC];
                System.arraycopy(queue, nextVal, tmpQueue, 0,
                                 (totVals - nextVal));
                queue = tmpQueue;
            }

            totVals -= nextVal;
            nextVal = 0;
        }

        queue[totVals++] = val;

        return this;
    }

    public boolean hasData()
    {
        //System.out.format("%d values left\n", totVals - nextVal);
        return nextVal < totVals;
    }

    public boolean nextBoolean()
    {
        return nextInt(2) == 1;
    }

    public int nextInt()
    {
        if (nextVal >= totVals) {
            throw new Error("No values left");
        }

//try{throw new Error(String.format("RND->%d", queue[nextVal]));}catch(Error e){e.printStackTrace(System.out);}
        return queue[nextVal++];
    }

    public int nextInt(int maxVal)
    {
        return nextInt() % maxVal;
    }

    public long nextLong()
    {
        throw new Error("No long values");
    }
}
