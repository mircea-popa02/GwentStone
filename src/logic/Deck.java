package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;
import logic.GameLogic;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class Deck {
    ArrayList<GenericCard> cardArrayList = new ArrayList<>();
    int playerId;

    public Deck(int playerId) {
        this.playerId = playerId;
    }

    void addToDeck(ArrayList<CardInput> cardInput) {
        for (int i = 0; i < cardInput.size(); i++) {
            GenericCard card;
            if (isEnvironment(cardInput.get(i)) == false) {
                card = new MinionCard(cardInput.get(i).getMana(), cardInput.get(i).getHealth(), cardInput.get(i).getAttackDamage(), cardInput.get(i).getDescription(), cardInput.get(i).getColors(), cardInput.get(i).getName());
                cardArrayList.add(card);
            } else {
                card = new EnvironmentCard(cardInput.get(i).getMana(), cardInput.get(i).getDescription(), cardInput.get(i).getColors(), cardInput.get(i).getName());
                cardArrayList.add(card);
            }
        }
    }

    boolean isEnvironment(CardInput card) {
        if (card.getName().equals("Firestorm") || card.getName().equals("Winterfell") || card.getName().equals("Heart Hound")) {
            return true;
        }
        return false;
    }

    public ObjectNode writeDeckToOutput(ObjectMapper objectMapper, String instruction) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", instruction);
        jsonNode.put("playerIdx", playerId);
        String json = objectMapper.writeValueAsString(cardArrayList);
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        jsonNode.put("output", jsonNodeCopy);
        return jsonNode;
    }
}
