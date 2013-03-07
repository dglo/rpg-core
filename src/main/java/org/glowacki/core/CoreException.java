package org.glowacki.core;

public class CoreException
    extends Exception
{
    public CoreException(String msg)
    {
        super(msg);
    }

    public CoreException(String msg, Throwable thr)
    {
        super(msg, thr);
    }
}
