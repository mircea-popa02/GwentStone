package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Table {
    ArrayList<ArrayList<GenericCard>> cardsOnTable;

    ObjectNode getCardsOnTable(ArrayList<ArrayList<GenericCard>> cardsOnTable, ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectNode jsonObject = objectMapper.createObjectNode();

        jsonObject.set(
                "output", objectMapper.readTree(
                    objectMapper.writeValueAsString(cardsOnTable)
                )
        );
        return jsonObject;
    }
}
