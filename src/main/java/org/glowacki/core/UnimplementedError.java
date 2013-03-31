package org.glowacki.core;

/**
 * Marker for unimplemented methods.
 */
public class UnimplementedError
    extends Error
{
    /**
     * Create an unimplemented error.
     */
    public UnimplementedError()
    {
        super("Unimplemented");
    }

    /**
     * Create an unimplemented error.
     *
     * @param msg error message
     */
    public UnimplementedError(String msg)
    {
        super(msg);
    }
}
