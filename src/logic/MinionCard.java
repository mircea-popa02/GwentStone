package logic;


import java.util.ArrayList;

public class MinionCard extends GenericCard {
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

    @Override
    public void attackCard(int damage) {
        this.health -= damage;
    }

    @Override
    public void weakness(int atk) {
        System.out.println(attackDamage + " before");
        this.attackDamage -= atk;
        System.out.println(attackDamage + " after");
    }
}