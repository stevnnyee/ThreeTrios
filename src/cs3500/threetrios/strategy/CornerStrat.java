package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

public class CornerStrat implements AIStrategy {

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }

    // Get hand directly from the model using our player's color
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

    // Get dimensions
    int[] dimensions = model.getGridDimensions();
    int rows = dimensions[0];
    int cols = dimensions[1];

    // Try corners first
    List<Position> corners = new ArrayList<>(4);
    corners.add(new Position(0, 0));
    corners.add(new Position(0, cols - 1));
    corners.add(new Position(rows - 1, 0));
    corners.add(new Position(rows - 1, cols - 1));

    // Check corner positions first with best scoring
    AIMove bestMove = null;
    int bestScore = -1;

    // Try corners first
    for (Position pos : corners) {
      if (!model.isHole(pos.row, pos.col) && model.getCardAt(pos.row, pos.col) == null) {
        for (Card card : hand) {
          if (model.canPlaceCard(pos.row, pos.col, card)) {
            int score = evaluateMove(model, pos, card);
            if (score > bestScore) {
              bestScore = score;
              bestMove = new AIMove(card, pos, score);
            }
          }
        }
      }
    }

    // If no corner moves available, try other positions
    // If no corner moves available, try other positions
    if (bestMove == null) {
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
          Position currentPos = new Position(r, c);  // Create position object here
          if (!model.isHole(r, c) && model.getCardAt(r, c) == null) {
            for (Card card : hand) {
              if (model.canPlaceCard(r, c, card)) {
                int score = evaluateMove(model, currentPos, card);  // Use the created position
                if (score > bestScore) {
                  bestScore = score;
                  bestMove = new AIMove(card, currentPos, score);
                }
              }
            }
          }
        }
      }
    }

    if (bestMove == null) {
      throw new IllegalStateException("No valid moves available");
    }

    return bestMove;
  }

  private int evaluateMove(MainModelInterface model, Position pos, Card card) {
    int score = 0;

    // Position scoring
    int rows = model.getGridDimensions()[0];
    int cols = model.getGridDimensions()[1];

    if ((pos.row == 0 || pos.row == rows - 1) &&
            (pos.col == 0 || pos.col == cols - 1)) {
      score += 1000;  // Corner position
    }

    // Card strength - prioritize high values
    int maxAttack = Math.max(
            Math.max(card.getAttackPower(Direction.NORTH), card.getAttackPower(Direction.SOUTH)),
            Math.max(card.getAttackPower(Direction.EAST), card.getAttackPower(Direction.WEST))
    );
    score += maxAttack * 2;  // Give extra weight to high attack values

    // Add base card strength
    score += card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST);

    return score;
  }
}