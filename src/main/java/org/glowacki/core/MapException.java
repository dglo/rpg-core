package org.glowacki.core;

/**
 * Map-related exception
 */
public class MapException
    extends CoreException
{
    /**
     * Create a map exception.
     */
    public MapException()
    {
        super();
    }

    /**
     * Create a map exception.
     *
     * @param msg error message
     */
    public MapException(String msg)
    {
        super(msg);
    }
}
