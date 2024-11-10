package cs3500.threetrios.model;

import java.io.IOException;
import java.util.List;

/**
 * Interface for the model of the ThreeTrios game, extending the read-only interface
 * to add methods that modify game state.
 */
public interface MainModelInterface extends ReadOnlyThreeTriosModel {
  /**
   * Starts a new game with the given grid and deck of cards.
   *
   * @param grid the game grid
   * @param deck the deck of cards
   * @throws IllegalArgumentException if grid or deck is invalid
   */
  void startGame(Grid grid, List<Card> deck);

  /**
   * Starts a new game using configuration files.
   *
   * @param boardFile path to the board configuration file
   * @param cardFile path to the card configuration file
   * @throws IOException if files cannot be read
   * @throws IllegalArgumentException if configurations are invalid
   */
  void startGameFromConfig(String boardFile, String cardFile) throws IOException;

  /**
   * Places a card on the grid for the current player.
   *
   * @param row row coordinate
   * @param col column coordinate
   * @param card the card to place
   * @throws IllegalArgumentException if the move is invalid
   * @throws IllegalStateException if the game is not in progress
   */
  void placeCard(int row, int col, Card card);

  List<Player> getPlayers();
}