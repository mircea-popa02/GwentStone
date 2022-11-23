package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.security.jgss.GSSUtil;
import fileio.Coordinates;
import fileio.Input;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Data
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

        Deck playerOneDeck = new Deck();
        playerOneDeck.playerId = 1;
        Deck playerTwoDeck = new Deck();
        playerTwoDeck.playerId = 2;

        Deck playerOneHand = new Deck();
        playerOneHand.playerId = 1;
        Deck playerTwoHand = new Deck();
        playerTwoHand.playerId = 2;

        Hero playerOneHero;
        Hero playerTwoHero;

        Coordinates coordinates = new Coordinates();




        for (int i = 0; i < rounds; i++) {
            // creating a new game table for every game played
            ArrayList<ArrayList<GenericCard>> table = new ArrayList<>();
            for (int k = 0; k < 4; k++) {
                table.add(new ArrayList<>());
            }

            int[][] frozenPlayerOne = new int[4][5];

            for (int k = 0; k < 2; k++) {
                for (int p = 0; p < 5; p++) {
                    frozenPlayerOne[k][p] = 0;
                }
            }

            int[][] frozenPlayerTwo = new int[4][5];

            for (int k = 0; k < 2; k++) {
                for (int p = 0; p < 5; p++) {
                    frozenPlayerTwo[k][p] = 0;
                }
            }

            int[][] hasAttacked = new int[4][5];
            for (int k = 0; k < 4; k++) {
                for (int p = 0; p < 5; p++) {
                    hasAttacked[k][p] = 0;
                }
            }
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
            int playerId;
            int cardIndex;
            int affectedRow;


            PlayerMana playerMana = new PlayerMana();
            Table tableInstance = new Table();
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
                        // check if round is over and add mana accordingly
                        if (roundEnd == 2) {


                            for (int k = 0; k < 4; k++) {
                                for (int p = 0; p < 5; p++) {
                                    hasAttacked[k][p] = 0;
                                }
                            }

                            if (playerMana.givenMana < 10) {
                                playerMana.givenMana++;
                            }
                            playerMana.playerOneMana += playerMana.givenMana;
                            playerMana.playerTwoMana += playerMana.givenMana;

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
                            // unfreezing player one
                            for (int k = 0; k < 2; k++) {
                                for (int p = 0; p < 5; p++) {
                                    frozenPlayerOne[k][p] = 0;
                                }
                            }
                            turn = 2;
                        } else if (turn == 2) {
                            // unfreezing player two
                            for (int k = 0; k < 2; k++) {
                                for (int p = 0; p < 5; p++) {
                                    frozenPlayerTwo[k][p] = 0;
                                }
                            }
                            turn = 1;
                        }
                        break;

                    case "placeCard":
                        cardIndex = inputData.getGames().get(i).getActions().get(j).getHandIdx();

                        if (turn == 1) {
                            if (throwPlaceCardErrors(playerOneHand, cardIndex)) break;
                            if (playerOneHand.getMana(cardIndex, objectMapper) > playerMana.playerOneMana) {
                                jsonNode = objectMapper.createObjectNode();
                                jsonNode.put("command", "placeCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("error", "Not enough mana to place card on table.");
                                output.add(jsonNode);
                                break;
                            }
                            if (isCardOnFrontRow(playerOneHand.getName(cardIndex, objectMapper))) {
                                if (table.get(2).size() == 5) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("command", "placeCard");
                                    jsonNode.put("handIdx", cardIndex);
                                    jsonNode.put("error", "Cannot place card on table since row is full.");
                                    output.add(jsonNode);
                                    break;
                                }
                                table.get(2).add(playerOneHand.cardArrayList.get(cardIndex));
                                playerMana.playerOneMana -= playerOneHand.getMana(cardIndex, objectMapper);
                                playerOneHand.cardArrayList.remove(cardIndex);

                            } else {
                                if (table.get(3).size() == 5) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("command", "placeCard");
                                    jsonNode.put("handIdx", cardIndex);
                                    jsonNode.put("error", "Cannot place card on table since row is full.");
                                    output.add(jsonNode);
                                    break;
                                }
                                table.get(3).add(playerOneHand.cardArrayList.get(cardIndex));
                                playerMana.playerOneMana -= playerOneHand.getMana(cardIndex, objectMapper);
                                playerOneHand.cardArrayList.remove(cardIndex);

                            }
                        } else {
                            if (throwPlaceCardErrors(playerTwoHand, cardIndex)) break;
                            if (playerTwoHand.getMana(cardIndex, objectMapper) > playerMana.playerTwoMana) {
                                jsonNode = objectMapper.createObjectNode();
                                jsonNode.put("command", "placeCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("error", "Not enough mana to place card on table.");
                                output.add(jsonNode);
                                break;
                            }
                            if (isCardOnFrontRow(playerTwoHand.getName(cardIndex, objectMapper))) {
                                // check row full
                                if (table.get(1).size() == 5) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("command", "placeCard");
                                    jsonNode.put("handIdx", cardIndex);
                                    jsonNode.put("error", "Cannot place card on table since row is full.");
                                    output.add(jsonNode);
                                    break;
                                }
                                table.get(1).add(playerTwoHand.cardArrayList.get(cardIndex));
                                playerMana.playerTwoMana -= playerTwoHand.getMana(cardIndex, objectMapper);
                                playerTwoHand.cardArrayList.remove(cardIndex);

                            } else {
                                if (table.get(0).size() == 5) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("command", "placeCard");
                                    jsonNode.put("handIdx", cardIndex);
                                    jsonNode.put("error", "Cannot place card on table since row is full.");
                                    output.add(jsonNode);
                                    break;
                                }
                                table.get(0).add(playerTwoHand.cardArrayList.get(cardIndex));
                                playerMana.playerTwoMana -= playerTwoHand.getMana(cardIndex, objectMapper);
                                playerTwoHand.cardArrayList.remove(cardIndex);
                            }
                        }
                        break;

                    case "getCardsInHand":
                        if (inputData.getGames().get(i).getActions().get(j).getPlayerIdx() == 1) {
                            output.add(playerOneHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                            break;
                        }
                        output.add(playerTwoHand.writeDeckToOutput(objectMapper, "getCardsInHand"));
                        break;

                    case "getPlayerMana":
                        playerId = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                        jsonNode = objectMapper.createObjectNode();
                        jsonNode.put("command", "getPlayerMana");
                        if (playerId == 1) {
                            jsonNode.put("output", playerMana.playerOneMana);
                        } else {
                            jsonNode.put("output", playerMana.playerTwoMana);
                        }
                        jsonNode.put("playerIdx", playerId);
                        output.add(jsonNode);
                        break;

                    case "getCardsOnTable":
                        output.add(tableInstance.getCardsOnTable(table, objectMapper));
                        break;

                    case "getEnvironmentCardsInHand":
                        playerId = inputData.getGames().get(i).getActions().get(j).getPlayerIdx();
                        if (playerId == 1) {
                            if (playerOneHand.cardArrayList.size() > 0) {
                                output.add(playerOneHand.writeEnvironmentCardsToOutput(objectMapper, playerId));
                            }
                        } else {
                            if (playerTwoHand.cardArrayList.size() > 0) {
                                output.add(playerTwoHand.writeEnvironmentCardsToOutput(objectMapper, playerId));
                            }
                        }
                        break;

                    case "getCardAtPosition":
                        coordinates.setX(inputData.getGames().get(i).getActions().get(j).getX());
                        int x = coordinates.getX();
                        coordinates.setY(inputData.getGames().get(i).getActions().get(j).getY());
                        int y =  coordinates.getY();

                        // TODO error condition may not suffice

                        if (table.get(x).size() < y + 1) {
                            jsonNode = objectMapper.createObjectNode();
                            jsonNode.put("command", "getCardAtPosition");
                            jsonNode.put("x", x);
                            jsonNode.put("y", y);
                            jsonNode.put("output", "No card available at that position.");
                            output.add(jsonNode);
                            break;
                        }
                        output.add(tableInstance.getCardAtPosition(objectMapper, x, y, table));


                        break;

                    case "useEnvironmentCard":
                        cardIndex = inputData.getGames().get(i).getActions().get(j).getHandIdx();
                        affectedRow = inputData.getGames().get(i).getActions().get(j).getAffectedRow();
                        jsonNode = objectMapper.createObjectNode();

                        if (turn == 1) {

                            if (!playerOneHand.isEnvironment(objectMapper, playerOneHand.cardArrayList.get(cardIndex))) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Chosen card is not of type environment.");
                                output.add(jsonNode);
                                break;
                            }
                            if (playerMana.playerOneMana < playerOneHand.getMana(cardIndex, objectMapper)) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Not enough mana to use environment card.");
                                output.add(jsonNode);
                                break;
                            }
                            if (affectedRow >= 2) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Chosen row does not belong to the enemy.");
                                output.add(jsonNode);
                                break;
                            }
                            if (playerOneHand.getName(cardIndex, objectMapper).equals("\"Firestorm\"")) {
                                useFirestormAbility(table, affectedRow);
                                playerMana.playerOneMana -= playerOneHand.getMana(cardIndex, objectMapper);
                                playerOneHand.cardArrayList.remove(cardIndex);
                                break;
                            }
                            if (playerOneHand.getName(cardIndex, objectMapper).equals("\"Winterfell\"")) {
                                useWinterFellAbility(frozenPlayerOne, frozenPlayerTwo, affectedRow);
                                playerMana.playerOneMana -= playerOneHand.getMana(cardIndex, objectMapper);
                                playerOneHand.cardArrayList.remove(cardIndex);
                                break;
                            }

                            if (playerOneHand.getName(cardIndex, objectMapper).equals("\"Heart Hound\"")) {
                                useHeartHoundAbility(affectedRow, table, cardIndex, playerMana, playerOneHand, turn);
                                break;
                            }
                        } else if (turn == 2) {
                            if (!playerTwoHand.isEnvironment(objectMapper, playerTwoHand.cardArrayList.get(cardIndex))) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Chosen card is not of type environment.");
                                output.add(jsonNode);
                                break;
                            }
                            if (playerMana.playerTwoMana < playerTwoHand.getMana(cardIndex, objectMapper)) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Not enough mana to use environment card.");
                                output.add(jsonNode);
                                break;
                            }
                            if (affectedRow < 2) {
                                jsonNode.put("command", "useEnvironmentCard");
                                jsonNode.put("handIdx", cardIndex);
                                jsonNode.put("affectedRow", affectedRow);
                                jsonNode.put("error", "Chosen row does not belong to the enemy.");
                                output.add(jsonNode);
                                break;
                            }
                            if (playerTwoHand.getName(cardIndex, objectMapper).equals("\"Firestorm\"")) {
                                useFirestormAbility(table, affectedRow);
                                playerMana.playerTwoMana -= playerTwoHand.getMana(cardIndex, objectMapper);
                                playerTwoHand.cardArrayList.remove(cardIndex);
                                break;
                            }
                            if (playerTwoHand.getName(cardIndex, objectMapper).equals("\"Winterfell\"")) {
                                useWinterFellAbility(frozenPlayerOne, frozenPlayerTwo, affectedRow);
                                playerMana.playerTwoMana -= playerTwoHand.getMana(cardIndex, objectMapper);
                                playerTwoHand.cardArrayList.remove(cardIndex);
                                break;
                            }
                            if (playerTwoHand.getName(cardIndex, objectMapper).equals("\"Heart Hound\"")) {
                                useHeartHoundAbility(affectedRow, table, cardIndex, playerMana, playerTwoHand, turn);
                                break;
                            }
                        }
                        break;

                    case "getFrozenCardsOnTable":
                        output.add(getFrozenCardsOnTable(table, frozenPlayerOne, frozenPlayerTwo));

                    case "cardUsesAttack":
                        // error handling
                        if (inputData.getGames().get(i).getActions().get(j).getCardAttacked() != null || inputData.getGames().get(i).getActions().get(j).getCardAttacker() != null) {
                            tableCoordinates attacked = new tableCoordinates(inputData.getGames().get(i).getActions().get(j).getCardAttacked());
                            tableCoordinates attacker = new tableCoordinates(inputData.getGames().get(i).getActions().get(j).getCardAttacker());
//                            System.out.println(attacker.x + " x " + attacker.y + " y");
//                            System.out.println(attacked.x + " x " + attacked.y + " y");
                            if (turn == 1) {
                                if (attacked.x >= 2) {
                                    // attacked card does not belong to enemy
                                    attackError(attacked, attacker, "Attacked card does not belong to the enemy.");
                                }
                            } else if (turn == 2) {
                                if (attacked.x < 2) {
                                    // attacked card does not belong to enemy
                                    attackError(attacked, attacker, "Attacked card does not belong to the enemy.");
                                }
                            }

                            // attacker has already attacked
                            if (hasAttacked[attacker.x][attacker.y] == 1) {
                                attackError(attacked, attacker, "Attacker card has already attacked this turn.");
                                break;
                            }
                            // TODO debug
//                            System.out.println(turn);
//                            showFrozen(frozenPlayerOne, frozenPlayerTwo);
                            // attacker card is frozen
                            if (turn == 1) {
                                if (frozenPlayerOne[attacker.x][attacker.y] == 1) {
                                    System.out.println("throw error");
                                    attackError(attacked, attacker, "Attacker card is frozen.");
                                    break;
                                }
                            }

                            if (turn == 2) {
                                if (frozenPlayerTwo[attacker.x][attacker.y] == 1) {
                                    System.out.println("throw error");
                                    attackError(attacked, attacker, "Attacker card is frozen.");
                                    break;
                                }
                            }

                            int sw = 0;
                            // attacked card is not of type tank
                            if (turn == 1) {
                                for (GenericCard card : table.get(1)) {
                                    if (((MinionCard) card).name.equals("Goliath") || ((MinionCard) card).name.equals("Warden")) {
                                        sw = 1;
                                    }
                                }
                            } else if (turn == 2) {
                                for (GenericCard card : table.get(2)) {
                                    if (((MinionCard) card).name.equals("Goliath") || ((MinionCard) card).name.equals("Warden")) {
                                        sw = 1;
                                        break;
                                    }
                                }
                            }

                            if (table.get(attacked.x).size() > attacked.y) {
                                if (sw == 0 && ((MinionCard)table.get(attacked.x).get(attacked.y)).name.equals("Warden")) {
                                    attackError(attacked, attacker, "Attacked card is not of type 'Tank’.");
                                    break;
                                }
                            }

                            if (table.get(attacked.x).size() > attacked.y) {
                                if (sw == 0 && ((MinionCard)table.get(attacked.x).get(attacked.y)).name.equals("Goliath")) {
                                    attackError(attacked, attacker, "Attacked card is not of type 'Tank’.");
                                    break;
                                }
                            }

                            attackCard(attacked, attacker, table, hasAttacked);
                        }
                        break;

                    case "cardUsesAbility":
                        tableCoordinates attacked = new tableCoordinates(inputData.getGames().get(i).getActions().get(j).getCardAttacked());
                        tableCoordinates attacker = new tableCoordinates(inputData.getGames().get(i).getActions().get(j).getCardAttacker());

                        // TODO add errors
                        if (table.get(attacker.x).size() > attacker.y) {
                            if (((MinionCard)table.get(attacker.x).get(attacker.y)).name.equals("The Ripper")) {

                                table.get(attacked.x).get(attacked.y).weakness(2);
                                if (((MinionCard) table.get(attacked.x).get(attacked.y)).attackDamage < 0) {
                                    ((MinionCard) table.get(attacked.x).get(attacked.y)).attackDamage = 0;
                                }
                                break;
                            }

                            if (((MinionCard)table.get(attacker.x).get(attacker.y)).name.equals("Miraj")) {
                                int health;
                                health = ((MinionCard) table.get(attacked.x).get(attacked.y)).health;
                                ((MinionCard) table.get(attacked.x).get(attacked.y)).health = ((MinionCard) table.get(attacker.x).get(attacker.y)).health;
                                ((MinionCard) table.get(attacker.x).get(attacker.y)).health = health;
                                break;
                            }

                            if (((MinionCard)table.get(attacker.x).get(attacker.y)).name.equals("The Cursed One")) {
                                if (((MinionCard) table.get(attacked.x).get(attacked.y)).attackDamage == 0) {
                                    table.get(attacked.x).remove(attacked.y);
                                    break;
                                }
                                int health;
                                health = ((MinionCard) table.get(attacked.x).get(attacked.y)).health;
                                ((MinionCard) table.get(attacked.x).get(attacked.y)).health = ((MinionCard) table.get(attacked.x).get(attacked.y)).attackDamage;
                                ((MinionCard) table.get(attacked.x).get(attacked.y)).attackDamage = health;

                                break;
                            }

                            if (((MinionCard)table.get(attacker.x).get(attacker.y)).name.equals("Disciple")) {
                                ((MinionCard) table.get(attacked.x).get(attacked.y)).health += 2;
                                break;
                            }
                        }

                        break;

                    case "useAttackHero":
                        attacker = new tableCoordinates(inputData.getGames().get(i).getActions().get(j).getCardAttacker());
                        if (table.get(attacker.x).size() > attacker.y) {
                            if (turn == 1) {
                                playerTwoHero.attackHero(((MinionCard) table.get(attacker.x).get(attacker.y)).attackDamage);
                                if (playerTwoHero.health <= 0) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("gameEnded", "Player one killed the enemy hero.");
                                    output.add(jsonNode);
                                    break;
                                }
                            }
                            if (turn == 2) {
                                playerOneHero.attackHero(((MinionCard) table.get(attacker.x).get(attacker.y)).attackDamage);
                                if (playerOneHero.health <= 0) {
                                    jsonNode = objectMapper.createObjectNode();
                                    jsonNode.put("gameEnded", "Player two killed the enemy hero.");
                                    output.add(jsonNode);
                                    break;
                                }
                            }
                        }
                        break;

                    case "useHeroAbility":
                        // TODO add errors
                        affectedRow = inputData.getGames().get(i).getActions().get(j).getAffectedRow();
                        if (turn == 1) {
                            if (playerOneHero.name.equals("Lord Royce")) {
                                int maxAtk = 0;
                                int index = 0;
                                for (int k = 0; k < table.get(affectedRow).size(); k++) {
                                    if (((MinionCard) table.get(affectedRow).get(k)).attackDamage > maxAtk) {
                                        maxAtk = ((MinionCard) table.get(affectedRow).get(k)).attackDamage;
                                        index = k;
                                    }
                                }
                                frozenPlayerTwo[affectedRow][index] = 1;
                                playerMana.playerOneMana -= playerOneHero.mana;
                            }
                            if (playerOneHero.name.equals("Empress Thorina")) {
                                int maxHealth = 0;
                                int index = 0;
                                for (int k = 0; k < table.get(affectedRow).size(); k++) {
                                    if (((MinionCard) table.get(affectedRow).get(k)).health > maxHealth) {
                                        maxHealth = ((MinionCard) table.get(affectedRow).get(k)).health;
                                        index = k;
                                    }
                                }
                                if (table.get(affectedRow).size() > index) {
                                    table.get(affectedRow).remove(index);
                                }
                                playerMana.playerOneMana -= playerOneHero.mana;
                                break;
                            }
                            if (playerOneHero.name.equals("King Mudface")) {
                                for (GenericCard card : table.get(affectedRow)) {
                                    ((MinionCard) card).health++;
                                }
                                playerMana.playerOneMana -= playerOneHero.mana;
                                break;
                            }
                            if (playerOneHero.name.equals("General Kocioraw")) {
                                for (GenericCard card : table.get(affectedRow)) {
                                    ((MinionCard) card).attackDamage++;
                                }
                                playerMana.playerOneMana -= playerOneHero.mana;
                                break;
                            }
                        }
                        if (turn == 2) {
                            if (playerTwoHero.name.equals("Lord Royce")) {
                                int maxAtk = 0;
                                int index = 0;
                                for (int k = 0; k < table.get(affectedRow).size(); k++) {
                                    if (((MinionCard) table.get(affectedRow).get(k)).attackDamage > maxAtk) {
                                        maxAtk = ((MinionCard) table.get(affectedRow).get(k)).attackDamage;
                                        index = k;
                                    }
                                }
                                if (affectedRow >= 2) {
                                    frozenPlayerOne[affectedRow - 2][index] = 1;
                                }

                                playerMana.playerTwoMana -= playerTwoHero.mana;
                                break;
                            }
                            if (playerTwoHero.name.equals("Empress Thorina")) {
                                int maxHealth = 0;
                                int index = 0;
                                for (int k = 0; k < table.get(affectedRow).size(); k++) {
                                    if (((MinionCard) table.get(affectedRow).get(k)).health > maxHealth) {
                                        maxHealth = ((MinionCard) table.get(affectedRow).get(k)).health;
                                        index = k;
                                    }
                                }
                                table.get(affectedRow).remove(index);
                                playerMana.playerTwoMana -= playerTwoHero.mana;
                                break;
                            }
                            if (playerOneHero.name.equals("King Mudface")) {
                                for (GenericCard card : table.get(affectedRow)) {
                                    ((MinionCard) card).health++;
                                }
                                playerMana.playerTwoMana -= playerTwoHero.mana;
                                break;
                            }
                            if (playerOneHero.name.equals("General Kocioraw")) {
                                for (GenericCard card : table.get(affectedRow)) {
                                    ((MinionCard) card).attackDamage++;
                                }
                                playerMana.playerTwoMana -= playerTwoHero.mana;
                                break;
                            }
                        }
                        break;

                }
            }
        }
    }


    private void attackError(tableCoordinates attacked, tableCoordinates attacker, String error) throws JsonProcessingException {
        ObjectNode jsonNode;
        jsonNode = objectMapper.createObjectNode();
        jsonNode.put("command", "cardUsesAttack");
        String json = objectMapper.writeValueAsString(attacker);
        jsonNode.put("cardAttacker", objectMapper.readTree(json));
        json = objectMapper.writeValueAsString(attacked);
        jsonNode.put("cardAttacked", objectMapper.readTree(json));
        jsonNode.put("error", error);
        output.add(jsonNode);
    }

    private boolean throwPlaceCardErrors(Deck playerTwoHand, int cardIndex) throws JsonProcessingException {
        ObjectNode jsonNode;
        if (isGenericEnvironment(playerTwoHand.getName(cardIndex, objectMapper))) {
            jsonNode = objectMapper.createObjectNode();
            jsonNode.put("command", "placeCard");
            jsonNode.put("handIdx", cardIndex);
            jsonNode.put("error", "Cannot place environment card on table.");
            output.add(jsonNode);
            return true;
        }
        return false;
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

    public boolean isGenericEnvironment(String name) {
        return name.equals("\"Firestorm\"") || name.equals("\"Winterfell\"") || name.equals("\"Heart Hound\"");
    }

    public boolean isCardOnFrontRow(String name) {
        return name.equals("\"The Ripper\"") || name.equals("\"Miraj\"") || name.equals("\"Goliath\"") || name.equals("\"Warden\"");
    }

    public boolean hasSpecialAbility(String name) {
        return name.equals("\"Miraj\"") || name.equals("\"The Ripper\"") || name.equals("\"Disciple\"") || name.equals("\"The Cursed One\"");
    }

    public void useFirestormAbility(ArrayList<ArrayList<GenericCard>> table, int affectedRow) {
        for (int i = 0; i < table.get(affectedRow).size(); i++) {
            table.get(affectedRow).get(i).attackCard(1);
        }

        for (int i = table.get(affectedRow).size() - 1; i >= 0; i--) {
            if (((MinionCard)table.get(affectedRow).get(i)).health <= 0) {
                table.get(affectedRow).remove(i);
            }
        }
    }

    public void useWinterFellAbility(int[][] frozenPlayerOne, int[][] frozenPlayerTwo, int affectedRow) {
        if (affectedRow < 2) {
            // player 1 freezes player 2
            for (int i = 0; i < 5; i++) {
                frozenPlayerTwo[affectedRow][i] = 1;
            }
        } else if (affectedRow >= 2) {
            // player 2 freezes player 1
            for (int i = 0; i < 5; i++) {
                frozenPlayerOne[affectedRow - 2][i] = 1;
            }
        }
    }

    public void useHeartHoundAbility(int affectedRow, ArrayList<ArrayList<GenericCard>> table, int handIdx, PlayerMana playerMana, Deck playerHand, int turn) throws JsonProcessingException{
        int maxHealth = 0;
        int index = 0;
        ObjectNode jsonNode = objectMapper.createObjectNode();
        // finding card index with maximum health
        for (int i = 0; i < table.get(affectedRow).size(); i++) {
            if (((MinionCard)table.get(affectedRow).get(i)).health > maxHealth) {
                index = i;
                maxHealth = ((MinionCard)table.get(affectedRow).get(i)).health;
            }
        }
        // stealing the card
        int mirrorRow = 0;
        if (affectedRow == 0) {
            mirrorRow = 3;
        } else if (affectedRow == 1) {
            mirrorRow = 2;
        } else if (affectedRow == 2) {
            mirrorRow = 1;
        }
        // check if row is full
        if (table.get(mirrorRow).size() >= 5) {
            // throw error
            jsonNode.put("command", "useEnvironmentCard");
            jsonNode.put("handIdx", handIdx);
            jsonNode.put("affectedRow", affectedRow);
            jsonNode.put("error", "Cannot steal enemy card since the player's row is full.");
            output.add(jsonNode);
        } else {
            table.get(mirrorRow).add(table.get(affectedRow).get(index));
            table.get(affectedRow).remove(index);
            if (turn == 1) {
                playerMana.playerOneMana -= playerHand.getMana(handIdx, objectMapper);
                playerHand.cardArrayList.remove(handIdx);
            } else {
                playerMana.playerTwoMana -= playerHand.getMana(handIdx, objectMapper);
                playerHand.cardArrayList.remove(handIdx);
            }
        }
    }

    public ObjectNode getFrozenCardsOnTable(ArrayList<ArrayList<GenericCard>> table, int[][] frozenPlayerOne, int[][] frozenPlayerTwo) throws JsonProcessingException {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayList<GenericCard> frozenCards = new ArrayList<>();
        // TODO fix getfrozencardsa on table
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (frozenPlayerTwo[i][j] == 1) {
                    if (table.get(i).size() > j) {
                        frozenCards.add(table.get(i).get(j));
                    }
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                if (frozenPlayerOne[i][j] == 1) {
                    if (table.get(i).size() > j) {
                        frozenCards.add(table.get(i).get(j));
                    }
                }
            }
        }

        jsonNode.put("command", "getFrozenCardsOnTable");
        String json = objectMapper.writeValueAsString(frozenCards);
        jsonNode.put("output",objectMapper.readTree(json));
        return jsonNode;
    }

    public void attackCard(tableCoordinates attacked, tableCoordinates attacker, ArrayList<ArrayList<GenericCard>> table, int[][] hasAttacked) {
//        System.out.println(attacked.x + " " + attacked.y + " " + attacker.x + " " + attacker.y);
        if (table.get(attacked.x).size() > attacked.y &&  table.get(attacker.x).size() > attacker.y) {
            table.get(attacked.x).get(attacked.y).attackCard(((MinionCard)table.get(attacker.x).get(attacker.y)).attackDamage);
        }

        if (table.get(attacked.x).size() > attacked.y) {
            if (((MinionCard)table.get(attacked.x).get(attacked.y)).health <= 0) {
                table.get(attacked.x).remove(attacked.y);
            }
        }

        hasAttacked[attacker.x][attacker.y] = 1;
    }

    public void showFrozen(int[][] frozenPlayerOne, int[][] frozenPlayerTwo) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.printf("%4d", frozenPlayerTwo[i][j]);
            }
            System.out.println();
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.printf("%4d", frozenPlayerOne[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
