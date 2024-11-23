package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A strategy that looks ahead to minimize the opponent's best possible moves.
 * Uses another strategy to evaluate the opponent's potential moves.
 */
public class MinimaxStrat implements AIStrategy {
  private final AIStrategy opponentStrategy;
  private static final int MAX_DEPTH = 2;

  public MinimaxStrat(AIStrategy opponentStrategy) {
    if (opponentStrategy == null) {
      throw new IllegalArgumentException("Opponent strategy cannot be null");
    }
    this.opponentStrategy = opponentStrategy;
  }

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
    int bestScore = Integer.MIN_VALUE;
    AIMove bestMove = null;

    // Try all possible moves
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (Card card : hand) {
          if (model.canPlaceCard(row, col, card)) {
            int score = evaluateMove(model, new Position(row, col), card, player);
            // Add 1000 to ensure positive scores for AIMove constructor
            score += 1000;
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
      return StrategyUtil.getDefaultMove(model, player);
    }

    return bestMove;
  }

  private int evaluateMove(MainModelInterface model, Position pos, Card card, Player currentPlayer) {
    int immediateScore = calculateImmediateScore(model, pos, card, currentPlayer);

    // Find opponent
    Player opponent = null;
    for (Player p : model.getPlayers()) {
      if (!p.getColor().equals(currentPlayer.getColor())) {
        opponent = p;
        break;
      }
    }

    if (opponent == null) {
      return immediateScore;
    }

    // Evaluate opponent's best response
    try {
      AIMove opponentMove = opponentStrategy.findBestMove(model, opponent);
      int opponentScore = opponentMove.getScore();
      return immediateScore - (opponentScore / 2); // Weight opponent moves less
    } catch (Exception e) {
      // If opponent evaluation fails, return just the immediate score
      return immediateScore;
    }
  }

  private int calculateImmediateScore(MainModelInterface model, Position pos, Card card, Player player) {
    int score = 0;

    // Flips are most important
    score += model.getFlippableCards(pos.row, pos.col, card) * 1000;

    // Card strength is second priority
    score += (card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST)) * 10;

    // Position value
    if ((pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1) &&
            (pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1)) {
      score += 500; // Corner bonus
    } else if (pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1 ||
            pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1) {
      score += 200; // Edge bonus
    }

    return score;
  }
}