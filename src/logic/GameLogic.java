package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameLogic {
    Input inputData;
    ArrayNode output;
    ObjectMapper objectMapper;

    public GameLogic(Input inputData, ArrayNode output, ObjectMapper objectMapper) {
        this.inputData = inputData;
        this.output = output;
        this.objectMapper = objectMapper;
    }

    public void playGames() throws JsonProcessingException {
        int rounds = inputData.getGames().size();
        String currentCommand;

        Deck playerOneDeck = new Deck(1);
        Deck playerTwoDeck = new Deck(2);

        Deck playerOneHand = new Deck(1);
        Deck playerTwoHand = new Deck(2);

        Table table;

        Hero playerOneHero;
        Hero playerTwoHero;
        for (int i = 0; i < rounds; i++) {
            // choosing a deck
            int deckNumOne = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
            int deckNumTwo = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();

            // add cards to deck for each player
            playerOneDeck.addToDeck(inputData.getPlayerOneDecks().getDecks().get(deckNumOne));
            playerTwoDeck.addToDeck(inputData.getPlayerTwoDecks().getDecks().get(deckNumTwo));

            // setting heroes for each players
            playerOneHero = new Hero(inputData.getGames().get(i).getStartGame().getPlayerOneHero(), 1);
            playerTwoHero = new Hero(inputData.getGames().get(i).getStartGame().getPlayerTwoHero(), 2);

            int shuffleSeed = inputData.getGames().get(i).getStartGame().getShuffleSeed();

            // shuffling chosen deck
            Collections.shuffle(playerOneDeck.cardArrayList, new Random(shuffleSeed));
            Collections.shuffle(playerTwoDeck.cardArrayList, new Random(shuffleSeed));

            // adding first card from deck to hand and removing it from deck
            playerOneHand.cardArrayList.add(playerOneDeck.cardArrayList.get(0));
            playerOneDeck.cardArrayList.remove(0);
            playerTwoHand.cardArrayList.add(playerTwoDeck.cardArrayList.get(0));
            playerTwoDeck.cardArrayList.remove(0);

            // setting first turn
            int turn = inputData.getGames().get(i).getStartGame().getStartingPlayer();

            // cycling through actions
            for (int j = 0; j < inputData.getGames().get(i).getActions().size(); j++) {
                currentCommand = inputData.getGames().get(i).getActions().get(j).getCommand();
                switch (currentCommand) {
                    case "getPlayerDeck":
                        if (inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                            output.add(playerOneDeck.writeDeckToOutput(objectMapper, "getPlayerDeck"));
                            break;
                        }
                        output.add(playerTwoDeck.writeDeckToOutput(objectMapper, "getPlayerDeck"));
                        break;

                    case "getPlayerHero":
                        if (inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                            output.add(writeHeroToOutput(objectMapper, inputData.getGames().get(i).getActions().get(j).getPlayerIdx(), playerOneHero));
                            break;
                        }
                        output.add(writeHeroToOutput(objectMapper, inputData.getGames().get(i).getActions().get(j).getPlayerIdx(), playerTwoHero));
                        break;

                    case "getPlayerTurn":
                        ObjectNode jsonNode = objectMapper.createObjectNode();
                        jsonNode.put("command", "getPlayerTurn");
                        jsonNode.put("output", turn);
                        output.add(jsonNode);
                        break;
                    case "endPlayerTurn":
                        // TODO check if round is over and increment mana accordingly
                        if (turn == 1) {
                            turn = 2;
                        } else if (turn == 2) {
                            turn = 1;
                        }
                        break;

                    case "placeCard":
                        break;
                    case "getCardsInHand":
                        if (inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                            output.add(playerOneHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                            break;
                        }
                        output.add(playerTwoHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                        break;
                }
            }
        }
    }

    public ObjectNode writeHeroToOutput(ObjectMapper objectMapper, int playerId, Hero hero) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", "getPlayerHero");
        jsonNode.put("playerIdx", playerId);
        String json = objectMapper.writeValueAsString(hero);
        JsonNode jsonNodeCopy = objectMapper.readTree(json);
        jsonNode.put("output", jsonNodeCopy);
        return jsonNode;
    }
}
