package cs3500.threetrios.strategy;

import cs3500.threetrios.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A strategy that looks ahead to minimize the opponent's best possible moves.
 * Uses another strategy to evaluate the opponent's potential moves.
 */
public class MinimaxStrat implements AIStrategy {
  private final AIStrategy opponentStrategy;
  private static final int MAX_DEPTH = 2;  // Limit search depth for performance

  public MinimaxStrat(AIStrategy opponentStrategy) {
    this.opponentStrategy = opponentStrategy;
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    List<Card> hand = model.getPlayerHand(player);
    List<AIMove> possibleMoves = new ArrayList<>();

    // Generate all possible moves
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (Card card : hand) {
          if (model.canPlaceCard(row, col, card)) {
            // Calculate score based on minimizing opponent's best move
            int minimaxScore = evaluateMove(model, new Position(row, col), card, player);
            possibleMoves.add(new AIMove(card, new Position(row, col), minimaxScore));
          }
        }
      }
    }

    if (possibleMoves.isEmpty()) {
      return StrategyUtil.getDefaultMove(model, player);
    }

    possibleMoves.sort(new MoveComparator());
    return possibleMoves.get(0);
  }

  private int evaluateMove(MainModelInterface model, Position pos, Card card, Player currentPlayer) {
    int immediateScore = calculateImmediateScore(model, pos, card, currentPlayer);

    // Get opponent
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

    // Simulate opponent's best response
    AIMove opponentMove = opponentStrategy.findBestMove(model, opponent);
    int opponentScore = opponentMove.getScore();

    // Our final score is our immediate gain minus the opponent's best response
    return immediateScore - opponentScore;
  }

  private int calculateImmediateScore(MainModelInterface model, Position pos, Card card, Player player) {
    int score = 0;

    // Consider immediate flips
    score += model.getFlippableCards(pos.row, pos.col, card) * 100;

    // Consider defensive value using DefensiveStrat
    DefensiveStrat defensiveStrat = new DefensiveStrat();
    score += defensiveStrat.findBestMove(model, player).getScore() * 0.5; // Weight defensive consideration

    // Consider corner value (from CornerStrat)
    if ((pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1) &&
            (pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1)) {
      score += 50;
    }

    return score;
  }
}