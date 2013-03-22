package org.glowacki.core;

/**
 * Generic exception for this package.
 */
public class CoreException
    extends Exception
{
    /**
     * Create a core exception.
     */
    public CoreException()
    {
        super();
    }

    /**
     * Create a core exception.
     *
     * @param msg error message
     */
    public CoreException(String msg)
    {
        super(msg);
    }

    /**
     * Create a core exception.
     *
     * @param msg error message
     * @param thr previous exception
     */
    public CoreException(String msg, Throwable thr)
    {
        super(msg, thr);
    }
}
