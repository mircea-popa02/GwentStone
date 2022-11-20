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
        ArrayList<GameInput> games;
        games = inputData.getGames();


        int deckNumOne;
        int deckNumTwo;

        // TODO add custom class for minion and environment class with a flag for each one used for output .json
        ArrayList<CardInput> chosenDeckPlayerOne;
        ArrayList<CardInput> chosenDeckPlayerTwo;

        ArrayList<CardInput> handPlayerOne = new ArrayList<>();
        ArrayList<CardInput> handPlayerTwo = new ArrayList<>();

        // TODO add custom class for hero card
        CardInput heroPlayerOne;
        CardInput heroPlayerTwo;

        ArrayList<ArrayList<CardInput>> tablePlayerOne;
        ArrayList<ArrayList<CardInput>> tablePlayerTwo;

        String currentCommand;

        // looping through games
        for (int i = 0; i < games.size(); i++) {
            // choosing the card deck
            deckNumOne = games.get(i).getStartGame().getPlayerOneDeckIdx();
            deckNumTwo = games.get(i).getStartGame().getPlayerTwoDeckIdx();

            // adding chosen cards to deck
            chosenDeckPlayerOne = inputData.getPlayerOneDecks().getDecks().get(deckNumOne);
            chosenDeckPlayerTwo = inputData.getPlayerTwoDecks().getDecks().get(deckNumTwo);

            // shuffling chosen deck
            Collections.shuffle(chosenDeckPlayerOne, new Random(games.get(i).getStartGame().getShuffleSeed()));
            Collections.shuffle(chosenDeckPlayerTwo, new Random(games.get(i).getStartGame().getShuffleSeed()));

            // adding first card from deck to hand and removing it from deck
            handPlayerOne.add(chosenDeckPlayerOne.get(0));
            chosenDeckPlayerOne.remove(0);
            handPlayerTwo.add(chosenDeckPlayerTwo.get(0));
            chosenDeckPlayerTwo.remove(0);

            // setting heroes
            heroPlayerOne = games.get(i).getStartGame().getPlayerOneHero();
            heroPlayerTwo = games.get(i).getStartGame().getPlayerTwoHero();

            // setting first turn
            int turn = games.get(i).getStartGame().getStartingPlayer();

            // cycling through actions
            for (int j = 0; j < games.get(i).getActions().size(); j++) {
                currentCommand = games.get(i).getActions().get(j).getCommand();
                if (currentCommand.equals("getPlayerDeck")) {
                    if (games.get(i).getActions().get(j).getPlayerIdx() == 1) {
                        writeDeckOutput(objectMapper, output, games, chosenDeckPlayerOne, i, j);
                    } else if (games.get(i).getActions().get(j).getPlayerIdx() == 2) {
                        writeDeckOutput(objectMapper, output, games, chosenDeckPlayerTwo, i, j);
                    }
                }

                if (currentCommand.equals("getPlayerHero")) {
                    if (games.get(i).getActions().get(j).getPlayerIdx() == 1) {
                        writePlayerHero(objectMapper, output, games, heroPlayerOne, i, j);
                    } else if (games.get(i).getActions().get(j).getPlayerIdx() == 2) {
                        writePlayerHero(objectMapper, output, games, heroPlayerTwo, i, j);
                    }
                }

                if (currentCommand.equals("getPlayerTurn")) {
                    ObjectNode jsonNode = objectMapper.createObjectNode();
                    jsonNode.put("command", "getPlayerTurn");
                    jsonNode.put("output", turn);
                    output.add(jsonNode);
                }

                if (currentCommand.equals("endPlayerTurn")) {
                    // TODO check if round is over and increment mana accordingly
                    if (turn == 1) {
                        turn = 2;
                    } else if (turn == 2) {
                        turn = 1;
                    }
                }

                if (currentCommand.equals("placeCard")) {
                    // checking whose turn is it
                    if (turn == 1) {
                        int handIndex = games.get(i).getActions().get(j).getHandIdx();

                    } else if (turn == 2) {
                        // code
                    }
                }


            }
        }

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
