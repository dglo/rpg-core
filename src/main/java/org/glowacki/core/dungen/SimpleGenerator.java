package org.glowacki.core.dungen;

import java.util.ArrayList;

import org.glowacki.core.util.IRandom;

/**
 * Specialized room description
 */
class RoomPlus
    extends Room
{
    private RoomPlus[] connections;
    private int numConn;

    RoomPlus(int num, int x, int y, int width, int height, String name,
             int maxConn)
    {
        super(num, x, y, width, height, name);

        connections = new RoomPlus[maxConn];
    }

    boolean addConnection(RoomPlus room)
    {
        if (numConn == connections.length) {
            return false;
        }

        for (int i = 0; i < numConn; i++) {
            if (connections[i] != null && connections[i].equals(room)) {
                return false;
            }
        }

        connections[numConn++] = room;
        room.connections[room.numConn++] = this;
        return true;
    }

    RoomPlus[] getConnections()
    {
        return connections;
    }

    int getMaxConnections()
    {
        return connections.length;
    }

    boolean isFull()
    {
        return numConn == connections.length;
    }

    boolean overlapX(IRoom room)
    {
        return getX() < room.getX() + room.getWidth() &&
            getX() + getWidth() > room.getX();
    }

    boolean overlapY(IRoom room)
    {
        return getY() < room.getY() + room.getHeight() &&
            getY() + getHeight() > room.getY();
    }

    boolean markConnections(boolean[] seen)
    {
        boolean marked = false;
        for (int i = 0; i < numConn; i++) {
            final int num = connections[i].getNumber();
            if (!seen[num]) {
                seen[num] = true;
                marked = true;
            }
        }
        return marked;
    }
}

/**
 * Connection between two rooms
 */
class Connection
{
    private IRoom r1;
    private IRoom r2;

    Connection(IRoom r1, IRoom r2)
    {
        this.r1 = r1;
        this.r2 = r2;
    }

    private void addDoor(IMapArray map, int a, int b, boolean isHorizontal)
        throws GeneratorException
    {
        if (isHorizontal) {
            map.addDoor(a, b);
        } else {
            map.addDoor(b, a);
        }
    }

    private void drawTunnel(IRandom random, IMapArray map, int lowA, int lowDim,
                            int lowB, int lowOther, int highA, int highDim,
                            int highB, int highOther, boolean isHorizontal)
        throws GeneratorException
    {
        final int lowWall = lowA + lowDim - 1;
        final int lowDoor = findDoor(random, lowB, lowOther);

        final int highWall = highA;
        final int highDoor = findDoor(random, highB, highOther);

        final int shift = findShift(random, lowWall, highWall);

        addDoor(map, lowWall, lowDoor, isHorizontal);
        addDoor(map, highWall, highDoor, isHorizontal);

        for (int i = lowWall + 1; i < highWall; i++) {
            final int j;
            if (i <= shift) {
                j = lowDoor;
            } else {
                j = highDoor;
            }

            if (isHorizontal) {
                map.tunnel(i, j);
            } else {
                map.tunnel(j, i);
            }
        }

        int lowShift;
        int highShift;
        if (lowDoor < highDoor) {
            lowShift = lowDoor;
            highShift = highDoor;
        } else {
            lowShift = highDoor;
            highShift = lowDoor;
        }

        for (int i = lowShift; i <= highShift; i++) {
            if (isHorizontal) {
                map.tunnel(shift, i);
            } else {
                map.tunnel(i, shift);
            }
        }
    }

    private int findDoor(IRandom random, int coord, int len)
    {
        return coord + 1 + random.nextInt(len - 2);
    }

    private int findShift(IRandom random, int low, int high)
    {
        int shift;
        if (high - low < 4) {
            shift = low + (((high - low) + 1) / 2);
        } else {
            shift = low + 1 + random.nextInt((high - low) - 1);
        }

        return shift;
    }

    boolean matches(IRoom a, IRoom b)
    {
        return (r1.equals(a) && r2.equals(b)) ||
            (r2.equals(a) && r1.equals(b));
    }

