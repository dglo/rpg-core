package org.glowacki.core.dungen;

import org.glowacki.core.test.MockRandom;

import org.junit.*;
import static org.junit.Assert.*;

public class SimpleGeneratorTest
{
    @Test
    public void testBuildRooms()
        throws GeneratorException
    {
        MockRandom random = new MockRandom();
        for (int i = 0; i < 16; i++) {
            random.add(i);
        }

        final int cellWidth = 20;
        final int cellHeight = 20;
        final int gridWidth = 2;
        final int gridHeight = 2;

        RoomPlus[] rooms =
            SimpleGenerator.buildRooms(random, cellWidth, cellHeight,
                                       gridWidth, gridHeight);
        assertNotNull("Returned null array", rooms);
        assertEquals("Unexpected number of rooms",
                     gridWidth * gridHeight, rooms.length);

        int[][] expRooms = new int[][] {
            { 2, 3, 5, 6 },
            { 10, 2, 9, 5 },
            { 0, 10, 8, 9 },
            { 12, 11, 7, 8 },
        };
        assertEquals("Unexpected expected data array length",
                     expRooms.length, rooms.length);

        for (int i = 0; i < expRooms.length; i++) {
            assertEquals("Unexpected room #" + i + " X coordinate",
                         expRooms[i][0], rooms[i].getX());
            assertEquals("Unexpected room #" + i + " Y coordinate",
                         expRooms[i][1], rooms[i].getY());
            assertEquals("Unexpected room #" + i + " width",
                         expRooms[i][2], rooms[i].getWidth());
            assertEquals("Unexpected room #" + i + " height",
                         expRooms[i][3], rooms[i].getHeight());
        }
    }

    @Test
    public void testCreateRoomsA()
        throws GeneratorException
    {
        MockRandom random = new MockRandom();
        for (int i = 0; i < 100; i++) {
            random.add(i);
        }

        final int cellWidth = 20;
        final int cellHeight = 20;
        final int gridWidth = 3;
        final int gridHeight = 3;

        IMapArray map =
            SimpleGenerator.createRooms(random, cellWidth, cellHeight,
                                        gridWidth, gridHeight, true, true);

        assertNotNull("Returned null map", map);

        String[] mapStr = map.getStrings();

        String[] expMap = new String[] {
            "      ----       ",
            "---- #+..|  -----",
            "|..+##|..|  |...|",
            "|..|  |..|  |..<|",
            "-+--  -+--  --+--",
            " #     ##     #  ",
            " ##   --+-    ## ",
            "--+- #+..+##---+-",
            "|..+##|..| #|...|",
            "|..|  |..| #+...|",
            "----  -+--  --+--",
            "       ##     #  ",
            "      --+-    ## ",
            "---- #+..|  ---+-",
            "|..+##|..|  |...|",
            "|>.|  |..+##+...|",
            "----  ----  -----",
        };

        assertEquals("Unexpected map length", expMap.length, mapStr.length);

        for (int y = 0; y < expMap.length; y++) {
            assertEquals("Unexpected map[" + y + "] width",
                         expMap[y].length(), mapStr[y].length());
            for (int x = 0; x < expMap[y].length(); x++) {
                assertEquals(String.format("Unexpected map[%d][%d]", y, x),
                             expMap[y].charAt(x), mapStr[y].charAt(x));
            }
        }
    }

    @Test
    public void testCreateRoomsB()
        throws GeneratorException
    {
        MockRandom random = new MockRandom();
        for (int i = 0; i < 200; i++) {
            random.add(200 - i);
        }

        final int cellWidth = 40;
        final int cellHeight = 40;
        final int gridWidth = 4;
        final int gridHeight = 4;

        IMapArray map =
            SimpleGenerator.createRooms(random, cellWidth, cellHeight,
                                        gridWidth, gridHeight, true, true);

        assertNotNull("Returned null map", map);

        String[] mapStr = map.getStrings();

        String[] expMap = new String[] {
            "   -----                               ",
            "   |...|             -------           ",
            "   |..<|             |.....|  -------- ",
            "   |...|    ------   |.....|  |......| ",
            "   |...|    |....|   |.....|  |......| ",
            "   |...|    |....|###+.....|  |......| ",
            "   |...|    |....+#  --+----  |......| ",
            "   |...|    -+----     #      |......| ",
            "   --+--     #         #      ------+- ",
            "     #       ####      #            #  ",
            "  ####       ---+-     #         ####  ",
            "--+------    |...|     ##       -+-----",
            "|.......|    |...|      #      #+.....|",
            "|.......|    |...|    --+---   #|.....|",
            "|.......|    |...|    |....+####|.....|",
            "|.......+##  |...|    |....|    |.....|",
            "|.......| #  |...|    |....|    --+----",
            "|.......| ###+...|    -+----      #    ",
            "----+----    -+---     #          #    ",
            " ####         #        ###        #    ",
            "-+------      ##       --+--      #    ",
            "|......|  -----+---####+...|      #    ",
            "|......|  |.......+#   |...|      #    ",
            "|......|  |.......|    |...|    --+--- ",
            "|......|  |.......|    |...|    |....| ",
            "|......|  |.......|    |...| ###+....| ",
            "--------  |.......|    |...| #  |....| ",
            "          |.......|    |...+##  ---+-- ",
            "          -+-------    -+---       #   ",
            "           #            #          #   ",
            "           #          ###        --+-- ",
            "-------   -+------  --+------    |...| ",
            "|.....|   |......|  |.......+### |...| ",
            "|.....+#  |......|  |.......|  # |...| ",
            "|.....|#  |......|  |.......|  # |...| ",
            "|.....|###+......|  |.......|  ##+...| ",
            "-------   |......|  |.......|    |..>| ",
            "          --------  |.......|    |...| ",
            "                    ---------    ----- ",
        };

        assertEquals("Unexpected map length", expMap.length, mapStr.length);

        for (int y = 0; y < expMap.length; y++) {
            assertEquals("Unexpected map[" + y + "] width",
                         expMap[y].length(), mapStr[y].length());
            for (int x = 0; x < expMap[y].length(); x++) {
                assertEquals(String.format("Unexpected map[%d][%d]", y, x),
                             expMap[y].charAt(x), mapStr[y].charAt(x));
            }
        }
    }
}
