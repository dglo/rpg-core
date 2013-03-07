package org.glowacki.core.ascii;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;

class AsciiTerm
{
    private static final int maxX = 20;
    private static final int maxY = 10;

    private Screen screen;
    private boolean running;

    private int x = 0;
    private int y = 0;
    private String errMsg;

    enum Direction { LEFT, UP, RIGHT, DOWN };

    AsciiTerm()
    {
        //Terminal terminal = TerminalFacade.createTerminal();
        //terminal.enterPrivateMode();
        screen = TerminalFacade.createScreen();
        screen.startScreen();
    }

    public void close()
    {
        screen.stopScreen();
        //terminal.exitPrivateMode();
    }

    private void drawScreen()
    {
        screen.clear();

        if (errMsg != null) {
            screen.putString(0, 23, errMsg, Terminal.Color.RED,
                             Terminal.Color.BLACK);
        }

        screen.putString(x + 20, y + 5, "*", Terminal.Color.GREEN,
                             Terminal.Color.BLACK);
    }

    private boolean handleInput(Key key)
    {
        switch (key.getKind()) {
        case NormalKey:
            switch (key.getCharacter()) {
            case 'q':
                running = false;
                break;
            case 'h':
                move(Direction.LEFT);
                break;
            case 'l':
                move(Direction.RIGHT);
                break;
            case 'k':
                move(Direction.UP);
                break;
            case 'j':
                move(Direction.DOWN);
                break;
            default:
                errMsg = "Unknown key " + key;
                break;
            }
            break;
        case ArrowLeft:
            move(Direction.LEFT);
            break;
        case ArrowRight:
            move(Direction.RIGHT);
            break;
        case ArrowUp:
            move(Direction.UP);
            break;
        case ArrowDown:
            move(Direction.DOWN);
            break;
        default:
            errMsg = "Unknown key " + key;
            break;
        }

        return true;
    }

    public void loop()
    {
        running = true;
        while (running) {
            boolean changed = false;

            Key key = screen.readInput();
            if (key != null) {
                errMsg = null;
                changed = handleInput(key);
            }

            if (changed || screen.resizePending()) {
                drawScreen();
                screen.refresh();
            }
        }
    }

    private void move(Direction dir)
    {
        switch (dir) {
        case LEFT:
            x -= 1;
            if (x < 0) {
                x += maxX;
            }
            break;
        case RIGHT:
            x += 1;
            if (x >= maxX) {
                x -= maxX;
            }
            break;
        case UP:
            y -= 1;
            if (y < 0) {
                y += maxY;
            }
            break;
        case DOWN:
            y += 1;
            if (y >= maxY) {
                y -= maxY;
            }
            break;
        }
    }
}

public class Runner
{
    public static final void main(String[] args)
    {
        AsciiTerm display = new AsciiTerm();
        try {
            display.loop();
        } finally {
            display.close();
        }
    }
}
