package org.glowacki.core.astar;

import org.glowacki.core.MapException;

/**
 * Indicate a problem with the path calculation
 */
public class PathException
    extends MapException
{
    /**
     * Create a path exception
     *
     * @param msg error message
     */
    public PathException(String msg)
    {
        super(msg);
    }
}
