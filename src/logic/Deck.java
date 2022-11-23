package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import java.util.ArrayList;


public class Deck {
    public ArrayList<GenericCard> cardArrayList = new ArrayList<>();
    int playerId;

    void addToDeck(ArrayList<CardInput> cardInput) {
        for (CardInput input : cardInput) {
            GenericCard card;
            if (!isEnvironment(input)) {
                card = new MinionCard(input.getMana(), input.getHealth(), input.getAttackDamage(), input.getDescription(), input.getColors(), input.getName());
                cardArrayList.add(card);
            } else {
                card = new EnvironmentCard(input.getMana(), input.getDescription(), input.getColors(), input.getName());
                cardArrayList.add(card);
            }
        }
    }

    boolean isEnvironment(CardInput input) {
        String name = input.getName();
        return name.equals("Firestorm") || name.equals("Winterfell") || name.equals("Heart Hound");
    }

    boolean isEnvironment(ObjectMapper objectMapper, GenericCard card) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(card);
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode.get("name").toString().equals("\"Firestorm\"") || jsonNode.get("name").toString().equals("\"Winterfell\"") || jsonNode.get("name").toString().equals("\"Heart Hound\"");
    }

    int getMana(int index, ObjectMapper objectMapper) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(cardArrayList.get(index));
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        return Integer.parseInt(jsonNodeCopy.get("mana").toString());
    }

    String getName(int index, ObjectMapper objectMapper) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(cardArrayList.get(index));
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        return jsonNodeCopy.get("name").toString();
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

    public ObjectNode writeEnvironmentCardsToOutput(ObjectMapper objectMapper, int playerId) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", "getEnvironmentCardsInHand");
        jsonNode.put("playerIdx", playerId);
        ArrayList<GenericCard> environmentalCards = new ArrayList<>();
        for (GenericCard genericCard : cardArrayList) {
            if (isEnvironment(objectMapper, genericCard)) {
                environmentalCards.add(genericCard);
            }
        }
        String json = objectMapper.writeValueAsString(environmentalCards);
        jsonNode.put("output", objectMapper.readTree(json));
        return  jsonNode;
    }
}