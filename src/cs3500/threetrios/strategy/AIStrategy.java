package cs3500.threetrios.strategy;

import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * Interface that defines a strategy for determining the optimal move in a game-playing AI system.
 * Implementations of this interface represent different algorithms or approaches
 * for selecting the best possible move given the current game state.
 */
public interface AIStrategy {
  AIMove findBestMove(MainModelInterface model, Player player);
}
