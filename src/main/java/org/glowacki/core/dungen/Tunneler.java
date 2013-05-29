package org.glowacki.core.dungen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.glowacki.core.astar.PathException;
import org.glowacki.core.util.IRandom;

class TunnelerException
    extends GeneratorException
{
    TunnelerException(String msg)
    {
        super(msg);
    }
}

public class Tunneler
{
    private static int nextConnection;

    static class RoomConnection
        implements Comparable
    {
        private int id = nextConnection++;
        private ConnectedRoom fromRoom;
        private ConnectedRoom toRoom;
        private int distance = Integer.MIN_VALUE;
        private boolean drawn;

        RoomConnection(ConnectedRoom fromRoom, ConnectedRoom toRoom)
        {
            this.fromRoom = fromRoom;
            this.toRoom = toRoom;
        }

        public int compareTo(Object obj)
        {
            if (obj == null) {
                return -1;
            }

            if (!(obj instanceof RoomConnection)) {
                return getClass().getName().compareTo(obj.getClass().getName());
            }

            RoomConnection rc = (RoomConnection) obj;

            int val = getDistance() - rc.getDistance();
            if (val == 0) {
                val = id - rc.id;
            }

            return val;
        }

        public boolean contains(int num)
        {
            return (num == fromRoom.getNumber() || num == toRoom.getNumber());
        }

        public boolean equals(Object obj)
        {
            return compareTo(obj) == 0;
        }

        public int getDistance()
        {
            // if we've already computed this, use the cached value
            if (distance == Integer.MIN_VALUE) {
                final int distX = fromRoom.getDistanceX(toRoom);
                final int distY = fromRoom.getDistanceY(toRoom);

                distance = (int) Math.sqrt(distX * distX + distY * distY);
            }

            return distance;
        }

        public ConnectedRoom getFrom()
        {
            return fromRoom;
        }

        public ConnectedRoom getOtherEnd(ConnectedRoom end)
        {
            if (fromRoom == end) {
                return toRoom;
            }

            if (toRoom == end) {
                return fromRoom;
            }

            return null;
        }

        public ConnectedRoom getTo()
        {
            return toRoom;
        }

        public int hashCode()
        {
            return id;
        }

        public boolean isDrawn()
        {
            return drawn;
        }

        public void setDrawn()
        {
            drawn = true;
        }

        public String toString()
        {
            String dStr;
            if (distance == Integer.MIN_VALUE) {
                dStr = "";
            } else {
                dStr = String.format("(dist %d)", distance);
            }

            return String.format("%c#%d<->%c#%d%s%s", fromRoom.getChar(),
                                 fromRoom.getNumber(), toRoom.getChar(),
                                 toRoom.getNumber(), dStr,
                                 drawn ? "drawn" : "");
        }
    }

    class ConnectedRoom
    {
        private IRoom room;
        private RoomConnection[] connections;
        private int nextConn;
        private boolean connected;

        ConnectedRoom(IRoom room)
        {
            this.room = room;
        }

        void connect(ConnectedRoom other)
            throws TunnelerException
        {
            if (nextConn == connections.length) {
                throw new TunnelerException("Room " + getNumber() +
                                " is maximally connected");
            } else if (other.nextConn == connections.length) {
                throw new TunnelerException("Room " + other.getNumber() +
                                " is maximally connected");
            }

            RoomConnection conn = new RoomConnection(this, other);
            connections[nextConn++] = conn;
            this.connected = true;

            other.connections[other.nextConn++] = conn;
            other.connected = true;
        }

        char getChar()
        {
            return room.getChar();
        }

        RoomConnection getConnection(int i)
            throws TunnelerException
        {
            if (i < 0 || i >= nextConn) {
                throw new TunnelerException("Bad connection " + i);
            }

            return connections[i];
        }

        private int getDistance(int coord1, int size1, int coord2, int size2)
        {
            if (coord1 < coord2) {
                if (coord1 + size1 >= coord2) {
                    return 0;
                }

                return coord2 - coord1 + size1;
            }

            if (coord2 + size2 >= coord1) {
                return 0;
            }

            return coord1 - coord2 + size2;
        }

        int getDistanceX(ConnectedRoom r)
        {
            return getDistance(room.getX(), room.getWidth(), r.room.getX(),
                               r.room.getWidth());
        }

