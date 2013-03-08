package org.glowacki.core.ascii;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import org.glowacki.core.Character;
import org.glowacki.core.CoreException;
import org.glowacki.core.Level;
import org.glowacki.core.MovableCharacter;
import org.glowacki.core.MovableCharacter.Direction;
import org.glowacki.core.Terrain;

class AsciiTerm
{
    private Screen screen;
    private int maxRows;
    private int maxCols;

    private boolean running;

    private Level level;
    private MovableCharacter character;

    private String errMsg;

    AsciiTerm()
    {
        //Terminal terminal = TerminalFacade.createTerminal();
        //terminal.enterPrivateMode();
        screen = TerminalFacade.createScreen();

        TerminalSize sz = screen.getTerminal().getTerminalSize();
        maxRows = sz.getRows();
        maxCols = sz.getColumns();

        screen.startScreen();
    }

    private char[][] buildMap()
    {
        final int maxX = level.getMaxX();
        final int maxY = level.getMaxY();

        char[][] map = new char[maxY + 1][maxX + 1];

        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                char ch;
                try {
                    ch = Terrain.getCharacter(level.get(x, y));
                } catch (CoreException ce) {
                    ce.printStackTrace();
                    ch = '?';
                }

                map[y][x] = ch;
            }
        }

        for (MovableCharacter c : level.getCharacters()) {
            map[c.getY()][c.getX()] = '*';
        }

        return map;
    }

    public void close()
    {
        screen.stopScreen();
        //terminal.exitPrivateMode();
    }

    private void drawScreen()
    {
        screen.clear();

        if (level != null) {
            char[][] map = buildMap();

            int padX = (maxCols - map[0].length) / 2;
            int padY = (maxRows - map.length) / 2;

            StringBuilder buf = new StringBuilder(map[0].length);
            for (int y = 0; y < map.length; y++) {
                buf.setLength(0);
                for (int x = 0; x < map[y].length; x++) {
                    buf.append(map[y][x]);
                }

                screen.putString(padX, padY + y, buf.toString(),
                                 Terminal.Color.WHITE,  Terminal.Color.BLACK);
            }
        }

        if (errMsg != null) {
            screen.putString(0, 23, errMsg, Terminal.Color.RED,
                             Terminal.Color.BLACK);
        }

        screen.refresh();
    }

    private boolean handleInput(Key key)
    {
        int turns;
        if (key.getKind() == Key.Kind.NormalKey) {
            switch (key.getCharacter()) {
            case '<':
                try {
                    turns = character.move(Direction.CLIMB);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                    break;
                }

                if (turns >= 0) {
                    Level nextLevel = level.getPreviousLevel();
                    if (nextLevel == null) {
                        errMsg = "You cannot exit this dungeon";
                        break;
                    }

                    try {
                        level.exit(character);
                        nextLevel.enterUp(character);
                    } catch (CoreException ce) {
                        ce.printStackTrace();
                        errMsg = "Something is seriously hosed";
                    }

                    level = nextLevel;
                }

                break;
            case '>':
                try {
                    turns = character.move(Direction.DESCEND);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                    break;
                }

                if (turns >= 0) {
                    Level nextLevel = level.getNextLevel();
                    if (nextLevel == null) {
                        errMsg = "You are at the bottom of this dungeon";
                        break;
                    }

                    try {
                        level.exit(character);
                        nextLevel.enterDown(character);
                    } catch (CoreException ce) {
                        ce.printStackTrace();
                        errMsg = "Something is seriously hosed";
                    }

                    level = nextLevel;
                }

                break;
            case 'h':
                try {
                    turns = character.move(Direction.LEFT);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                }
                break;
            case 'j':
                try {
                    turns = character.move(Direction.DOWN);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                }
                break;
            case 'k':
                try {
                    turns = character.move(Direction.UP);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                }
                break;
            case 'l':
                try {
                    turns = character.move(Direction.RIGHT);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                }
                break;
            case 'q':
                running = false;
                break;
            default:
                errMsg = "Unknown key " + key;
                break;
            }
        } else {
            Direction dir;
            switch (key.getKind()) {
            case ArrowLeft:
                dir = Direction.LEFT;
                break;
            case ArrowRight:
                dir = Direction.RIGHT;
                break;
            case ArrowUp:
                dir = Direction.UP;
                break;
            case ArrowDown:
                dir = Direction.DOWN;
                break;
            default:
                errMsg = "Unknown key " + key;
                dir = Direction.UNKNOWN;
                break;
            }

            if (dir != Direction.UNKNOWN) {
                try {
                    turns = character.move(dir);
                } catch (CoreException ce) {
                    errMsg = ce.getMessage();
                }
            }
        }

        return true;
    }

    public void loop()
    {
        running = true;

        drawScreen();
        while (running) {
            boolean changed = false;

            Key key = screen.readInput();
            if (key != null) {
                errMsg = null;
                changed = handleInput(key);
            }

            if (changed || screen.resizePending()) {
                drawScreen();
            }
        }
    }

    public void setCharacter(Character c)
        throws CoreException
    {
        character = level.enterDown(c);
    }

    public void setLevel(Level l)
    {
        level = l;
    }
}

public class Runner
{
    private static final String[] level1 = new String[] {
        "           ---------",
        "           |.......|",
        "           |.......|",
        "           |.......|",
        "           ---+-----",
        "              #",
        "              #",
        "              ###",
        "                #",
        "------          #",
        "|....|          #       ----------",
        "|....+#######   #       |.....>..|",
        "|.<..|      ############+........|",
        "|....|                  |........|",
        "------                  ----------",
    };

    private static final String[] level2 = new String[] {
        "----------------------------",
        "|.....................>....|",
        "|..........................|     -----",
        "|..........................|     |...|",
        "|..........................+#####+.<.|",
        "|..........................|     |...|",
        "|..........................|     -----",
        "----------------------------",
    };

    private static final String[] level3 = new String[] {
        "-----",
        "|...|",
        "|...----",
        "|......|",
        "|..<...|",
        "|......--------",
        "|.............|",
        "-----.........|",
        "    |.........|",
        "    -----.....|",
        "        |.....|",
        "        |.....|",
        "        -------",
    };

    public static final void main(String[] args)
        throws CoreException
    {
        AsciiTerm display = new AsciiTerm();

        Level lvl = new Level("Top", level1);
        display.setLevel(lvl);

        Level l2 = new Level("Middle", level2);
        lvl.addNextLevel(l2);

        Level l3 = new Level("Bottom", level3);
        l2.addNextLevel(l3);

        Character ch = new Character("me", 10, 10, 10);
        display.setCharacter(ch);

        try {
            display.loop();
        } finally {
            display.close();
        }
    }
}
