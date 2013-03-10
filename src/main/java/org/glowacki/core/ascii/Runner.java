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
import org.glowacki.core.PlayerCharacter;
import org.glowacki.core.Terrain;
import org.glowacki.core.View;

/**
 * ASCII terminal interface
 */
class AsciiTerm
{
    private Screen screen;
    private int maxRows;
    private int maxCols;

    private String errMsg;
    private boolean changed;

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

    private char[][] buildMap(MovableCharacter player)
    {
        Level level = player.getLevel();

        final int maxX = level.getMap().getMaxX();
        final int maxY = level.getMap().getMaxY();

        char[][] map = new char[maxY + 1][maxX + 1];

        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                char ch;
                try {
                    ch = Terrain.getCharacter(level.getMap().get(x, y));
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

    public void drawScreen(MovableCharacter player)
    {
        if (!changed && !screen.resizePending()) {
            return;
        }

        screen.clear();

        char[][] map = buildMap(player);

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

        if (errMsg != null) {
            screen.putString(0, 23, errMsg, Terminal.Color.RED,
                             Terminal.Color.BLACK);
        }

        screen.refresh();
    }

    public void logError(String msg)
    {
        errMsg = msg;
    }

    public void markChanged()
    {
        changed = true;
    }

    public Key readInput()
    {
        return screen.readInput();
    }
}

class AsciiView
    implements View
{
    private AsciiTerm display;
    private MovableCharacter player;

    private boolean running;

    AsciiView()
    {
        display = new AsciiTerm();
    }

    void close()
    {
        display.close();
    }

    public void handleInput()
    {
        Key key = display.readInput();
        if (key != null) {
            processKey(key);
        }
    }

    void loop()
    {
        running = true;

        display.markChanged();
        display.drawScreen(player);

        while (running) {
            for (MovableCharacter ch : player.getLevel().getCharacters()) {
                ch.takeTurn();
            }

            display.drawScreen(player);
        }
    }

    private void processKey(Key key)
    {
        int turns;
        if (key.getKind() == Key.Kind.NormalKey) {
            switch (key.getCharacter()) {
            case '<':
                try {
                    turns = player.move(Direction.CLIMB);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                    break;
                }

                break;
            case '>':
                try {
                    turns = player.move(Direction.DESCEND);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                    break;
                }

                break;
            case 'h':
                try {
                    turns = player.move(Direction.LEFT);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                }
                break;
            case 'j':
                try {
                    turns = player.move(Direction.DOWN);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                }
                break;
            case 'k':
                try {
                    turns = player.move(Direction.UP);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                }
                break;
            case 'l':
                try {
                    turns = player.move(Direction.RIGHT);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                }
                break;
            case 'q':
                running = false;
                break;
            default:
                display.logError("Unknown key " + key);
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
                display.logError("Unknown key " + key);
                dir = Direction.UNKNOWN;
                break;
            }

            if (dir != Direction.UNKNOWN) {
                try {
                    turns = player.move(dir);
                } catch (CoreException ce) {
                    display.logError(ce.getMessage());
                }
            }
        }

        display.markChanged();
    }

    void setPlayer(MovableCharacter mch)
    {
        player = mch;
    }
}

/**
 * Class which runs the code.
 */
public class Runner
{
    private static final String[] LEVEL_1 = new String[] {
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

    private static final String[] LEVEL_2 = new String[] {
        "----------------------------",
        "|.....................>....|",
        "|..........................|     -----",
        "|..........................|     |...|",
        "|..........................+#####+.<.|",
        "|..........................|     |...|",
        "|..........................|     -----",
        "----------------------------",
    };

    private static final String[] LEVEL_3 = new String[] {
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

    /**
     * Main method
     *
     * @param args command-line arguments
     *
     * @throws CoreException if there is an unexpected problem
     */
    public static final void main(String[] args)
        throws CoreException
    {
        AsciiView view = new AsciiView();

        Level lvl = new Level("Top", LEVEL_1);

        Level l2 = new Level("Middle", LEVEL_2);
        lvl.addNextLevel(l2);

        Level l3 = new Level("Bottom", LEVEL_3);
        l2.addNextLevel(l3);

        PlayerCharacter ch = new PlayerCharacter(view, "me", 10, 10, 10);
        MovableCharacter mch = lvl.enterDown(ch);
        view.setPlayer(mch);

        try {
            view.loop();
        } finally {
            view.close();
        }
    }
}
