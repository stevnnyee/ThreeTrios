package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

import static cs3500.threetrios.strategy.StrategyUtil.getDefaultMove;

/**
 * A CornerStrat class that prioritizes placing cards in corner positions on the game board.
 * This strategy follows a defensive approach by attempting to secure corner positions,
 * which are positionally better due to having fewer adjacent spaces.
 */
public class CornerStrat implements AIStrategy {

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    List<Card> hand = model.getPlayerHand(player);
    if (hand == null) {
      throw new IllegalStateException("Player hand cannot be null");
    }
    int[] dimensions = model.getGridDimensions();
    if (dimensions[0] <= 0 || dimensions[1] <= 0) {
      throw new IllegalStateException("Invalid grid dimensions");
    }
    List<AIMove> possibleMoves = new ArrayList<>();

    Position[] corners = new Position[4];
    corners[0] = new Position(0, 0);
    corners[1] = new Position(0, model.getGridDimensions()[1] - 1);
    corners[2] = new Position(model.getGridDimensions()[0] - 1, 0);
    corners[3] = new Position(model.getGridDimensions()[0] - 1,
            model.getGridDimensions()[1] - 1);

    for (Position corner : corners) {
      if (!model.isHole(corner.row, corner.col)
              && model.getCardAt(corner.row, corner.col) == null) {
        for (Card card : hand) {
          if (model.canPlaceCard(corner.row, corner.col, card)) {
            int defensibility = calculateDefensibility(model, corner, card, player);
            possibleMoves.add(new AIMove(card, corner, defensibility));
          }
        }
      }
    }
    if (possibleMoves.isEmpty()) {
      for (int row = 0; row < model.getGridDimensions()[0]; row++) {
        for (int col = 0; col < model.getGridDimensions()[1]; col++) {
          if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
            continue;
          }

          for (Card card : hand) {
            if (model.canPlaceCard(row, col, card)) {
              int defensibility = calculateDefensibility(model,
                      new Position(row, col), card, player);
              possibleMoves.add(new AIMove(card, new Position(row, col), defensibility));
            }
          }
        }
      }
    }
    if (possibleMoves.isEmpty()) {
      return getDefaultMove(model, player);
    }
    possibleMoves.sort(new MoveComparator());
    return possibleMoves.get(0);
  }

  /**
   * Calculates a defensibility score for a potential move based on position and
   * card characteristics.
   * The score is calculated by:
   * Adding 1000 points if the position is a corner.
   * Adding the sum of the card's attack powers in all directions.
   *
   *
   * @param model the current model game state
   * @param pos the position
   * @param card the card at hand
   * @param player the player making the move
   * @return
   */
  private int calculateDefensibility(MainModelInterface model,
                                     Position pos, Card card, Player player) {
    if (pos == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    int score = 0;
    if ((pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1)
            && (pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1)) {
      score += 1000;
    }
    score += card.getAttackPower(Direction.NORTH);
    score += card.getAttackPower(Direction.SOUTH);
    score += card.getAttackPower(Direction.EAST);
    score += card.getAttackPower(Direction.WEST);
    return score;
  }
}