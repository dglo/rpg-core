package org.glowacki.core.ascii;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.glowacki.core.ComputerCharacter;
import org.glowacki.core.CoreException;
import org.glowacki.core.Direction;
import org.glowacki.core.ICharacter;
import org.glowacki.core.Level;
import org.glowacki.core.Map;
import org.glowacki.core.MapCharRepresentation;
import org.glowacki.core.MapPoint;
import org.glowacki.core.PlayerCharacter;
import org.glowacki.core.Terrain;

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

    public void close()
    {
        screen.stopScreen();
        //terminal.exitPrivateMode();
    }

    public void drawScreen(char[][] map)
    {
        if (!changed && !screen.resizePending()) {
            return;
        }

        screen.clear();

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

/**
 * ASCII view.
 */
class AsciiView
{
    private AsciiTerm display;

    AsciiView(AsciiTerm display)
    {
        this.display = display;
    }

    private char[][] buildMap(PlayerCharacter player)
    {
        Level level = player.getLevel();

        final int maxX = level.getMaxX();
        final int maxY = level.getMaxY();

        char[][] map = new char[maxY + 1][maxX + 1];

        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                char ch;
                try {
                    if (level.isOccupied(x, y)) {
                        ch = 'X';
                    } else {
                        Terrain t = level.getTerrain(x, y);
                        ch = MapCharRepresentation.getCharacter(t);
                    }
                } catch (CoreException ce) {
                    ce.printStackTrace();
                    ch = '?';
                }

                map[y][x] = ch;
            }
        }

        map[player.getY()][player.getX()] = '*';

        return map;
    }

    void close()
    {
        display.close();
    }

    void drawScreen(PlayerCharacter ch)
    {
        char[][] map = buildMap(ch);

        display.drawScreen(map);
    }

    public void logError(String msg)
    {
        display.logError(msg);
    }

    void markChanged()
    {
        display.markChanged();
    }
}

/**
 * ASCII controller .
 */
class AsciiController
{
    private AsciiView view;
    private AsciiTerm terminal;

    private List<PlayerCharacter> players = new ArrayList<PlayerCharacter>();

    private boolean running;

    AsciiController()
    {
        this.terminal = new AsciiTerm();
        this.view = new AsciiView(terminal);
    }

    public void addPlayer(PlayerCharacter ch)
    {
        players.add(ch);
    }

    public void close()
    {
        view.close();
    }

    private MapPoint findGoal(Map map, Direction dir)
        throws CoreException
    {
        Terrain t;
        if (dir == Direction.CLIMB) {
            t = Terrain.UPSTAIRS;
        } else if (dir == Direction.DESCEND) {
            t = Terrain.DOWNSTAIRS;
        } else {
            throw new CoreException("Bad path direction " + dir);
        }

        return map.find(t);
    }

    public int handleInput(ICharacter ch)
    {
        Key key = terminal.readInput();
        if (key != null) {
            return processKey(key, ch);
        }

        if (ch.hasPath()) {
            try {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    // ignore interrupts
                }
                return ch.movePath();
            } catch (CoreException ce) {
                view.logError(ce.getMessage());
                return -1;
            }
        }

        return -1;
    }

    void loop()
    {
        running = true;

        view.markChanged();

        while (running) {
            HashMap<ICharacter, ICharacter> npcs =
                new HashMap<ICharacter, ICharacter>();
            for (PlayerCharacter ch : players) {
                view.drawScreen(ch);
                int turns = handleInput(ch);
                while (turns > 0) {
                    for (ICharacter npc : ch.getLevel().getCharacters()) {
                        if (!npc.isPlayer()) {
                            npcs.put(npc, npc);
                        }
                    }

                    turns--;
                }
            }

            for (ICharacter ch : npcs.keySet()) {
                ch.takeTurn();
            }
        }
    }

    private int processKey(Key key, ICharacter player)
    {
        int turns = -1;
        if (key.getKind() == Key.Kind.NormalKey) {
            switch (key.getCharacter()) {
            case '<':
                try {
                    turns = player.move(Direction.CLIMB);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                    break;
                }

                break;
            case '>':
                try {
                    turns = player.move(Direction.DESCEND);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                    break;
                }

                break;
            case 'D':
                try {
                    MapPoint goal = findGoal(player.getLevel().getMap(),
                                             Direction.DESCEND);
                    player.buildPath(goal);
                } catch (CoreException ce) {
                    view.logError(ce.getMessage());
                }
                turns = -1;
                break;
            case 'h':
                try {
                    turns = player.move(Direction.LEFT);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'i':
                try {
                    turns = player.move(Direction.RIGHT_UP);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'j':
                try {
                    turns = player.move(Direction.DOWN);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'k':
                try {
                    turns = player.move(Direction.UP);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'l':
                try {
                    turns = player.move(Direction.RIGHT);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'm':
                try {
                    turns = player.move(Direction.RIGHT_DOWN);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'n':
                try {
                    turns = player.move(Direction.LEFT_DOWN);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            case 'q':
                running = false;
                break;
            case 'U':
                try {
                    MapPoint goal = findGoal(player.getLevel().getMap(),
                                             Direction.CLIMB);
                    player.buildPath(goal);
                } catch (CoreException ce) {
                    view.logError(ce.getMessage());
                }
                turns = -1;
                break;
            case 'u':
                try {
                    turns = player.move(Direction.LEFT_UP);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
                break;
            default:
                turns = -1;
                view.logError("Unknown key " + key);
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
                view.logError("Unknown key " + key);
                dir = Direction.UNKNOWN;
                break;
            }

            if (dir == Direction.UNKNOWN) {
                turns = -1;
            } else {
                try {
                    turns = player.move(dir);
                } catch (CoreException ce) {
                    turns = -1;
                    view.logError(ce.getMessage());
                }
            }
        }

        view.markChanged();

        return turns;
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

    private AsciiController controller;

    Runner(long seed)
        throws CoreException
    {
        Random random = new Random(seed);

        int maxNPCs = 3;

        Level lvl = new Level("Top", new Map(LEVEL_1));
        populate(lvl, random, maxNPCs);

        Level l2 = new Level("Middle", new Map(LEVEL_2));
        populate(l2, random, maxNPCs + 1);

        lvl.addNextLevel(l2);

        Level l3 = new Level("Bottom", new Map(LEVEL_3));
        populate(l3, random, maxNPCs + 2);

        l2.addNextLevel(l3);

        controller = new AsciiController();

        PlayerCharacter ch = new PlayerCharacter("me", 10, 10, 10);
        controller.addPlayer(ch);

        lvl.enterDown(ch);
    }

    private void populate(Level lvl, Random random, int max)
        throws CoreException
    {
        for (int i = 0; i < max; i++) {
            ComputerCharacter ch = new ComputerCharacter(6, 6, 6,
                                                         random.nextLong());
            ch.setLevel(lvl);
        }
    }

    private void run()
    {
        try {
            controller.loop();
        } finally {
            controller.close();
        }
    }

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
        Runner runner = new Runner(1234L);
        runner.run();
    }
}
