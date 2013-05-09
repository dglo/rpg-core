package org.glowacki.core;

/**
 * A map object.
 */
public interface IMapObject
    extends IMapPoint
{
    /**
     * Clear the object's position.
     */
    void clearPosition();

    /**
     * Return object's name.
     *
     * @return name
     */
    String getName();

    /**
     * Set the object's position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    void setPosition(int x, int y);
}
