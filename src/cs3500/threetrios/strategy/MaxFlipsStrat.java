package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

import static cs3500.threetrios.strategy.StrategyUtil.getDefaultMove;

public class MaxFlipsStrat implements AIStrategy {
  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    List<Card> hand = model.getPlayerHand(player);
    List<AIMove> possibleMoves = new ArrayList<>();

    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (int cardIdx = 0; cardIdx < hand.size(); cardIdx++) {
          Card card = hand.get(cardIdx);
          if (model.canPlaceCard(row, col, card)) {
            int flips = model.getFlippableCards(row, col, card);
            possibleMoves.add(new AIMove(card, new Position(row, col), flips));
          }
        }
      }
    }

    if (possibleMoves.isEmpty()) {
      return getDefaultMove(model, player);
    }

    possibleMoves.sort(new MoveComparator());
    return possibleMoves.get(0);
  }
}
