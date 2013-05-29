package org.glowacki.core.dungen;

import org.glowacki.core.util.IRandom;
import org.glowacki.core.util.Random;

public class RunGen
{
    private int width;
    private int height;

    RunGen(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    void specific(IRandom random)
        throws GeneratorException
    {
        //Room[] rooms = RoomGenerator1.createRooms(random, width, height, 12);
        Room[] rooms = RoomGenerator2.createRooms(random, width, height, 3, 3);
        RoomGenerator2.addStairs(rooms, random, true, true);

        Tunneler tunneler = new Tunneler(rooms, 4);
        String[] map = tunneler.dig(width, height, random);
        for (int i = 0; i < map.length; i++) {
            System.out.println(map[i]);
        }
    }

    public static final void main(String[] args)
        throws GeneratorException
    {
        long seed = 123;
        int width = 79;
        int height = 23;

        if (args.length > 0)
            seed = Long.parseLong(args[0]);
        if (args.length > 1)
            width = Integer.parseInt(args[1]);
        if (args.length > 2)
            height = Integer.parseInt(args[2]);

        RunGen x = new RunGen(width, height);
        x.specific(new Random(seed));
    }
}