    void tunnel(IRandom random, IMapArray map)
        throws GeneratorException
    {
        IRoom left;
        IRoom right;
        if (r1.getX() < r2.getX()) {
            left = r1;
            right = r2;
        } else {
            left = r2;
            right = r1;
        }

        IRoom top;
        IRoom bottom;
        if (r1.getY() < r2.getY()) {
            top = r1;
            bottom = r2;
        } else {
            top = r2;
            bottom = r1;
        }

        if (right.getX() - (left.getX() + left.getWidth()) >
            bottom.getY() - (top.getY() + top.getHeight()))
        {
            drawTunnel(random, map, left.getX(), left.getWidth(), left.getY(),
                       left.getHeight(), right.getX(), right.getWidth(),
                       right.getY(), right.getHeight(), true);
        } else {
            drawTunnel(random, map, top.getY(), top.getHeight(), top.getX(),
                       top.getWidth(), bottom.getY(), bottom.getHeight(),
                       bottom.getX(), bottom.getWidth(), false);
        }
    }

    public String toString()
    {
        return String.format("Connection[%s<->%s]", r1, r2);
    }
}

/**
 * Simple map generator
 */
public class SimpleGenerator
{
    private static void addRoomStairs(IRandom random, RoomPlus room,
                                      IMapArray map, boolean isUp)
        throws GeneratorException
    {
        final int sx = room.getX() + getStairPos(random, room.getWidth());
        final int sy = room.getY() + getStairPos(random, room.getHeight());
        map.addStaircase(sx, sy, isUp);
    }

    private static int getStairPos(IRandom random, int max)
    {
        final int pos;
        if (max < 5) {
            pos = 1 + random.nextInt(2);
        } else if (max == 5) {
            pos = 3;
        } else {
            pos = 2 + random.nextInt(max - 4);
        }
        return pos;
    }

    private static void addStairs(IRandom random, RoomPlus[] rooms,
                                  int gridWidth, int gridHeight, IMapArray map,
                                  boolean addUpStairs, boolean addDownStairs)
        throws GeneratorException
    {
        final int one;
        final int other;

        if (random.nextBoolean()) {
            final int x = random.nextInt(gridWidth);

            one = x;
            other = (gridHeight - 1) * gridWidth + (gridWidth - x) - 1;
        } else {
            final int y = random.nextInt(gridHeight);

            one = y * gridWidth;
            other = (gridHeight - (y + 1)) * gridWidth + (gridWidth - 1);
        }

        final int up;
        final int down;
        if (random.nextBoolean()) {
            up = one;
            down = other;
        } else {
            up = other;
            down = one;
        }

        if (addUpStairs) {
            addRoomStairs(random, rooms[up], map, true);
        }
        if (addDownStairs) {
            addRoomStairs(random, rooms[down], map, false);
        }
    }

    /**
     * Build rooms
     *
     * @param random random number generator
     * @param maxWidth maximum width
     * @param maxHeight maximum height
     * @param gridWidth number of rooms horizontally
     * @param gridHeight number of rooms vertically
     *
     * @return array of room descriptions
     *
     * @throws GeneratorException if there is a problem
     */
    public static RoomPlus[] buildRooms(IRandom random, int maxWidth,
                                        int maxHeight, int gridWidth,
                                        int gridHeight)
        throws GeneratorException
    {
        final int cellWidth = maxWidth / gridWidth;
        final int cellHeight = maxHeight / gridHeight;

        final int numRooms = gridWidth * gridHeight;

        RoomPlus[] rooms = new RoomPlus[numRooms];

        StringBuilder nameBuf = new StringBuilder(1);

        for (int r = 0; r < rooms.length; r++) {
            final int i = r % gridWidth;
            final int j = r / gridWidth;

            int width =
                (cellWidth / 2) + random.nextInt((cellWidth + 1) / 2);
            if (width < 4) {
                width = 4;
            }
            int height =
                (cellHeight / 2) + random.nextInt((cellHeight + 1) / 2);
            if (height < 4) {
                height = 4;
            }

            final int x = (cellWidth * i) +
                (width < cellWidth ? random.nextInt(cellWidth - width) : 0);
            final int y = (cellHeight * j) +
                (height < cellHeight ? random.nextInt(cellHeight - height) : 0);

            nameBuf.setLength(0);
            nameBuf.append('A' + r);

            final int maxConn = 2 +
                (i == 0 || i == gridWidth - 1 ? 0 : 1) +
                (j == 0 || j == gridHeight - 1 ? 0 : 1);

            rooms[r] = new RoomPlus(r, x, y, width, height, nameBuf.toString(),
                                    maxConn);
        }

        return rooms;
    }

