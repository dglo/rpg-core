package org.glowacki.core;

class CharacterException
    extends CoreException
{
    CharacterException(String msg)
    {
        super(msg);
    }
}

public class PlayerCharacter
    extends BaseCharacter
{
    private String name;

    private Level level;

    public PlayerCharacter(String name, int str,
                           int dex, int spd)
    {
        super(str, dex, spd);

        this.name = name;
    }

    public Level getLevel()
    {
        return level;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPlayer()
    {
        return true;
    }

    public int move(Direction dir)
        throws CoreException
    {
        if (dir == Direction.CLIMB) {
            Terrain t = level.get(getX(), getY());
            if (t != Terrain.UPSTAIRS) {
                throw new CharacterException("You cannot climb here");
            }

            Level prevLevel = level.getPreviousLevel();
            if (prevLevel == null) {
                throw new CharacterException("You cannot exit here");
            }

            level.exit(this);
            prevLevel.enterUp(this);

            level = prevLevel;

            return moveInternal(t, false);
        } else if (dir == Direction.DESCEND) {
            Terrain t = level.get(getX(), getY());
            if (t != Terrain.DOWNSTAIRS) {
                throw new CharacterException("You cannot descend here");
            }

            Level nextLevel = level.getNextLevel();
            if (nextLevel == null) {
                throw new CharacterException("You are at the bottom");
            }

            level.exit(this);
            nextLevel.enterDown(this);

            level = nextLevel;

            return moveInternal(t, false);
        } else {
            return move(level, dir);
        }
    }

    public void setLevel(Level l)
    {
        this.level = l;
    }

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        throw new UnimplementedError();
    }

    public String toString()
    {
        return name + super.toString();
    }
}
