package org.glowacki.core;

public class PlayerCharacter
    extends Character
{
    private View view;

    /**
     * Create a character.
     *
     * @param view view
     * @param name name
     * @param str strength
     * @param dex dexterity
     * @param spd speed
     */
    public PlayerCharacter(View view, String name, int str, int dex, int spd)
    {
        super(name, str, dex, spd);

        this.view = view;
    }

    /**
     * Perform this turn's action(s).
     */
    public void takeTurn()
    {
        view.handleInput();
    }
}