    private static boolean connect(IRandom random, RoomPlus[] rooms,
                                   int gridWidth, int gridHeight)
        throws GeneratorException
    {
        for (int r = 0; r < rooms.length; r++) {
            final int maxConn = rooms[r].getMaxConnections();
            final int connect = 1 + random.nextInt(maxConn - 1);
            for (int n = 0; n < connect; n++) {
                connectOne(rooms, random, gridWidth, gridHeight, r);
            }
        }

        boolean[] seen = new boolean[rooms.length];
        findConnected(rooms, seen);

        int failCount = 0;
        while (!seenAll(seen) && failCount < 10) {
            // find random unconnected room
            int unconn = findUnconnected(random, rooms, seen);
            if (unconn < 0) {
                failCount++;
                continue;
            }

            // find next connected room which is not fully connected
            RoomPlus conn = null;
            for (int r : getNeighbors(gridWidth, gridHeight, unconn)) {
                if (seen[r] && !rooms[r].isFull()) {
                    conn = rooms[r];
                    break;
                }
            }
            if (conn == null) {
                failCount++;
                continue;
            }

            // connect the loops
            rooms[unconn].addConnection(conn);
            findConnected(rooms, seen);
            failCount = 0;
        }

        return seenAll(seen);
    }

    private static boolean connectOne(RoomPlus[] rooms, IRandom random,
                                      int gridWidth, int gridHeight, int r)
    {
        int[] neighbors = getNeighbors(gridWidth, gridHeight, r);
        RoomPlus room = rooms[neighbors[random.nextInt(neighbors.length)]];
        return rooms[r].addConnection(room);
    }

    /**
     * Generate a map
     *
     * @param random random number generator
     * @param maxWidth maximum width
     * @param maxHeight maximum height
     * @param gridWidth number of rooms horizontally
     * @param gridHeight number of rooms vertically
     * @param addUpStairs add an up staircase
     * @param addDownStairs add a down staircase
     *
     * @return generated character map
     *
     * @throws GeneratorException if there is a problem
     */
    public static IMapArray createRooms(IRandom random, int maxWidth,
                                        int maxHeight, int gridWidth,
                                        int gridHeight, boolean addUpStairs,
                                        boolean addDownStairs)
        throws GeneratorException
    {
        RoomPlus[] rooms =
            buildRooms(random, maxWidth, maxHeight, gridWidth, gridHeight);

        if (!connect(random, rooms, gridWidth, gridHeight)) {
            IMapArray map = drawMap(random, rooms, gridWidth, gridHeight,
                                    addUpStairs, addDownStairs, true);
            map.show(System.err);
            throw new GeneratorException("Cannot connect all rooms");
        }

        return drawMap(random, rooms, gridWidth, gridHeight, addUpStairs,
                       addDownStairs, false);
    }

