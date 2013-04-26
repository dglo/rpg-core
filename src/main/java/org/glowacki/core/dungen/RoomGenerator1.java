package org.glowacki.core.dungen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public abstract class RoomGenerator1
    extends BaseGenerator
{
    private static final boolean DEBUG = false;

    public static Room[] createRooms(Random random, int width, int height,
                                     int maxRooms)
    {
        LinkedList<Room> stack = new LinkedList<Room>();
        splitRoom(new Room(0, 0, 0, width, height, null), stack, random,
                  width, height, false);

        final int minSize = 4;

        for (int i = 0; i < 20; i++) {
            Room room = stack.pop();

            final int op = random.nextInt(100);
if(DEBUG)System.out.println("RndOp " + op);
            if (op < 60 || stack.size() == 1) {
                if (room.getWidth() >= minSize * 2 ||
                    room.getHeight() >= minSize * 2)
                {
                    splitRoom(room, stack, random, width, height, false);
                } else if (op > 10) {
                    splitRoom(room, stack, random, width, height, true);
                } else {
if(DEBUG)System.out.println("DropSplit " + room);
                    // destroy room
                }
            } else if (op < 80) {
                joinRoom(room, stack, random);
            } else if (op < 95 || room.getWidth() > width / 3 ||
                       room.getHeight() > height / 3)
            {
                // do nothing
                stack.addLast(room);
            } else {
if(DEBUG)System.out.println("DropOp " + room);
                // destroy room
            }
if(false&&DEBUG){
    System.out.println("-----------------------------------\n#" + i);
    for (Room r : stack) {
        System.out.println(r.toString());
    }
    System.out.println("-----------------------------------");
}
        }

        ArrayList<Room> removed = new ArrayList<Room>();
        LinkedList<Room> added = new LinkedList<Room>();

        boolean foundLarge = true;
        while (foundLarge) {
            foundLarge = false;
            for (Room r : stack) {
                if (r.getWidth() >= minSize * 3 ||
                    r.getHeight() >= minSize * 3)
                {
                    removed.add(r);
                    splitRoom(r, added, random, width, height, true);
                    foundLarge = true;
                    break;
                }
            }

            for (Room r : removed) {
                stack.remove(r);
            }
            removed.clear();

            for (Room r : added) {
                stack.addLast(r);
            }
            added.clear();
        }

        while (stack.size() > maxRooms) {
            stack.remove(random.nextInt(stack.size()));
        }

        Room[] rooms = new Room[stack.size()];
        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = stack.pop();
            rooms[i].setNumber(i);
        }

        fixNeighbors(rooms);

        return rooms;
    }

    private static void joinRoom(Room room, LinkedList<Room> stack,
                                 Random random)
    {
        Room removed = null;
        Room added = null;

        for (Room r : stack) {
            if (r.getX() == room.getX() && r.getWidth() == room.getWidth()) {
if(DEBUG)System.out.format("XX Join vert %d,%d %dx%d and %d,%d %dx%d\n",
                   room.getX(), room.getY(), room.getWidth(), room.getHeight(),
                   r.getX(), r.getY(), r.getWidth(), r.getHeight());
if(DEBUG)System.out.format("XX c1 %d == %d+%d<%d> c2 %d+%d-1<%d> == %d\n",
                  r.getY(), room.getY(), room.getHeight(),
                  room.getY() + room.getHeight() - 1, r.getY(), r.getHeight(),
                  r.getY() + r.getHeight() - 1, room.getY());

                if (r.getY() == room.getY() + room.getHeight() - 1) {
                    removed = r;
                    added = new Room(0, room.getX(), room.getY(),
                                     room.getWidth(), room.getHeight() +
                                     r.getHeight() - 1, null);
                } else if (r.getY() + r.getHeight() - 1 == room.getY()) {
                    removed = r;
                    added = new Room(0, r.getX(), r.getY(),
                                     r.getWidth(), r.getHeight() +
                                     room.getHeight() - 1, null);
                }
            } else if (r.getY() == room.getY() &&
                       r.getHeight() == room.getHeight())
            {
if(DEBUG)System.out.format("Join horiz %d,%d %dx%d and %d,%d %dx%d\n",
                   room.getX(), room.getY(), room.getWidth(), room.getHeight(),
                   r.getX(), r.getY(), r.getWidth(), r.getHeight());
if(DEBUG)System.out.format("c3 %d == %d+%d<%d> c4 %d+%d-1<%d> == %d\n",
                  r.getX(), room.getX(), room.getWidth(),
                  room.getX() + room.getWidth() - 1, r.getX(), r.getWidth(),
                  r.getX() + r.getWidth() - 1, room.getX(), room.getX());
                if (r.getX() == room.getX() + room.getWidth() - 1) {
                    removed = r;
                    added = new Room(0, room.getX(), room.getY(),
                                     room.getWidth() + r.getWidth() - 1,
                                     room.getHeight(), null);
                } else if (r.getX() + r.getWidth() - 1 == room.getX()) {
                    removed = r;
                    added = new Room(0, r.getX(), r.getY(),
                                     r.getWidth() + room.getWidth() - 1,
                                     r.getHeight(), null);
                }
            }

            if (removed != null) {
                break;
            }
        }

        if (removed != null) {
            stack.remove(removed);
            stack.addLast(added);
        } else {
            stack.addLast(room);
        }
    }

    private static void splitRoom(Room room, LinkedList<Room> stack,
                                  Random random, int maxWidth, int maxHeight,
                                  boolean killOne)
    {
        int num = random.nextInt(2) + 2;
        if (room.getWidth() < room.getHeight()) {
            int newHgt = room.getHeight() / num;
            while (newHgt < 4) {
                num--;
                if (num < 2) {
                    stack.addLast(room);
                    return;
                }

                newHgt = room.getHeight() / num;
            }

            int killIdx;
            if (!killOne || room.getHeight() > maxHeight / 3 ||
                room.getWidth() > maxWidth / 3)
            {
                killIdx = -1;
            } else if ((num & 1) == 1) {
                killIdx = num / 2;
            } else {
                killIdx = random.nextInt(num);
            }

if(DEBUG)System.out.println("Split vert " + room + " killIdx " + killIdx);
            int y = room.getY();
            for (int i = 0; i < num - 1; i++) {
                Room r = new Room(0, room.getX(), y, room.getWidth(), newHgt,
                                  null);

                if (i != killIdx) {
                    stack.addLast(r);
                } else {
                    // destroy room
if(DEBUG)System.out.println("Kill " + killIdx + " of " + num + " vert " + r);
                }
                y += newHgt - 1;
            }
            Room r = new Room(0, room.getX(), y, room.getWidth(),
                              room.getY() + room.getHeight() - y, null);
            if (num -1 != killIdx) {
                stack.addLast(r);
            } else {
                    // destroy room
if(DEBUG)System.out.println("Kill " + killIdx + " of " + num + " vert " + r);
            }
        } else {
            int newWid = room.getWidth() / num;
            while (newWid < 4) {
                num--;
                if (num < 2) {
                    stack.addLast(room);
                    return;
                }

                newWid = room.getWidth() / num;
            }

            int killIdx;
            if (!killOne || room.getHeight() > maxHeight / 3 ||
                room.getWidth() > maxWidth / 3)
            {
                killIdx = -1;
            } else if ((num & 1) == 1) {
                killIdx = num / 2;
            } else {
                killIdx = random.nextInt(num);
            }

if(DEBUG)System.out.println("Split horiz " + room + " killIdx " + killIdx);
            int x = room.getX();
            for (int i = 0; i < num - 1; i++) {
                Room r = new Room(0, x, room.getY(), newWid, room.getHeight(),
                                  null);
                if (i != killIdx) {
                    stack.addLast(r);
                } else {
                    // destroy room
if(DEBUG)System.out.println("Kill " + killIdx + " of " + num + " horiz " + r);
                }
                x += newWid - 1;
            }

            Room r = new Room(0, x, room.getY(),
                              room.getX() + room.getWidth() - x,
                              room.getHeight(), null);
            if (num - 1 != killIdx) {
                stack.addLast(r);
            } else {
                    // destroy room
if(DEBUG)System.out.println("Kill " + killIdx + " of " + num + " horiz " + r);
            }
        }
    }
}
