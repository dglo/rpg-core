package org.glowacki.core.dungen;

import java.util.Random;

public class RunGen
{
    private int width;
    private int height;

    RunGen(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    void specific(Random random)
    {
        //Room[] rooms = RoomGenerator1.createRooms(random, width, height, 12);
        Room[] rooms = RoomGenerator2.createRooms(random, width, height, 3, 3);

        Tunneler tunneler = new Tunneler(rooms, 4, random);
        char[][] map = tunneler.dig(width, height);
        CharMap.showMap(map);
    }

    public static final void main(String[] args)
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
