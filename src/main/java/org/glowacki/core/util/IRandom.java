package org.glowacki.core.util;

/**
 * Random number generator interface
 */
public interface IRandom
{
    /**
     * Generate the next random boolean value
     *
     * @return next random boolean value
     */
    boolean nextBoolean();

    /**
     * Generate the next random double value
     *
     * @return next random double value
     */
    double nextDouble();

    /**
     * Generate the next random value
     *
     * @return next random value
     */
    int nextInt();

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.
     *
     * @param n the bound on the random number to be returned.  Must be
     *        positive.
     *
     * @return the next pseudorandom, uniformly distributed {@code int}
     *         value between {@code 0} (inclusive) and {@code n} (exclusive)
     *         from this random number generator's sequence
     *
     * @exception IllegalArgumentException if n is not positive
     */
    int nextInt(int n)
        throws IllegalArgumentException;

    /**
     * Generate the next random long value
     *
     * @return next random long value
     */
    long nextLong();
}
