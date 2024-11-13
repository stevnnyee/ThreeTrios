package cs3500.threetrios.strategy;

import cs3500.threetrios.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A composite strategy that combines multiple strategies with weights.
 * This allows for creating complex strategies by combining simpler ones.
 */
public class CompositeStrategy implements AIStrategy {
  private final List<WeightedStrategy> strategies;

  public CompositeStrategy(List<AIStrategy> strategies, List<Integer> weights) {
    if (strategies.size() != weights.size()) {
      throw new IllegalArgumentException("Must provide same number of strategies and weights");
    }
    this.strategies = new ArrayList<>();
    for (int i = 0; i < strategies.size(); i++) {
      this.strategies.add(new WeightedStrategy(strategies.get(i), weights.get(i)));
    }
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    List<AIMove> allMoves = new ArrayList<>();

    // Get moves from all strategies
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        for (Card card : model.getPlayerHand(player)) {
          if (model.canPlaceCard(row, col, card)) {
            int totalScore = 0;
            // Calculate weighted score from each strategy
            for (WeightedStrategy ws : strategies) {
              AIMove move = ws.strategy.findBestMove(model, player);
              if (move.getPosition().row == row && move.getPosition().col == col
                      && move.getCard().equals(card)) {
                totalScore += move.getScore() * ws.weight;
              }
            }
            allMoves.add(new AIMove(card, new Position(row, col), totalScore));
          }
        }
      }
    }

    if (allMoves.isEmpty()) {
      return StrategyUtil.getDefaultMove(model, player);
    }

    allMoves.sort(new MoveComparator());
    return allMoves.get(0);
  }

  private static class WeightedStrategy {
    final AIStrategy strategy;
    final int weight;

    WeightedStrategy(AIStrategy strategy, int weight) {
      this.strategy = strategy;
      this.weight = weight;
    }
  }
}