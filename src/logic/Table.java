package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class Table {
    ArrayList<ArrayList<GenericCard>> cardsOnTable;

    ObjectNode getCardsOnTable(ArrayList<ArrayList<GenericCard>> cardsOnTable, ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        JsonNode jsonNodeCopy = objectMapper.createObjectNode();
        String json;

//        jsonNode.put("command", "getCardsOnTable");
//        json = objectMapper.writeValueAsString(cardsOnTable);
//        jsonNodeCopy = objectMapper.readTree(json);
//        jsonNode.put("output", jsonNodeCopy);

//        for (int i = 0; i < cardsOnTable.size(); i++) {
//            for (int j = 0; j < cardsOnTable.get(i).size(); j++) {
//                json = objectMapper.writeValueAsString(cardsOnTable.get(i).get(j));
//                jsonNodeCopy = objectMapper.readTree(json);
//                jsonNode.put("output",jsonNodeCopy);
//            }
//        }
        return jsonNode;
    }
}
