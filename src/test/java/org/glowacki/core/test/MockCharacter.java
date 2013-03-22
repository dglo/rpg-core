package org.glowacki.core.test;

import org.glowacki.core.CoreException;
import org.glowacki.core.Direction;
import org.glowacki.core.ICharacter;
import org.glowacki.core.Level;

public class MockCharacter
    implements ICharacter
{
    private String name;

    public MockCharacter(String name)
    {
        this.name = name;
    }

    public Level getLevel()
    {
        throw new Error("Unimplemented");
    }

    public String getName()
    {
        return name;
    }

    public int getX()
    {
        throw new Error("Unimplemented");
    }

    public int getY()
    {
        throw new Error("Unimplemented");
    }

    public boolean isPlayer()
    {
        throw new Error("Unimplemented");
    }

    public int move(Direction dir)
        throws CoreException
    {
        throw new Error("Unimplemented");
    }

    public void setLevel(Level l)
    {
        throw new Error("Unimplemented");
    }

    public void setPosition(int x, int y)
    {
        throw new Error("Unimplemented");
    }

    public void takeTurn()
    {
        throw new Error("Unimplemented");
    }
}