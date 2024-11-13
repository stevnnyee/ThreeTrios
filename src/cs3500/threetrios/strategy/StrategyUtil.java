package cs3500.threetrios.strategy;

import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

public class StrategyUtil {
  public static AIMove getDefaultMove(MainModelInterface model, Player player) {
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (!model.isHole(row, col) && model.getCardAt(row, col) == null) {
          List<Card> hand = model.getPlayerHand(player);
          if (!hand.isEmpty()) {
            return new AIMove(hand.get(0), new Position(row, col), 0);
          }
        }
      }
    }
    throw new IllegalStateException("No valid moves available");
  }
}