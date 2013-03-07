package org.glowacki.core;

public class UnimplementedError
    extends Error
{
    public UnimplementedError()
    {
        super();
    }

    public UnimplementedError(String msg)
    {
        super(msg);
    }

    public UnimplementedError(String msg, Throwable thr)
    {
        super(msg, thr);
    }
}
