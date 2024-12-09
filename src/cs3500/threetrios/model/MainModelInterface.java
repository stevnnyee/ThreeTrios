package cs3500.threetrios.model;

import java.io.IOException;
import java.util.List;

import cs3500.threetrios.features.ModelFeatures;

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
   * @param cardFile  path to the card configuration file
   * @throws IOException              if files cannot be read
   * @throws IllegalArgumentException if configurations are invalid
   */
  void startGameFromConfig(String boardFile, String cardFile) throws IOException;

  /**
   * Places a card on the grid for the specified player at the given position.
   *
   * @param player the player making the move
   * @param row    the row position on the grid where the card should be placed
   * @param col    the column position on the grid where the card should be placed
   * @param card   the card to be placed on the grid
   * @throws IllegalArgumentException if the move is invalid (e.g., position is occupied,
   *                                  out of bounds, or card placement violates game rules)
   * @throws IllegalArgumentException if any parameter is null
   */
  void placeCard(Player player, int row, int col, Card card);

  /**
   * Places a card on the grid for the current player.
   *
   * @param row  the row position
   * @param col  the column position
   * @param card the card to place
   * @throws IllegalArgumentException if the move is invalid
   */
  void placeCard(int row, int col, Card card);

  /**
   * Returns the list of all players participating in the current game.
   *
   * @return a List containing all Player objects in the game
   */
  List<Player> getPlayers();

  /**
   * Sets the current active player based on their color.
   *
   * @param color the color identifier of the player to set as current
   * @throws IllegalArgumentException if no player matches the given color
   * @throws IllegalArgumentException if the color parameter is null or empty
   */
  void setCurrentPlayer(String color);

  /**
   * Adds a features listener to observe and respond to model state changes.
   * The listener will be notified when significant game events occur.
   *
   * @param listener the ModelFeatures listener to add
   * @throws IllegalArgumentException if the listener parameter is null
   */
  void addFeaturesListener(ModelFeatures listener);

  void executeBattlePhase(cs3500.threetrios.strategy.Position newCardPosition);
}