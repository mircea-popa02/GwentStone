package logic;

import java.util.ArrayList;

public class EnvironmentCard extends GenericCard{
    public int mana;
    public String description;
    public ArrayList<String> colors;
    public String name;

    public EnvironmentCard(int mana, String description, ArrayList<String> colors, String name) {
        this.mana = mana;
        this.description = description;
        this.colors = colors;
        this.name = name;
    }
}