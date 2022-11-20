package logic;

import fileio.GameInput;

import java.util.ArrayList;

public class MinionCard extends GenericCard{
    public int mana;
    public int health;
    public int attackDamage;
    public String description;
    public ArrayList<String> colors ;
    public String name;

    public MinionCard(int mana, int health, int attackDamage, String description, ArrayList<String> colors, String name) {
        this.mana = mana;
        this.health = health;
        this.attackDamage = attackDamage;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }
}
