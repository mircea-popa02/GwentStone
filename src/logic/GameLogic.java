package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;
import java.util.Collections;

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

        // TODO vezi daca exista un singura table sau mai multe
        Table tablePlayerOne;
        Table tablePlayerTwo;

        Hero playerOneHero;
        Hero PlayerTwoHero;
        for (int i = 0; i < rounds; i++) {
            // choosing a deck
            int deckNumOne = inputData.getGames().get(i).getStartGame().getPlayerOneDeckIdx();
            int deckNumTwo = inputData.getGames().get(i).getStartGame().getPlayerTwoDeckIdx();

            // add cards to deck for each player
            playerOneDeck.addToDeck(inputData.getPlayerOneDecks().getDecks().get(deckNumOne));
            playerTwoDeck.addToDeck(inputData.getPlayerTwoDecks().getDecks().get(deckNumTwo));

            // print to output deck as json
//            playerOneDeck.writeDeckToOutput(objectMapper, output);
            
        }
    }


}
