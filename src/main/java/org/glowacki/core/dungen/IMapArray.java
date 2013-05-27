package org.glowacki.core.dungen;

import java.io.PrintStream;

/**
 * A character representation of a dungeon level
 */
public interface IMapArray
{
    /**
     * Add a door
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws GeneratorException if the current terrain is not a wall
     */
    void addDoor(int x, int y)
        throws GeneratorException;

    /**
     * Add a staircase
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param isUp if <tt>true</tt>, add an up staircase.
     *             otherwise add down staircase
     *
     * @throws GeneratorException if the current terrain is not a floor
     */
    void addStaircase(int x, int y, boolean isUp)
        throws GeneratorException;

    /**
     * Get the map as an array of strings
     *
     * @return array of strings depicting the map
     */
    String[] getStrings();

    /**
     * Write the map to System.out
     */
    void show();

    /**
     * Write the map to the output file/device
     *
     * @param out output file/device
     */
    void show(PrintStream out);

    /**
     * Dig a tunnel
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @throws GeneratorException if the current terrain is not tunnelable
     */
    void tunnel(int x, int y)
        throws GeneratorException;
}
