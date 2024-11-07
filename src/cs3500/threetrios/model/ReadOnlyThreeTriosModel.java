package cs3500.threetrios.model;

import java.util.List;

/**
 * A read-only interface for the ThreeTrios game model. This interface provides methods
 * for observing the game state without modifying it.
 */
public interface ReadOnlyThreeTriosModel {
  /**
   * Gets the current state of the grid without allowing modifications.
   *
   * @return the current Grid
   */
  Grid getGrid();

  /**
   * Gets the dimensions of the grid.
   *
   * @return an array where [0] is rows and [1] is columns
   */
  int[] getGridDimensions();

  /**
   * Gets the card at the specified cell coordinates.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @return the Card at the specified position, or null if empty
   * @throws IllegalArgumentException if coordinates are invalid
   */
  Card getCardAt(int row, int col);

  /**
   * Gets the owner of the card at the specified cell coordinates.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @return the Player who owns the card, or null if cell is empty
   * @throws IllegalArgumentException if coordinates are invalid
   */
  Player getCardOwnerAt(int row, int col);

  /**
   * Gets the current player's turn.
   *
   * @return the current Player
   */
  Player getCurrentPlayer();

  /**
   * Gets a copy of the specified player's hand.
   *
   * @param player the player whose hand to get
   * @return List of Cards in the player's hand
   * @throws IllegalArgumentException if player is null
   */
  List<Card> getPlayerHand(Player player);

  /**
   * Checks if the current player can legally place a card at the specified position.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @param card the card to check
   * @return true if the move is legal, false otherwise
   */
  boolean canPlaceCard(int row, int col, Card card);

  /**
   * Gets the number of cards that would be flipped if a card is played at the specified position.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @param card the card to check
   * @return number of cards that would be flipped
   */
  int getFlippableCards(int row, int col, Card card);

  /**
   * Gets the current score for the specified player.
   *
   * @param player the player whose score to get
   * @return the player's current score (owned cards in hand + on grid)
   * @throws IllegalArgumentException if player is null
   */
  int getPlayerScore(Player player);

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Gets the winner of the game if it's over.
   *
   * @return the winning Player, or null if game is tied or not over
   */
  Player getWinner();

  /**
   * Checks if the given position is a hole in the grid.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @return true if the position is a hole, false otherwise
   * @throws IllegalArgumentException if coordinates are invalid
   */
  boolean isHole(int row, int col);
}