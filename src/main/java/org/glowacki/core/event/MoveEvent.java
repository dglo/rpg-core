package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

public class MoveEvent
    extends CoreEvent
{
    private ICharacter eChar;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public MoveEvent(ICharacter eChar, int fromX, int fromY, int toX, int toY)
    {
        this(Type.MOVE, eChar, fromX, fromY, toX, toY);
    }

    public MoveEvent(Type type, ICharacter eChar, int fromX, int fromY,
                     int toX, int toY)
    {
        super(type);

        this.eChar = eChar;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public ICharacter getCharacter()
    {
        return eChar;
    }

    public int getFromX()
    {
        return fromX;
    }

    public int getFromY()
    {
        return fromY;
    }

    public int getToX()
    {
        return toX;
    }

    public int getToY()
    {
        return toY;
    }

    public String toString()
    {
        return String.format("Move[%s %d,%d->%d,%d]", eChar.getName(), fromX,
                             fromY, toX, toY);
    }
}
