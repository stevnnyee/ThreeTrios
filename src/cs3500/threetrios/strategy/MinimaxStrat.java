package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;
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

  /**
   * Constructor for the minimax strategy by using the opponent's strategy.
   * @param opponentStrategy the strategy the opponent is using.
   */
  public MinimaxStrat(AIStrategy opponentStrategy) {
    if (opponentStrategy == null) {
      throw new IllegalArgumentException("Opponent strategy cannot be null");
    }
    this.opponentStrategy = opponentStrategy;
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    int[] dimensions = model.getGridDimensions();
    if (dimensions[0] <= 0 || dimensions[1] <= 0) {
      throw new IllegalStateException("Invalid grid dimensions");
    }
    List<Card> hand = model.getPlayerHand(player);
    if (hand == null) {
      throw new IllegalStateException("Player hand cannot be null");
    }
    List<AIMove> possibleMoves = new ArrayList<>();

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

  /**
   * Evaluates a move by calculating the immediate value of a move, considering the possible
   * moves of an opposing player.
   *
   * @param model model
   * @param pos position
   * @param card card
   * @param currentPlayer current player
   * @return the combined score of the move's value
   */
  private int evaluateMove(MainModelInterface model,
                           Position pos, Card card, Player currentPlayer) {
    int immediateScore = calculateImmediateScore(model, pos, card, currentPlayer);
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

    AIMove opponentMove = opponentStrategy.findBestMove(model, opponent);
    int opponentScore = opponentMove.getScore();

    return immediateScore - opponentScore;
  }

  /**
   * Calculates the immediate value of a move without considering the opponents possible moves.
   *
   * @param model model
   * @param pos position
   * @param card card
   * @param player player
   * @return the immediate value of a move without considering the opponents possible moves
   */
  private int calculateImmediateScore(MainModelInterface model,
                                      Position pos, Card card, Player player) {
    int score = 0;
    score += model.getFlippableCards(pos.row, pos.col, card) * 100;

    DefensiveStrat defensiveStrat = new DefensiveStrat();
    score += defensiveStrat.findBestMove(model, player).getScore() * 0.5;

    if ((pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1)
            && (pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1)) {
      score += 50;
    }

    return score;
  }
}