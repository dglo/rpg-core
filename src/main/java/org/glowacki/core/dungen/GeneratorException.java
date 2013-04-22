package org.glowacki.core.dungen;

/**
 * Indicate a problem with the level generation
 */
public class GeneratorException
    extends Exception
{
    /**
     * Create an exception
     *
     * @param msg error message
     */
    GeneratorException(String msg)
    {
        super(msg);
    }
}
