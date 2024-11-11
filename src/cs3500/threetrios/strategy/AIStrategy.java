package cs3500.threetrios.strategy;

import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

public interface AIStrategy {
  AIMove findBestMove(MainModelInterface model, Player player);
}
