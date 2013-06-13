package org.glowacki.core.event;

import org.glowacki.core.ICharacter;

/**
 * Move event
 */
public class MoveEvent
    extends CoreEvent
{
    private ICharacter eChar;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    /**
     * Create a move event
     *
     * @param eChar character
     * @param fromX old X coordinate
     * @param fromY old Y coordinate
     * @param toX new X coordinate
     * @param toY new Y coordinate
     */
    public MoveEvent(ICharacter eChar, int fromX, int fromY, int toX, int toY)
    {
        this(Type.MOVE, eChar, fromX, fromY, toX, toY);
    }

    /**
     * Create a move event
     *
     * @param type event type
     * @param eChar character
     * @param fromX old X coordinate
     * @param fromY old Y coordinate
     * @param toX new X coordinate
     * @param toY new Y coordinate
     */
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

    /**
     * Compare this object against another
     *
     * @param obj object being compared
     *
     * @return the usual comparison values
     */
    public int compareTo(Object obj)
    {
        int val = compareBasic(obj);
        if (val == 0) {
            MoveEvent evt = (MoveEvent) obj;

            val = eChar.compareTo(evt.eChar);
            if (val == 0) {
                val = evt.fromX - fromX;
                if (val == 0) {
                    val = evt.fromY - fromY;
                    if (val == 0) {
                        val = evt.toX - toX;
                        if (val == 0) {
                            val = evt.toY - toY;
                        }
                    }
                }
            }
        }

        return val;
    }

    /**
     * Get the character being moved
     *
     * @return character
     */
    public ICharacter getCharacter()
    {
        return eChar;
    }

    /**
     * Get the old X coordinate
     *
     * @return old X coordinate
     */
    public int getFromX()
    {
        return fromX;
    }

    /**
     * Get the old Y coordinate
     *
     * @return old Y coordinate
     */
    public int getFromY()
    {
        return fromY;
    }

    /**
     * Get the new X coordinate
     *
     * @return new X coordinate
     */
    public int getToX()
    {
        return toX;
    }

    /**
     * Get the new Y coordinate
     *
     * @return new Y coordinate
     */
    public int getToY()
    {
        return toY;
    }

    /**
     * Return a debugging string
     *
     * @return debugging string
     */
    public String toString()
    {
        return String.format("Move[%s %d,%d->%d,%d]", eChar.getName(), fromX,
                             fromY, toX, toY);
    }
}
