package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * A composite strategy that combines multiple strategies with weights.
 * This allows for creating complex strategies by combining simpler ones.
 * In combining multiple strategies this helps determine the best move in a game.
 */
public class CompositeStrategy implements AIStrategy {
  private final List<WeightedStrategy> strategies;

  /**
   * Constructs a new CompositeStrategy by combining strategies with their respective weights.
   *
   * @param strategies List of strategies to be combined
   * @param weights    List of weights corresponding to each strategy
   */
  public CompositeStrategy(List<AIStrategy> strategies, List<Integer> weights) {
    if (strategies == null || weights == null) {
      throw new IllegalArgumentException("Strategies and weights cannot be null");
    }
    if (strategies.isEmpty() || weights.isEmpty()) {
      throw new IllegalArgumentException("Strategies and weights cannot be empty");
    }
    if (strategies.size() != weights.size()) {
      throw new IllegalArgumentException("Must provide same number of strategies and weights");
    }
    if (strategies.contains(null)) {
      throw new IllegalArgumentException("Strategy list cannot contain null");
    }
    for (Integer weight : weights) {
      if (weight < 0) {
        throw new IllegalArgumentException("Weights cannot be negative");
      }
    }
    this.strategies = new ArrayList<>();
    for (int i = 0; i < strategies.size(); i++) {
      this.strategies.add(new WeightedStrategy(strategies.get(i), weights.get(i)));
    }
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    List<AIMove> allMoves = new ArrayList<>();
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        for (Card card : model.getPlayerHand(player)) {
          if (model.canPlaceCard(row, col, card)) {
            int totalScore = 0;
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

  /**
   * Private inner class representing a strategy and its associated weight.
   * This class helps represent the relationship between a strategy and
   * its influence on the composite decision.
   */
  private static class WeightedStrategy {
    final AIStrategy strategy;
    final int weight;

    /**
     * Constructs a new WeightedStrategy that takes in a strategy and its weight.
     *
     * @param strategy the AI strategy
     * @param weight   the strategy's weight
     */
    WeightedStrategy(AIStrategy strategy, int weight) {
      this.strategy = strategy;
      this.weight = weight;
    }
  }

}