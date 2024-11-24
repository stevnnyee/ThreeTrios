package cs3500.threetrios.features;

import cs3500.threetrios.model.Player;

/**
 * Interface for handling model-related game events and notifying observers of significant
 * changes in the game state.
 */
public interface ModelFeatures {
  /**
   * Notifies observers when the active turn changes to a different player.
   * This method is called whenever the game advances to the next player's turn.
   *
   * @param player the Player whose turn is now active
   * @throws IllegalArgumentException if the player parameter is null
   */
  void notifyTurnChange(Player player);

  /**
   * Notifies observers when the game has concluded and a winner is determined.
   * This method is called once the game reaches its end condition.
   *
   * @param winner the Player who has won the game
   * @throws IllegalArgumentException if the winner parameter is null
   */
  void notifyGameOver(Player winner);
}