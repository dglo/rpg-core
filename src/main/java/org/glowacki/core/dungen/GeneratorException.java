package org.glowacki.core.dungen;

import org.glowacki.core.CoreException;

/**
 * Indicate a problem with the level generation
 */
public class GeneratorException
    extends CoreException
{
    /**
     * Create an exception
     *
     * @param msg error message
     */
    public GeneratorException(String msg)
    {
        super(msg);
    }
}
