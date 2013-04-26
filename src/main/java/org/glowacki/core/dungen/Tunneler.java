package org.glowacki.core.dungen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Tunneler
{
    private static int nextConnection;

    class RoomConnection
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
            System.out.println("    Created " + toString());
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
//        return (num == fromRoom.getNumber() || num == toRoom.getNumber());
boolean val = (num == fromRoom.getNumber() || num == toRoom.getNumber());
System.out.format("    RC %s contains %d? -> %s\n", toString(), num, val);
return val;
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
System.out.format("%c->%c X%d Y%d : %d\n", fromRoom.getChar(), toRoom.getChar(), distX, distY, distance);
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
        private Room room;
        private RoomConnection[] connections;
        private int nextConn;
        private boolean connected;

        ConnectedRoom(Room room)
        {
            this.room = room;
        }

        void connect(ConnectedRoom other)
        {
            if (nextConn == connections.length) {
                throw new Error("Room " + getNumber() +
                                " is maximally connected");
            } else if (other.nextConn == connections.length) {
                throw new Error("Room " + other.getNumber() +
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
        {
            if (i < 0 || i >= nextConn) {
                throw new Error("Bad connection " + i);
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
System.out.format("  Rm %s isLinked %d?\n", toString(), num);
            for (int i = 0; i < nextConn; i++) {
                if (connections[i].contains(num)) {
                    return true;
                }
            }

            return false;
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
    private Random random;
    private RoomConnection[] connections;

    Tunneler(Room[] rooms, int maxConnections, Random random)
    {
        this.random = random;
        this.rooms = new ConnectedRoom[rooms.length];
System.err.println("Tunnel to " + rooms.length + " rooms");
        for (int r = 0; r < rooms.length; r++) {
            this.rooms[r] = new ConnectedRoom(rooms[r]);
            this.rooms[r].setMaxConnections(maxConnections);
System.err.println("  #" + r + ": " + this.rooms[r]);
        }
    }

    private void buildTunnels(MapNode[][] map)
    {
        RoomFinder finder = new RoomFinder(map);

        for (int c = 0; c < connections.length; c++) {
            RoomConnection conn = connections[c];
System.out.format("FindPath %c->%c ====\n", conn.getFrom().getChar(), conn.getTo().getChar());

            List<MapNode> path = null;

            while (true) {
                try {
                    path = finder.findBestPath(getMidpoint(map, conn.getFrom()),
                                               getMidpoint(map, conn.getTo()));
                } catch (GeneratorException ge) {
                    // XXX deal with this!!!
                    ge.printStackTrace(System.out);
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
                            throw new Error("Node " + n +
                                            " is not a single step from " +
                                            wallNode);
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
System.out.format("==== PATH %c->%c ====\n", conn.getFrom().getChar(), conn.getTo().getChar());
CharMap.showMap(getCharMap(map));
            }
        }
    }

    char[][] dig(int width, int height)
    {
        // connect all rooms
        initialConnect(rooms);
        for (int i = 0; i < 10; i++) {
            if (interconnectLoops(rooms)) {
                break;
            }
        }

        // build sorted list of connections
        connections = sortConnections(rooms);

        MapNode[][] map = fillMap(width, height);
CharMap.showMap(getCharMap(map));

        // build tunnels
        buildTunnels(map);

        return getCharMap(map);
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
System.out.println("R#" + r + ": " + rooms[r]);

            fillRoom(map, room);
        }

        return map;
    }

    private void fillRoom(MapNode[][] map, ConnectedRoom room)
    {
final boolean DEBUG_FILL_ROOM = false;

if(DEBUG_FILL_ROOM)System.out.format("%s\n", room);
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

        final int cx = room.getX() + (room.getWidth() / 2);
        final int cy = room.getY() + (room.getHeight() / 2);
        map[cx][cy].hackChar(room.getChar());
if(DEBUG_FILL_ROOM)CharMap.showMap(getCharMap(map));
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

    /**
     * Run through room list once to connect every room to one other room.
     */
    private void initialConnect(ConnectedRoom[] rooms)
    {
final boolean DEBUG_INIT_CONN = false;
if(DEBUG_INIT_CONN)System.out.println("Connect " + rooms.length + " rooms");
        // while all rooms are not connected...
        boolean connected = false;
        while (!connected) {
            ConnectedRoom room = rooms[random.nextInt(rooms.length)];
            if (room.isConnected() || room.isFull()) {
                // already connected to another room, or
                // room already has maximum number of connections
if(DEBUG_INIT_CONN)System.out.println("Already connected " + room);
                continue;
            }

            // pick a random room
            final int otherNum = random.nextInt(rooms.length);
            if (room.getNumber() == otherNum ||
                room.isLinked(otherNum))
            {
                // picked the same room or the rooms are already linked
if(DEBUG_INIT_CONN)System.out.println("Room "+room+" is/isLinked "+rooms[otherNum]);
                continue;
            }

            ConnectedRoom other = rooms[otherNum];
            if (other.isLinked(room.getNumber()) ||
                other.isFull())
            {
if(DEBUG_INIT_CONN)System.out.println("Already connected " + other);
                // the rooms are already linked or the other room is full
                continue;
            }

            // connect the rooms
            room.connect(other);
            //other.connect(room);
if(DEBUG_INIT_CONN)System.out.println("Connect " + room + " and " + other);

            // check if all rooms are connected
            connected = true;
            for (int i = 0; i < rooms.length; i++) {
                if (!rooms[i].isConnected()) {
                    connected = false;
if(DEBUG_INIT_CONN)System.out.format("    #%d: %s not connected\n", i, rooms[i]);
                    break;
                }
if(DEBUG_INIT_CONN)System.out.format("    #%d: %s\n", i, rooms[i]);
            }
if(DEBUG_INIT_CONN)System.out.println("Connected " + connected);
        }
    }

    /**
     * Interconnect unconnected loops of rooms.
     */
    private boolean interconnectLoops(ConnectedRoom[] rooms)
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
                throw new Error("Cannot find unconnected room");
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
                throw new Error("Cannot connect two loops of connections");
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
    private boolean markConnections(ConnectedRoom room, boolean[] seen)
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
