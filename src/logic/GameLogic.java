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

        ArrayList<ArrayList<GenericCard>> table = new ArrayList<>(5);

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
                        break;

//                    case "placeCard":
//                        // Rândurile 0 și 1 sunt asignate jucătorului 2, iar rândurile 2 și 3 sunt asignate jucătorului 1, conform imaginii de mai jos. Rândurile din față vor fi reprezentate de rândurile 1 și 2, iar rândurile din spate vor fi 0 si 3 (jucătorii vor fi așezați față în față). Totodată, eroii jucătorilor vor avea un loc special în care vor fi așezați de la începutul jocului.
//                        // check if card is environment type and throw error otherwise
//                        int cardIndex = inputData.getGames().get(i).getActions().get(j).getHandIdx();
//                        if (turn == 1) {
//                            // checking if card is of type environment
//                            if (isGenericEnvironment(playerOneHand.cardArrayList.get(cardIndex)) == false) {
//                                // error not enough mana
//                                if (playerOneHand.cardArrayList.get(cardIndex).mana > playerOneHero.mana) {
//                                    jsonNode = objectMapper.createObjectNode();
//                                    jsonNode.put("command", "placeCard");
//                                    jsonNode.put("handIdx", cardIndex);
//                                    jsonNode.put("error", "Not enough mana to place card on table.");
//                                    output.add(jsonNode);
//                                } else {
//                                    if (isCardOnFrontRow(playerOneHand.cardArrayList.get(cardIndex)) == true) {
//                                        // check if there is room for another card else throw error
//                                        if (table.get(2).size() <= 4) {
//                                            // add card to row
//                                            table.get(2).add(playerOneHand.cardArrayList.get(cardIndex));
//                                            playerOneHand.cardArrayList.remove(cardIndex);
//                                        } else {
//                                            jsonNode = objectMapper.createObjectNode();
//                                            jsonNode.put("command", "placeCard");
//                                            jsonNode.put("handIdx", cardIndex);
//                                            jsonNode.put("error", "Cannot place card on table since row is full.");
//                                            output.add(jsonNode);
//                                        }
//                                    }
//                                    if (isCardOnFrontRow(playerOneHand.cardArrayList.get(cardIndex)) == true) {
//                                        if (table.get(3).size() <= 4) {
//                                            table.get(3).add(playerOneHand.cardArrayList.get(cardIndex));
//                                            playerOneHand.cardArrayList.remove(cardIndex);
//                                        } else {
//                                            jsonNode = objectMapper.createObjectNode();
//                                            jsonNode.put("command", "placeCard");
//                                            jsonNode.put("handIdx", cardIndex);
//                                            jsonNode.put("error", "Cannot place card on table since row is full.");
//                                            output.add(jsonNode);
//                                        }
//                                    }
//                                }
//                            } else {
//                                // error card is env
//                                jsonNode = objectMapper.createObjectNode();
//                                jsonNode.put("command", "placeCard");
//                                jsonNode.put("handIdx", cardIndex);
//                                jsonNode.put("error", "Cannot place environment card on table.");
//                                output.add(jsonNode);
//                            }
//                        }
//                        break;

                    case "getCardsInHand":
                        if (inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                            output.add(playerOneHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                            break;
                        }
                        output.add(playerTwoHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                        break;

                    case "getPlayerMana":
                        int playerId = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                        jsonNode = objectMapper.createObjectNode();
                        jsonNode.put("command", "getPlayerMana");
                        if (playerId == 1) {
                            jsonNode.put("output", playerOneHero.mana;
                        } else {
                            jsonNode.put("output", playerTwoHero.mana);
                        }
                        jsonNode.put("playerIdx", playerId);
                        output.add(jsonNode);
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

    public boolean isGenericEnvironment(GenericCard card) {
        if (card.name ==  null) {
            return false;
        }
        if (card.name.equals("Firestorm") || card.name.equals("Winterfell") || card.name.equals("Heart Hound")) {
            return true;
        }
        return false;
    }

    public boolean isCardOnFrontRow(GenericCard card) {
        if (card.name ==  null) {
            return false;
        }
        if (card.name.equals("The Ripper") || card.name.equals("Miraj") || card.name.equals("Goliath") || card.name.equals("Warden")) {
            return true;
        }
        return false;
    }

    public boolean isCardOnBackRow(GenericCard card) {
        if (card.name ==  null) {
            return false;
        }
        if (card.name.equals("Sentinel") || card.name.equals("Berserker") || card.name.equals("The Cursed One") || card.name.equals("Disciple")) {
            return true;
        }
        return false;
    }
}
