package cs3500.threetrios.provider.model;

import java.util.List;

/**
 * Interface for all Trios games.
 */
public interface ReadOnlyTT {

  /**
   * Returns the current turn in play.
   *
   * @return the PlayerColor of the turn to be made.
   */
  PlayerColor getCurrentTurn();

  /**
   * Gets the cell at the specified index.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the cell at this location
   * @throws IllegalArgumentException if the game has not started
   * @throws IllegalArgumentException if the row index is illegal
   * @throws IllegalArgumentException if the col index is illegal
   */
  Cell getCell(int row, int col);

  /**
   * Gets the owner of the card at the specified index.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @throws IllegalStateException if the game has not started
   */
  PlayerColor getCardOwner(int row, int col);

  /**
   * Determines whether playing a card to this spot would be valid.
   *
   * @param row the row we are checking.
   * @param col the col we are checking.
   * @throws IllegalStateException    if the game has not started
   * @throws IllegalArgumentException if the row / col is invalid
   */
  boolean isValidMove(int row, int col);

  /**
   * Counts the number of cards flipped by placing the given card in the given row and col.
   *
   * @param toPlace the card to place
   * @param row     the row to place in
   * @param col     the col to place in
   * @return the number of cards flipped by placing the given card in the given row and col
   * @throws IllegalStateException    if the game has not started
   * @throws IllegalArgumentException if the row / col is invalid
   */
  int countNumFlipped(Card toPlace, int row, int col);

  /**
   * Creates a copy of the game board, which is 0-indexed.
   *
   * @return a copy of the game board
   * @throws IllegalStateException if the game has not started
   */
  List<List<Cell>> getBoard();

  /**
   * Returns the number of rows in the game's board.
   *
   * @return the number of rows in the game grid
   * @throws IllegalStateException if the game has not started
   */
  int numRows();

  /**
   * Returns the number of columns in the game's board.
   *
   * @return the number of columns in the game grid
   * @throws IllegalStateException if the game has not started
   */
  int numCols();

  /**
   * Gets a copy of the given player's hand.
   *
   * @param turn the hand of the player to get
   * @return the list of cards belonging to this player
   * @throws IllegalStateException if the game has not started
   */
  List<Card> getHand(PlayerColor turn);


  /**
   * Checks if the game is over.
   *
   * @return true or false if the game is over.
   */
  boolean isGameOver();

  /**
   * Returns the score of a given player.
   *
   * @param player to check the score of
   * @return a tally of the number of owned cards
   */
  int getScore(PlayerColor player);

  /**
   * If the game is over, returns the winner of the game.
   * AKA the player with the most cards of their color on the board.
   *
   * @return the color of the winning player or null if tie
   * @throws IllegalStateException if the current gamestate is not over
   */
  PlayerColor getWinner();

}
