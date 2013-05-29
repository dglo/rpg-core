package org.glowacki.core.test;

import org.junit.*;
import static org.junit.Assert.*;

public class MockRandomTest
{
    @Test
    public void testSimple()
    {
        int[] sequence = new int[] {
            1, 3, 5, 7, 9, 8, 6, 4, 2, 0, 4, 5, 6, 7, 10, 9, 8, 7, 6, 5, 4,
        };

        MockRandom random = new MockRandom();
        for (int s = 0; s < sequence.length; s++) {
            random.add(sequence[s]);
        }

        for (int s = 0; s < sequence.length; s++) {
            assertEquals("Bad number #" + s, sequence[s], random.nextInt());
        }
    }

    @Test
    public void testReads()
    {
        int[] sequence = new int[] {
            1, 3, 5, 7, 9, 8, 6, 4, 2, 0, 4, 5, 6, 7, 10, 9, 8, 7, 6, 5, 4,
        };

        for (int f = 0; f < sequence.length; f++) {
            int n = 0;

            MockRandom random = new MockRandom();
            for (int s = 0; s < sequence.length; s++) {
                random.add(sequence[s]);
                if (s == f) {
                    while (n < s) {
                        assertEquals("Bad number #" + n,
                                     sequence[n], random.nextInt());
                        n++;
                    }
                }
            }

            for (int s = n; s < sequence.length; s++) {
                assertEquals("Bad number #" + s, sequence[s], random.nextInt());
            }
        }
    }

    @Test
    public void testDouble()
    {
        MockRandom random = new MockRandom();
        random.addDouble(1.0);
        assertEquals("Bad double value", 1.0, random.nextDouble());
        random.addDouble(Double.MIN_VALUE);
        assertEquals("Bad double value",
                     Double.MIN_VALUE, random.nextDouble());
        random.addDouble(Double.MAX_VALUE);
        assertEquals("Bad double value",
                     Double.MAX_VALUE, random.nextDouble());
    }
}
