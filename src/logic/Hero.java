package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class Hero extends GenericCard{
    public int mana;
    public String description;
    public ArrayList<String> colors;
    public String name;
    public int health;
    int playerId;

    public Hero(CardInput card, int playerId) {
        this.mana = card.getMana();
        this.description = card.getDescription();
        this.colors = card.getColors();
        this.name = card.getName();
        this.playerId = playerId;
        this.health = 30;
    }


}
