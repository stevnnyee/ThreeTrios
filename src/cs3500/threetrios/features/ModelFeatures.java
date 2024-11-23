package cs3500.threetrios.features;

import cs3500.threetrios.model.Player;

public interface ModelFeatures {
  void notifyTurnChange(Player player);

  void notifyGameOver(Player winner);
}