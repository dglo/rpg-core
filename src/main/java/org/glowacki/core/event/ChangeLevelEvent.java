package org.glowacki.core.event;

import org.glowacki.core.ICharacter;
import org.glowacki.core.ILevel;

/**
 * Change level event
 */
public class ChangeLevelEvent
    extends MoveEvent
{
    private ILevel oldLevel;
    private ILevel newLevel;

    /**
     * Change levels
     *
     * @param eChar character being moved
     * @param oldLevel old level
     * @param newLevel new level
     * @param fromX X coordinate on old level
     * @param fromY Y coordinate on old level
     * @param toX X coordinate on new level
     * @param toY Y coordinate on new level
     */
    public ChangeLevelEvent(ICharacter eChar, ILevel oldLevel, int fromX,
                            int fromY, ILevel newLevel, int toX, int toY)
    {
        super(Type.CHANGE_LEVEL, eChar, fromX, fromY, toX, toY);

        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /**
     * Get the level being moved from
     *
     * @return old level
     */
    public ILevel getFromLevel()
    {
        return oldLevel;
    }

    /**
     * Get the level being moved to
     *
     * @return new level
     */
    public ILevel getToLevel()
    {
        return newLevel;
    }

    /**
     * Return a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("ChgLvl[%s %s:%d,%d->%s:%d,%d]",
                             getCharacter().getName(), oldLevel.getName(),
                             getFromX(), getFromY(), newLevel.getName(),
                             getToX(), getToY());
    }
}
