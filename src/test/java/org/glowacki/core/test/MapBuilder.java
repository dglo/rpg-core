package org.glowacki.core.test;

import org.glowacki.core.CoreException;

class MapBuilderException
    extends CoreException
{
    MapBuilderException(String msg)
    {
        super(msg);
    }
}

public abstract class MapBuilder
{
    public static String[] buildMap(int upX, int upY, int downX, int downY)
        throws MapBuilderException
    {
        int maxX = (upX > downX ? upX + 2 : downX + 2);
        if (maxX <= 0) {
            maxX = 2;
        }

        int maxY = (upY > downY ? upY + 2: downY + 2);
        if (maxY <= 0) {
            maxY = 2;
        }

        return buildMap(upX, upY, downX, downY, maxX, maxY);
    }

    public static String[] buildMap(int upX, int upY, int downX, int downY,
                                    int maxX, int maxY)
        throws MapBuilderException
    {
        if (maxX <= 0) {
            throw new MapBuilderException("Max X " + maxX +
                                          " must be greater than 0");
        } else if (maxY <= 0) {
            throw new MapBuilderException("Max Y " + maxY +
                                          " must be greater than 0");
        } else if (upX == 0 || upX >= maxX) {
            throw new MapBuilderException("Up X " + upX +
                                          " would be embedded in the wall");
        } else if (downX == 0 || downX >= maxX) {
            throw new MapBuilderException("Down X " + downX +
                                          " would be embedded in the wall");
        } else if (upY == 0 || upY >= maxY) {
            throw new MapBuilderException("Up Y " + upY +
                                          " would be embedded in the wall");
        } else if (downY == 0 || downY >= maxY) {
            throw new MapBuilderException("Down Y " + downY +
                                          " would be embedded in the wall");
        } else if (upX == downX && upY == downY) {
            final String fmt = "Up (%d,%d) and down (%d,%d) are the same";
            throw new MapBuilderException(String.format(fmt, upX, upY, downX,
                                                        downY));
        }

        String[] map = new String[maxY + 1];

        final String wallFmt = String.format("%%%ds", maxX + 1);
        final String allWall = String.format(wallFmt, "").replace(' ', '-');

        StringBuilder buf = new StringBuilder(maxX + 1);
        for (int i = 0; i < map.length; i++) {
            if (i == 0 || i == map.length - 1) {
                map[i] = allWall;
                continue;
            }

            buf.setLength(0);
            buf.append('|');

            for (int j = 1; j < maxX; j++) {
                if (i == upY && upX == j) {
                    buf.append('<');
                } else if (i == downY && downX == j) {
                    buf.append('>');
                } else {
                    buf.append('.');
                }
            }

            buf.append('|');

            map[i] = buf.toString();
        }

        return map;
    }
}
