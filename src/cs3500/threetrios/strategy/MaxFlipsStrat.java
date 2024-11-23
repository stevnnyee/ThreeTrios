package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

import static cs3500.threetrios.strategy.StrategyUtil.getDefaultMove;

/**
 * An AI strategy that checks for moves that flips the most amount of opponent cards in a single
 * turn.
 */
public class MaxFlipsStrat implements AIStrategy {
  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }

    // Get hand directly from model using player's color
    List<Card> hand = null;
    for (Player p : model.getPlayers()) {
      if (p.getColor().equals(player.getColor())) {
        hand = model.getPlayerHand(p);
        break;
      }
    }

    if (hand == null || hand.isEmpty()) {
      throw new IllegalStateException("No cards in player's hand");
    }

    List<AIMove> possibleMoves = new ArrayList<>();
    int bestScore = -1;
    AIMove bestMove = null;

    // Try all possible moves
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (Card card : hand) {
          if (model.canPlaceCard(row, col, card)) {
            // Calculate composite score that considers:
            // 1. Number of flips (primary)
            // 2. Card strength (secondary)
            // 3. Position value (tertiary)
            int flips = model.getFlippableCards(row, col, card);
            int cardStrength = card.getAttackPower(Direction.NORTH) +
                    card.getAttackPower(Direction.SOUTH) +
                    card.getAttackPower(Direction.EAST) +
                    card.getAttackPower(Direction.WEST);

            // Base score from flips
            int score = flips * 1000;

            // Add card strength bonus
            score += cardStrength * 10;

            // Add position bonus for corners and edges
            if ((row == 0 || row == model.getGridDimensions()[0] - 1) &&
                    (col == 0 || col == model.getGridDimensions()[1] - 1)) {
              score += 500; // Corner bonus
            } else if (row == 0 || row == model.getGridDimensions()[0] - 1 ||
                    col == 0 || col == model.getGridDimensions()[1] - 1) {
              score += 200; // Edge bonus
            }

            // Create move with composite score
            AIMove move = new AIMove(card, new Position(row, col), score);
            if (score > bestScore) {
              bestScore = score;
              bestMove = move;
            }
          }
        }
      }
    }

    if (bestMove == null) {
      return getDefaultMove(model, player);
    }

    return bestMove;
  }
}