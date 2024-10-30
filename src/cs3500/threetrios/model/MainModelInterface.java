package cs3500.threetrios.model;

import java.util.List;

import javax.swing.text.Position;

/**
 * Interface for the model of the ThreeTrios game, defining the actions that
 * can be performed on the game.
 */
public interface MainModelInterface {

  /**
   * Starts a new game with the given grid and cards.
   *
   * @param grid grid
   * @param deck cards
   */
  void startGame(Grid grid, List<Card> deck);

  /**
   * Places a card on the grid for a given player.
   *
   * @param player player
   * @param row    row to place card
   * @param col    column to place card
   * @param card   card to place
   * @throws IllegalArgumentException if the move is invalid
   */
  void placeCard(Player player, int row, int col, Card card);

  /**
   * Gets the current state of the grid.
   *
   * @return the current Grid
   */
  Grid getGrid();

  /**
   * Gets the current player, either red or blue.
   *
   * @return the current Player
   */
  Player getCurrentPlayer();

  /**
   * Gets the hand of the specified player.
   *
   * @param player the player
   * @return The list of cards in the player's hand
   */
  List<Card> getPlayerHand(Player player);

  /**
   * Checks if you can place the card onto the specified cell.
   *
   * @param row row
   * @param col column
   * @param card card
   * @return true if you can place card, false otherwise
   */
  boolean canPlaceCard(int row, int col, Card card);

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  boolean isGameOver();

  /**
   * Gets the winner of the game.
   *
   * @return the winning Player, or null if the game is a tie or not over
   * @throws IllegalStateException if the game is not over
   */
  Player getWinner();

  /**
   * Get score for specified player, the count of owned cards.
   *
   * @param player player
   * @return number of cards owned by player
   */
  int getPlayerScore(Player player);



  Player determineWinner();

  void executeBattlePhase(ThreeTriosGameModel.Position newCardPosition);

  void dealCards(List<Card> deck);


  void initialize(Grid grid);
}
