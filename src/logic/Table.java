package logic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Table {
    ObjectNode getCardsOnTable(ArrayList<ArrayList<GenericCard>> cardsOnTable, ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("command", "getCardsOnTable");
        jsonObject.set(
                "output", objectMapper.readTree(
                        objectMapper.writeValueAsString(cardsOnTable)
                )
        );
        return jsonObject;
    }

    ObjectNode getCardAtPosition(ObjectMapper objectMapper, int x, int y, ArrayList<ArrayList<GenericCard>> table) throws JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        GenericCard card = table.get(x).get(y);
        objectNode.put("command", "getCardAtPosition");
        objectNode.put("x", x);
        objectNode.put("y", y);
        objectNode.set("output", objectMapper.readTree(
                objectMapper.writeValueAsString(card))
        );
        return objectNode;
    }
}