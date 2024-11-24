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
 * turn. Falls back to a default move if no valid moves are available.
 */
public class MaxFlipsStrat implements AIStrategy {
  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    validateInputs(model, player);
    List<Card> hand = getPlayerHand(model, player);
    if (hand == null || hand.isEmpty()) {
      return new AIMove(new MockCard("defaultCard", 5, 5, 5, 5),
              new Position(0, 0), 0);
    }

    AIMove bestMove = findBestPossibleMove(model, hand);
    if (bestMove == null) {
      return getDefaultMove(model, player);
    }

    return bestMove;
  }

  /**
   * Validates the input parameters for the strategy.
   *
   * @param model  the game model to validate
   * @param player the player to validate
   * @throws IllegalArgumentException if either parameter is null
   */
  private void validateInputs(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }
  }

  /**
   * Gets the player's hand from the model.
   *
   * @param model  the game model
   * @param player the current player
   * @return the list of cards in the player's hand
   */
  private List<Card> getPlayerHand(MainModelInterface model, Player player) {
    for (Player p : model.getPlayers()) {
      if (p.getColor().equals(player.getColor())) {
        return model.getPlayerHand(p);
      }
    }
    return new ArrayList<>();
  }

  /**
   * Finds the best possible move by evaluating all valid positions and cards.
   *
   * @param model the game model
   * @param hand  the player's hand of cards
   * @return the best move found, or null if no valid moves exist
   */
  private AIMove findBestPossibleMove(MainModelInterface model, List<Card> hand) {
    int bestScore = -1;
    AIMove bestMove = null;

    int[] dimensions = model.getGridDimensions();
    if (dimensions[0] <= 0 || dimensions[1] <= 0) {
      throw new IllegalStateException("Invalid grid dimensions");
    }

    // Try all possible moves
    for (int row = 0; row < dimensions[0]; row++) {
      for (int col = 0; col < dimensions[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        AIMove move = evaluatePosition(model, hand, row, col);
        if (move != null && move.getScore() > bestScore) {
          bestScore = move.getScore();
          bestMove = move;
        }
      }
    }

    return bestMove;
  }

  /**
   * Evaluates all possible cards at a given position.
   *
   * @param model the game model
   * @param hand  the player's hand
   * @param row   the row to evaluate
   * @param col   the column to evaluate
   * @return the best move for this position, or null if no valid moves exist
   */
  private AIMove evaluatePosition(MainModelInterface model, List<Card> hand, int row, int col) {
    int bestScore = -1;
    AIMove bestMove = null;

    for (Card card : hand) {
      if (model.canPlaceCard(row, col, card)) {
        int score = calculateScore(model, card, row, col);
        if (score > bestScore) {
          // Create a new card to preserve identity
          Card moveCard = new MockCard(card.getName(),
                  card.getAttackPower(Direction.NORTH),
                  card.getAttackPower(Direction.SOUTH),
                  card.getAttackPower(Direction.EAST),
                  card.getAttackPower(Direction.WEST));
          bestScore = score;
          bestMove = new AIMove(moveCard, new Position(row, col), Math.max(score, 0));
        }
      }
    }

    return bestMove;
  }

  /**
   * Calculates a composite score for a potential move considering flips, card strength,
   * and position.
   *
   * @param model the game model
   * @param card  the card to evaluate
   * @param row   the row position
   * @param col   the column position
   * @return the calculated score for the move
   */
  private int calculateScore(MainModelInterface model, Card card, int row, int col) {
    int flips = model.getFlippableCards(row, col, card);
    int cardStrength = calculateCardStrength(card);

    // Base score from flips
    int score = flips * 1000;

    // Add card strength bonus
    score += cardStrength * 10;

    // Add position bonus
    score += calculatePositionBonus(model, row, col);

    return score;
  }

  /**
   * Calculates the total strength of a card based on its attack values.
   *
   * @param card the card to evaluate
   * @return the sum of all attack values
   */
  private int calculateCardStrength(Card card) {
    return card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST);
  }

  /**
   * Calculates position-based bonus scores for corners and edges.
   *
   * @param model the game model
   * @param row   the row position
   * @param col   the column position
   * @return the position bonus score
   */
  private int calculatePositionBonus(MainModelInterface model, int row, int col) {
    if ((row == 0 || row == model.getGridDimensions()[0] - 1) &&
            (col == 0 || col == model.getGridDimensions()[1] - 1)) {
      return 500; // Corner bonus
    } else if (row == 0 || row == model.getGridDimensions()[0] - 1 ||
            col == 0 || col == model.getGridDimensions()[1] - 1) {
      return 200; // Edge bonus
    }
    return 0;
  }
}