        int getDistanceY(ConnectedRoom r)
        {
            return getDistance(room.getY(), room.getHeight(), r.room.getY(),
                               r.room.getHeight());
        }

        int getHeight()
        {
            return room.getHeight();
        }

        int getNumConnections()
        {
            return nextConn;
        }

        int getNumber()
        {
            return room.getNumber();
        }

        int getStaircaseX()
        {
            return room.getStaircaseX();
        }

        int getStaircaseY()
        {
            return room.getStaircaseY();
        }

        int getWidth()
        {
            return room.getWidth();
        }

        int getX()
        {
            return room.getX();
        }

        int getY()
        {
            return room.getY();
        }

        boolean hasStaircase()
        {
            return room.hasStaircase();
        }

        boolean isConnected()
        {
            return connected;
        }

        boolean isFull()
        {
            return nextConn == connections.length;
        }

        boolean isLinked(int num)
        {
            for (int i = 0; i < nextConn; i++) {
                if (connections[i].contains(num)) {
                    return true;
                }
            }

            return false;
        }

        boolean isUpStaircase()
        {
            return room.isUpStaircase();
        }

        void setMaxConnections(int max)
        {
            this.connections = new RoomConnection[max];
            for (int i = 0; i < connections.length; i++) {
                connections[i] = null;
            }
        }

        public String toString()
        {
            String cStr = "";
            for (int i = 0; i < nextConn; i++) {
                if (cStr.length() == 0) {
                    cStr = ": ";
                }
                cStr += String.format(" %s", connections[i]);
            }

            return String.format("%c[%d,%d]%dx%d%s", getChar(), room.getX(),
                                 room.getY(), room.getWidth(),
                                 room.getHeight(), cStr);
        }
    }

    private ConnectedRoom[] rooms;
    private RoomConnection[] connections;

    public Tunneler(IRoom[] rlist, int maxConnections)
        throws TunnelerException
    {
        final int len = (rlist == null ? 0 : rlist.length);
        if (len < 2) {
            throw new TunnelerException("Expect at least 2 rooms, not " + len);
        }

        rooms = new ConnectedRoom[len];
        for (int r = 0; r < len; r++) {
            if (rlist[r] == null) {
                final String msg = String.format("List entry %d is null", r);
                throw new TunnelerException(msg);
            } else if (rlist[r].getNumber() < 0 ||
                       rlist[r].getNumber() >= rooms.length)
            {
                final String msg =
                    String.format("Bad room number %d (must be between" +
                                  " 0 and %d)", rlist[r].getNumber(),
                                  rlist.length);
                throw new TunnelerException(msg);
            } else if (rooms[rlist[r].getNumber()] != null) {
                final String msg =
                    String.format("Found multiple rooms numbered %d",
                                  rlist[r].getNumber());
                throw new TunnelerException(msg);
            }

            rooms[rlist[r].getNumber()] = new ConnectedRoom(rlist[r]);
            rooms[rlist[r].getNumber()].setMaxConnections(maxConnections);
        }
    }

    private void buildTunnels(MapNode[][] map)
        throws TunnelerException
    {
        RoomFinder finder = new RoomFinder(map);

        for (int c = 0; c < connections.length; c++) {
            RoomConnection conn = connections[c];

            List<MapNode> path = null;

            while (true) {
                try {
                    path = finder.findBestPath(getMidpoint(map, conn.getFrom()),
                                               getMidpoint(map, conn.getTo()));
                } catch (GeneratorException ge) {
                    // XXX deal with this!!!
                    ge.printStackTrace(System.out);
                    continue;
                } catch (PathException pe) {
                    // XXX deal with this!!!
                    pe.printStackTrace(System.out);
                    continue;
                }

                if (path == null) {
                    // XXX deal with this!!!
                    System.out.println("Cannot generate path from " +
                                       conn.getFrom() + " to " + conn.getTo());
                    break;
                }

                MapNode wallNode = null;
                boolean isVertical = false;
                for (MapNode n : path) {
                    if (wallNode != null) {
                        if (n.getX() == wallNode.getX() + 1 ||
                            n.getX() == wallNode.getX() - 1)
                        {
                            // crossing vertical wall horizontally
                            isVertical = true;
                        } else if (n.getY() == wallNode.getY() + 1 ||
                                   n.getY() == wallNode.getY() - 1)
                        {
                            // crossing horizontal wall vertically
                            isVertical = false;
                        } else {
                            final String msg = "Node " + n +
                                " is not a single step from " + wallNode;
                            throw new TunnelerException(msg);
                        }
                        break;
                    } else if (n.isWall()) {
                        wallNode = n;
                    }
                }

                // didn't bust through any walls, so path is usable
                if (wallNode == null) {
                    break;
                }

                makeDoor(map, wallNode, isVertical);
            }

            if (path != null) {
                fillTunnel(path);
            }
        }
    }

