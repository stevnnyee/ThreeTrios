package cs3500.threetrios.strategy;

import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * A utility class that provides helper methods for AI methods in the ThreeTrios game.
 * This provides default move scenarios for simpler functionality.
 */
public class StrategyUtil {

  /**
   * Generates a default move when a strategy cannot provide a move.
   *
   * @param model  the game model
   * @param player the player
   * @return returns an AIMove representing the first available valid move
   */
  public static AIMove getDefaultMove(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }

    // Get hand safely
    List<Card> hand = model.getPlayerHand(player);
    if (hand == null || hand.isEmpty()) {
      throw new IllegalStateException("No cards in hand");
    }

    // Get dimensions safely
    int[] dims = model.getGridDimensions();
    int rows = dims[0];
    int cols = dims[1];

    // Find first valid move
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (!model.isHole(row, col) && model.getCardAt(row, col) == null) {
          // Try each card in hand
          for (Card card : hand) {
            if (model.canPlaceCard(row, col, card)) {
              return new AIMove(card, new Position(row, col), 0);
            }
          }
        }
      }
    }
    throw new IllegalStateException("No valid moves available");
  }
}