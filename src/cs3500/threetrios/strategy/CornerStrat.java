package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * An AI strategy that prioritizes placing cards in corner positions on the game board.
 * If no valid corner moves are available, it falls back to evaluating all possible positions.
 */
public class CornerStrat implements AIStrategy {

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }

    List<Card> hand = getPlayerHandFromModel(model, player);
    int[] dimensions = model.getGridDimensions();
    AIMove cornerMove = tryCornerMoves(model, hand, dimensions);
    if (cornerMove != null) {
      return cornerMove;
    }
    AIMove gridMove = tryGridMoves(model, hand, dimensions);
    if (gridMove != null) {
      return gridMove;
    }
    return StrategyUtil.getDefaultMove(model, player);
  }

  private List<Card> getPlayerHandFromModel(MainModelInterface model, Player player) {
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
    return hand;
  }

  private AIMove tryCornerMoves(MainModelInterface model, List<Card> hand, int[] dimensions) {
    List<Position> corners = getCornerPositions(dimensions[0], dimensions[1]);
    int bestScore = -1;
    AIMove bestMove = null;

    for (Position pos : corners) {
      if (!model.isHole(pos.row, pos.col) && model.getCardAt(pos.row, pos.col) == null) {
        AIMove move = tryMovesAtPosition(model, hand, pos, 1000);
        if (move != null && move.getScore() > bestScore) {
          bestScore = move.getScore();
          bestMove = move;
        }
      }
    }
    return bestMove;
  }

  private AIMove tryGridMoves(MainModelInterface model, List<Card> hand, int[] dimensions) {
    int bestScore = -1;
    AIMove bestMove = null;

    for (int row = 0; row < dimensions[0]; row++) {
      for (int col = 0; col < dimensions[1]; col++) {
        if (!model.isHole(row, col) && model.getCardAt(row, col) == null) {
          AIMove move = tryMovesAtPosition(model, hand, new Position(row, col), 0);
          if (move != null && move.getScore() > bestScore) {
            bestScore = move.getScore();
            bestMove = move;
          }
        }
      }
    }
    return bestMove;
  }

  private AIMove tryMovesAtPosition(MainModelInterface model, List<Card> hand,
                                    Position pos, int bonus) {
    int bestScore = -1;
    AIMove bestMove = null;

    for (Card card : hand) {
      if (model.canPlaceCard(pos.row, pos.col, card)) {
        int score = calculateCardStrengthScore(card) + bonus;
        if (score > bestScore) {
          bestScore = score;
          bestMove = new AIMove(card, pos, score);
        }
      }
    }
    return bestMove;
  }


  /**
   * Generates a list of corner positions based on the board dimensions.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   * @return list of corner positions
   */
  private List<Position> getCornerPositions(int rows, int cols) {
    List<Position> corners = new ArrayList<>(4);
    corners.add(new Position(0, 0));
    corners.add(new Position(0, cols - 1));
    corners.add(new Position(rows - 1, 0));
    corners.add(new Position(rows - 1, cols - 1));
    return corners;
  }

  /**
   * Calculates the score based on the card's attack values.
   *
   * @param card the card to evaluate
   * @return the card strength-based score component
   */
  private int calculateCardStrengthScore(Card card) {
    int maxAttack = Math.max(
            Math.max(card.getAttackPower(Direction.NORTH), card.getAttackPower(Direction.SOUTH)),
            Math.max(card.getAttackPower(Direction.EAST), card.getAttackPower(Direction.WEST))
    );

    return (maxAttack * 2) +
            card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST);
  }
}