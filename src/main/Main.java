package main;

import checker.Checker;

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

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        //TODO add here the entry point to your implementation

        GameLogic gameLogic = new GameLogic(inputData, output, objectMapper);
        gameLogic.playGames();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    private static void writePlayerHero(ObjectMapper objectMapper, ArrayNode output, ArrayList<GameInput> games, CardInput heroPlayerOne, int i, int j) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", "getPlayerHero");
        jsonNode.put("playerIdx", games.get(i).getActions().get(j).getPlayerIdx());
        String json = objectMapper.writeValueAsString(heroPlayerOne);
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        ((ObjectNode) jsonNodeCopy).remove("health");
        ((ObjectNode) jsonNodeCopy).remove("attackDamage");
        // TODO hardcoded hero health
        ((ObjectNode) jsonNodeCopy).put("health", 30);
        jsonNode.put("output", jsonNodeCopy);
        output.add(jsonNode);
    }

    private static void writeDeckOutput(ObjectMapper objectMapper, ArrayNode output, ArrayList<GameInput> games, ArrayList<CardInput> chosenDeckPlayer, int i, int j) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", "getPlayerDeck");
        jsonNode.put("playerIdx", games.get(i).getActions().get(j).getPlayerIdx());
        String json = objectMapper.writeValueAsString(chosenDeckPlayer);
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        for (int k = 0; k < jsonNodeCopy.size(); k++) {
            if (jsonNodeCopy.get(k).get("name").toString().equals("\"Winterfell\"")) {
                ((ObjectNode) jsonNodeCopy.get(k)).remove("health");
                ((ObjectNode) jsonNodeCopy.get(k)).remove("attackDamage");
            }

            if (jsonNodeCopy.get(k).get("name").toString().equals("\"Firestorm\"")) {
                ((ObjectNode) jsonNodeCopy.get(k)).remove("health");
                ((ObjectNode) jsonNodeCopy.get(k)).remove("attackDamage");
            }

            if (jsonNodeCopy.get(k).get("name").toString().equals("\"Heart Hound\"")) {
                ((ObjectNode) jsonNodeCopy.get(k)).remove("health");
                ((ObjectNode) jsonNodeCopy.get(k)).remove("attackDamage");
            }
        }
        jsonNode.put("output", jsonNodeCopy);
        output.add(jsonNode);
    }
}
