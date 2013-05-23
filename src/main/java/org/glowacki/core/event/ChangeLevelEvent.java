package org.glowacki.core.event;

import org.glowacki.core.ICharacter;
import org.glowacki.core.ILevel;

public class ChangeLevelEvent
    extends MoveEvent
{
    private ILevel oldLevel;
    private ILevel newLevel;

    public ChangeLevelEvent(ICharacter eChar, ILevel oldLevel, int fromX,
                            int fromY, ILevel newLevel, int toX, int toY)
    {
        super(Type.CHANGE_LEVEL, eChar, fromX, fromY, toX, toY);

        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public ILevel getFromLevel()
    {
        return oldLevel;
    }

    public ILevel getToLevel()
    {
        return newLevel;
    }

    public String toString()
    {
        return String.format("ChgLvl[%s %s:%d,%d->%s:%d,%d]",
                             getCharacter().getName(), oldLevel.getName(),
                             getFromX(), getFromY(), newLevel.getName(),
                             getToX(), getToY());
    }
}
