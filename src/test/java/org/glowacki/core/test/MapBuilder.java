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
        if (upX == 0) {
            throw new MapBuilderException("Up X " + upX +
                                          " would be embedded in the wall");
        } else if (downX == 0) {
            throw new MapBuilderException("Down X " + downX +
                                          " would be embedded in the wall");
        } else if (upY == 0) {
            throw new MapBuilderException("Up Y " + upY +
                                          " would be embedded in the wall");
        } else if (downY == 0) {
            throw new MapBuilderException("Down Y " + downY +
                                          " would be embedded in the wall");
        } else if (upX == downX && upY == downY) {
            final String fmt = "Up (%d,%d) and down (%d,%d) are the same";
            throw new MapBuilderException(String.format(fmt, upX, upY, downX,
                                                        downY));
        }

        int maxX = (upX > downX ? upX : downX);
        int maxY = (upY > downY ? upY : downY);

        String[] map = new String[maxY + 3];

        final String wallFmt = String.format("%%%ds", maxX + 3);
        final String allWall = String.format(wallFmt, "").replace(' ', '-');

        StringBuilder buf = new StringBuilder(maxX + 3);
        for (int i = 0; i < map.length; i++) {
            if (i == 0 || i == map.length - 1) {
                map[i] = allWall;
                continue;
            }

            buf.setLength(0);
            buf.append('|');

            int floorEnd = maxX + 1;
            for (int j = 1; j < maxX + 2; j++) {
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