    private static void digTunnels(IRandom random, RoomPlus[] rooms,
                                   IMapArray map)
        throws GeneratorException
    {
        ArrayList<Connection> connections = new ArrayList<Connection>();
        for (int r = rooms.length - 1; r >= 0; r--) {
            for (RoomPlus room : rooms[r].getConnections()) {
                if (room == null) {
                    continue;
                }

                boolean found = false;
                for (Connection c : connections) {
                    if (c.matches(rooms[r], room)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    continue;
                }

                // remember this connection
                Connection conn = new Connection(rooms[r], room);
                connections.add(conn);

                conn.tunnel(random, map);
            }
        }
    }

    private static IMapArray drawMap(IRandom random, RoomPlus[] rooms,
                                     int gridWidth, int gridHeight,
                                     boolean addUpStairs,
                                     boolean addDownStairs, boolean addLabel)
        throws GeneratorException
    {
        IMapArray map = new CharMap(rooms, addLabel);

        digTunnels(random, rooms, map);
        addStairs(random, rooms, gridWidth, gridHeight, map, addUpStairs,
                  addDownStairs);

        return map;
    }

    private static boolean[] findConnected(RoomPlus[] rooms, boolean[] seen)
    {
        // clear 'seen' array
        for (int i = 0; i < seen.length; i++) {
            seen[i] = false;
        }

        // initialize the 'seen' array with the first room's connections
        rooms[0].markConnections(seen);

        // loop through all other rooms and mark the connections of
        // all rooms which have previously been seen. Loop stops
        // when we've visited all rooms which are directly or indirectly
        // connected to the first room
        boolean added = true;
        while (added) {
            added = false;
            for (int i = 0; i < rooms.length; i++) {
                if (seen[i]) {
                    added |= rooms[i].markConnections(seen);
                }
            }
        }

        return seen;
    }

    private static int findUnconnected(IRandom random, RoomPlus[] rooms,
                                       boolean[] seen)
    {
        int[] unconn = new int[rooms.length];
        int num = 0;
        for (int s = 0; s < seen.length; s++) {
            if (!seen[s] && !rooms[s].isFull()) {
                unconn[num++] = s;
            }
        }

        if (num == 0) {
            return -1;
        }

        return unconn[random.nextInt(num)];
    }

    private static int[] getNeighbors(int gridWidth, int gridHeight, int r)
    {
        final int i = r % gridWidth;
        final int j = r / gridWidth;

        int[] tmp = new int[4];

        int num = 0;
        if (i > 0) {
            tmp[num++] = r - 1;
        }
        if (j > 0) {
            tmp[num++] = r - gridWidth;
        }
        if (i < gridWidth - 1) {
            tmp[num++] = r + 1;
        }
        if (j < gridHeight - 1) {
            tmp[num++] = r + gridWidth;
        }

        int[] neighbors = new int[num];
        for (int n = 0; n < num; n++) {
            neighbors[n] = tmp[n];
        }

        return neighbors;
    }

    private static boolean seenAll(boolean[] seen)
    {
        for (int i = 0; i < seen.length; i++) {
            if (!seen[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Run generator from the command line
     *
     * @param args command-line arguments
     *
     * @throws GeneratorException if there is a problem
     */
/*
    public static final void main(String[] args)
        throws GeneratorException
    {
        long seed = 12345;

        int maxWidth = 22;
        int maxHeight = 22;
        int gridWidth = 3;
        int gridHeight = 3;

        int argNum = 0;
        boolean getSeed = false;
        for (int i = 0; i < args.length; i++) {
            if (getSeed) {
                try {
                    seed = Long.parseLong(args[i]);
                } catch (NumberFormatException nfe) {
                    System.err.format("Bad seed '%s'\n", args[i]);
                    System.exit(1);
                }
                continue;
            }

            if (args[i].startsWith("-s")) {
                getSeed = true;
                continue;
            }

            int val;
            try {
                val = Integer.parseInt(args[i]);
            } catch (NumberFormatException nfe) {
                System.err.format("Bad value '%s'\n", args[i]);
                System.exit(1);
                val = 0;
            }

            switch (argNum++) {
            case 0:
                maxWidth = val;
                break;
            case 1:
                maxHeight = val;
                break;
            case 2:
                gridWidth = val;
                break;
            case 3:
                gridHeight = val;
                break;
            default:
                System.err.format("Extra argument '%s'\n", args[i]);
                System.exit(1);
                break;
            }
        }

        System.out.format("Max %dx%d grid %dx%d seed %d\n", maxWidth,
                          maxHeight, gridWidth, gridHeight, seed);

        IRandom random = new Random(seed);

        IMapArray map =
            SimpleGenerator.createRooms(random, maxWidth, maxHeight,
                                       gridWidth, gridHeight, true, true);
        map.show();
    }
*/
}
