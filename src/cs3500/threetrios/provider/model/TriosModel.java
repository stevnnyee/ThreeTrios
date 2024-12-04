package cs3500.threetrios.provider.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import cs3500.threetrios.provider.model.ReadOnlyTT;

/**
 * Interface for all TriosModel games.
 */
public interface TriosModel extends ReadOnlyTT {

  /**
   * Draws until the given player's hand is full. Updates current turn once drawn.
   *
   * @param player the player color to draw for
   */
  void drawForHand(PlayerColor player);

  /**
   * Starts the game with the given hand size.
   *
   * @throws IllegalArgumentException if the deck size is too small
   * @throws IllegalArgumentException if the number of card cells is not odd
   */
  void playGame(List<Card> deck, List<java.util.List<Cell>> grid);

  /**
   * Place the given card to the board.
   *
   * @param cardName the name of the card in the player's hand
   * @param row      the row to play the card to
   * @param col      the column to play the card to
   * @throws IllegalArgumentException if the game is not started or over
   * @throws IllegalArgumentException if the row or col index is invalid
   */
  void placeCard(String cardName, int row, int col);

  /**
   * Parses the given deck config file into a list of cards representing the deck.
   * If there are cards with duplicate names, only takes the first one.
   *
   * @param cardConfig the config file
   * @return the parsed deck as a list of cards
   * @throws IOException if unable to read from file
   */
  static List<Card> parseDeckConfig(File cardConfig) throws IOException {
    Scanner scanCards = new Scanner(cardConfig);

    Set<Card> cards = new HashSet<>();

    while (scanCards.hasNextLine()) {
      String name = scanCards.next();
      String northStr = scanCards.next();
      int north = (northStr.equals("A")) ? 10 : Integer.parseInt(northStr);
      String southStr = scanCards.next();
      int south = (southStr.equals("A")) ? 10 : Integer.parseInt(southStr);
      String eastStr = scanCards.next();
      int east = (eastStr.equals("A")) ? 10 : Integer.parseInt(eastStr);
      String westStr = scanCards.next();
      int west = (westStr.equals("A")) ? 10 : Integer.parseInt(westStr);

      cards.add(new Card(name, north, south, east, west));
    }

    List<Card> result = new ArrayList<>(cards);
    result.sort(Comparator.comparing(Card::getName));

    return result;
  }

  /**
   * Determines if the given move is valid.
   *
   * @param row      the row to play the card to
   * @param col      the column to play the card to
   * @return true if the move is valid, false otherwise
   * @throws IllegalArgumentException if the game is not started or over
   */
  boolean isValidMove(int row, int col);

  /**
   * Parses the given grid config file, returning a 2D list of cells representing the game board.
   * Ignores all rows after the provided number of rows.
   *
   * @param gridConfig the config file
   * @return the parsed grid represented as a 2D list of Cells
   * @throws IOException if unable to read from file
   */

  /**
   * Adds a listener for player actions.
   * @param listener the listener to add
   */
}
