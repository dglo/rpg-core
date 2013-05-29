package org.glowacki.core.test;

import java.nio.ByteBuffer;

import org.glowacki.core.util.IRandom;

public class MockRandom
    implements IRandom
{
    private static final boolean DEBUG = false;
    private static final int SIZE_INC = 16;

    private int[] queue;
    private int totVals;;
    private int nextVal;

    private ByteBuffer cvtBuf;

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

    public MockRandom addDouble(double val)
    {
        if (cvtBuf == null) {
            cvtBuf = ByteBuffer.allocate(8);
        }

        cvtBuf.clear();
        cvtBuf.putDouble(val);
        cvtBuf.flip();

        add(cvtBuf.getInt());
        add(cvtBuf.getInt());

        return this;
    }

    public boolean hasData()
    {
        final int rem = remaining();
        //System.out.format("%d values left\n", rem);
        return rem > 0;
    }

    public boolean nextBoolean()
    {
        return nextInt(2) == 1;
    }

    public double nextDouble()
    {
        if (cvtBuf == null) {
            cvtBuf = ByteBuffer.allocate(8);
        }

        cvtBuf.clear();
        cvtBuf.putInt(nextInt());
        cvtBuf.putInt(nextInt());
        cvtBuf.flip();

        return cvtBuf.getDouble();
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

    public int remaining()
    {
        return totVals - nextVal;
    }
}