    public String[] dig(int width, int height, IRandom random)
        throws TunnelerException
    {
        // connect all rooms
        initialConnect(rooms, random);
        for (int i = 0; i < 10; i++) {
            if (interconnectLoops(rooms)) {
                break;
            }
        }

        // build sorted list of connections
        connections = sortConnections(rooms);

        MapNode[][] map = fillMap(width, height);

        // build tunnels
        buildTunnels(map);

        return getStringMap(map);
    }

    private MapNode[][] fillMap(int mapWidth, int mapHeight)
    {
        // initialize everything to space character
        MapNode[][] map = new MapNode[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                map[x][y] = new MapNode(x, y);
            }
        }

        // fill in rooms
        for (int r = 0; r < rooms.length; r++) {
            ConnectedRoom room = rooms[r];

            fillRoom(map, room);
        }

        return map;
    }

    private void fillRoom(MapNode[][] map, ConnectedRoom room)
    {
        final int left = room.getX();
        final int top = room.getY();
        final int right = left + room.getWidth() - 1;
        final int bottom = top + room.getHeight() - 1;

        boolean overlapError = false;
        for (int xx = left; xx <= right; xx++) {
            for (int yy = top; yy <= bottom; yy++) {
                if (!map[xx][yy].isEmpty() && !map[xx][yy].isWall() &&
                    !overlapError)
                {
                    System.out.println("Room " + room + " overwrites data");
                    overlapError = true;
                }

                if (xx == left || xx == right || yy == top || yy == bottom) {
                    map[xx][yy].setType(RoomType.WALL);
                } else {
                    map[xx][yy].setType(RoomType.FLOOR);
                }
            }
        }

        if (room.hasStaircase()) {
            final int x = room.getX() + room.getStaircaseX();
            final int y = room.getY() + room.getStaircaseY();

            if (room.isUpStaircase()) {
                map[x][y].setType(RoomType.UPSTAIRS);
            } else {
                map[x][y].setType(RoomType.DOWNSTAIRS);
            }
        }
    }

    private void fillTunnel(List<MapNode> path)
    {
        for (MapNode n : path) {
            if (n.isEmpty()) {
                n.setType(RoomType.TUNNEL);
            }
        }
    }

    private char[][] getCharMap(MapNode[][] map)
    {
        char[][] chMap = new char[map.length][map[0].length];
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                chMap[x][y] = map[x][y].getChar();
            }
        }

        return chMap;
    }

    private static MapNode getMidpoint(MapNode[][] map, ConnectedRoom room)
    {
        final int midX = room.getX() + (room.getWidth() / 2);
        final int midY = room.getY() + (room.getHeight() / 2);

        return map[midX][midY];
    }

    private String[] getStringMap(MapNode[][] map)
    {
        String[] chMap = new String[map.length];
        for (int x = 0; x < map.length; x++) {
            char[] chars = new char[map[x].length];
            for (int y = 0; y < map[0].length; y++) {
                chars[y] = map[x][y].getChar();
            }
            chMap[x] = new String(chars);
        }

        return chMap;
    }

    /**
     * Run through room list once to connect every room to one other room.
     */
    private static void initialConnect(ConnectedRoom[] rooms, IRandom random)
        throws TunnelerException
    {
        int attempts = 0;

        // while all rooms are not connected...
        boolean connected = false;
        while (!connected && attempts < rooms.length) {
            ConnectedRoom room = rooms[random.nextInt(rooms.length)];
            if (room.isConnected() || room.isFull()) {
                // already connected to another room, or
                // room already has maximum number of connections
                attempts++;
                continue;
            }

            // pick a random room
            final int otherNum = random.nextInt(rooms.length);
            if (room.getNumber() == otherNum ||
                room.isLinked(otherNum))
            {
                // picked the same room or the rooms are already linked
                attempts++;
                continue;
            }

            ConnectedRoom other = rooms[otherNum];
            if (other.isLinked(room.getNumber()) ||
                other.isFull())
            {
                // the rooms are already linked or the other room is full
                attempts++;
                continue;
            }

            attempts = 0;

            // connect the rooms
            room.connect(other);
            //other.connect(room);

            // check if all rooms are connected
            connected = true;
            for (int i = 0; i < rooms.length; i++) {
                if (!rooms[i].isConnected()) {
                    connected = false;
                    break;
                }
            }
        }
    }

    /**
     * Interconnect unconnected loops of rooms.
     */
    private static boolean interconnectLoops(ConnectedRoom[] rooms)
        throws TunnelerException
    {
        // initialize the 'seen' array with the first room's connections
        boolean[] seen = new boolean[rooms.length];
        markConnections(rooms[0], seen);

        // loop through all other rooms and mark the connections of
        // all rooms which have previously been seen. Loop stops
        // when we've visited all rooms which are directly or indirectly
        // connected to the first room
        boolean added = true;
        while (added) {
            added = false;
            for (int i = 0; i < rooms.length; i++) {
                if (seen[i]) {
                    added |= markConnections(rooms[i], seen);
                }
            }
        }

        // check if all rooms are connected to the first room
        boolean seenAll = true;
        for (int i = 0; seenAll && i < seen.length; i++) {
            seenAll &= seen[i];
        }

        if (!seenAll) {
            // find first unconnected room which is not fully connected
            ConnectedRoom unconn = null;
            for (int i = 0; i < seen.length; i++) {
                if (!seen[i] && !rooms[i].isFull()) {
                    unconn = rooms[i];
                    break;
                }
            }
            if (unconn == null) {
                throw new TunnelerException("Cannot find unconnected room");
            }

            // find next connected room which is not fully connected
            ConnectedRoom conn = null;
            final int nextRoom = (unconn.getNumber() + 1) % rooms.length;
            for (int i = nextRoom; i != unconn.getNumber();
                 i = (i + 1) % rooms.length)
            {
                if (seen[i] && !rooms[i].isFull()) {
                    conn = rooms[i];
                    break;
                }
            }
            if (conn == null) {
                throw new TunnelerException("Cannot connect two loops of " +
                                            "connections");
            }

            // connect the loops
            unconn.connect(conn);
            //conn.connect(unconn);
        }

        // return <tt>true</tt> if all the rooms are interconnected
        return seenAll;
    }

    private void makeDoor(MapNode[][] map, MapNode wallNode,
                          boolean isVertical)
    {
        System.out.println("XXX MakeDoor HACK!!!");
        wallNode.setType(RoomType.DOOR);
    }

    /**
     * Mark all this room's connections as seen in the provided array.
     *
     * @param room room being checked
     * @param seen array of values which are set to <tt>true</tt> if this
     *             room is connected to them.
     *
     * @return <tt>true</tt> if any new connections were marked
     */
    private static boolean markConnections(ConnectedRoom room, boolean[] seen)
        throws TunnelerException
    {
        boolean marked = false;
        for (int i = 0; i < room.getNumConnections(); i++) {
            final ConnectedRoom other =
                room.getConnection(i).getOtherEnd(room);
            final int num = other.getNumber();
            if (!seen[num]) {
                seen[num] = true;
                marked = true;
            }
        }
        return marked;
    }

    private RoomConnection[] sortConnections(ConnectedRoom[] rooms)
        throws TunnelerException
    {
        HashMap<RoomConnection, RoomConnection> connMap =
            new HashMap<RoomConnection, RoomConnection>();
        for (int r = 0; r < rooms.length; r++) {
            for (int c = 0; c < rooms[r].getNumConnections(); c++) {
                RoomConnection conn = rooms[r].getConnection(c);
                connMap.put(conn, conn);
            }
        }

        RoomConnection[] conns =
            connMap.values().toArray(new RoomConnection[0]);

        Arrays.sort(conns);

        return conns;
    }
}
