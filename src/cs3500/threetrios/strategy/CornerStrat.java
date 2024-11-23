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

    // Get hand safely
    List<Card> hand = model.getPlayerHand(player);
    if (hand == null || hand.isEmpty()) {
      throw new IllegalStateException("Player has no cards");
    }

    // Get dimensions safely
    int[] dimensions = model.getGridDimensions();
    int rows = dimensions[0];
    int cols = dimensions[1];

    // Try corners first
    List<Position> corners = new ArrayList<>(4);
    corners.add(new Position(0, 0));
    corners.add(new Position(0, cols - 1));
    corners.add(new Position(rows - 1, 0));
    corners.add(new Position(rows - 1, cols - 1));

    // Check corner positions first
    for (Position pos : corners) {
      if (!model.isHole(pos.row, pos.col) && model.getCardAt(pos.row, pos.col) == null) {
        for (Card card : hand) {
          if (model.canPlaceCard(pos.row, pos.col, card)) {
            int score = evaluateMove(model, pos, card);
            return new AIMove(card, pos, score);
          }
        }
      }
    }

    // If no corner moves, try any valid move
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (!model.isHole(r, c) && model.getCardAt(r, c) == null) {
          for (Card card : hand) {
            if (model.canPlaceCard(r, c, card)) {
              Position pos = new Position(r, c);
              int score = evaluateMove(model, pos, card);
              return new AIMove(card, pos, score);
            }
          }
        }
      }
    }

    throw new IllegalStateException("No valid moves available");
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

    // Card strength
    score += card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST);

    return score;
  }
}