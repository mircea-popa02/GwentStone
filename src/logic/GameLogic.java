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
            int roundEnd = 0;
            int givenMana = 1;

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
                        roundEnd++;
                        if (roundEnd == 2) {
                            playerOneHero.mana += givenMana;
                            playerTwoHero.mana += givenMana;
                            if (givenMana < 10) {
                                givenMana++;
                            }
                            roundEnd = 0;
                            // start of new round. adding first card from deck to hand
                            if (playerOneDeck.cardArrayList.size() != 0) {
                                playerOneHand.cardArrayList.add(playerOneDeck.cardArrayList.get(0));
                                playerOneDeck.cardArrayList.remove(0);
                            }
                            if (playerTwoDeck.cardArrayList.size() != 0) {
                                playerTwoHand.cardArrayList.add(playerTwoDeck.cardArrayList.get(0));
                                playerTwoDeck.cardArrayList.remove(0);
                            }
                        }
                        if (turn == 1) {
                            turn = 2;
                        } else if (turn == 2) {
                            turn = 1;
                        }
                        jsonNode = objectMapper.createObjectNode();
                        jsonNode.put("command", "endPlayerTurn");
                        output.add(jsonNode);
                        break;

                    case "placeCard":
                        // Rândurile 0 și 1 sunt asignate jucătorului 2, iar rândurile 2 și 3 sunt asignate jucătorului 1, conform imaginii de mai jos. Rândurile din față vor fi reprezentate de rândurile 1 și 2, iar rândurile din spate vor fi 0 si 3 (jucătorii vor fi așezați față în față). Totodată, eroii jucătorilor vor avea un loc special în care vor fi așezați de la începutul jocului.
                        // check if card is environment type and throw error otherwise
                        
                        // not enough mana

                        // cannot place card on table since row is full
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
