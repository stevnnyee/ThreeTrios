package cs3500.threetrios.controller;

import java.util.List;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.strategy.AIStrategy;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.strategy.AIMove;

public class HumanPlayer implements Player {
  private final Player basePlayer;
  private final ViewFeatures features;

  public HumanPlayer(Player basePlayer, ViewFeatures features) {
    this.basePlayer = basePlayer;
    this.features = features;
  }

  @Override
  public String getColor() {
    return basePlayer.getColor();
  }

  @Override
  public List<Card> getHand() {
    return basePlayer.getHand();
  }

  @Override
  public void addCardToHand(Card card) {
    basePlayer.addCardToHand(card);
  }

  @Override
  public void removeCardFromHand(Card card) {
    basePlayer.removeCardFromHand(card);
  }

  @Override
  public int countOwnedCards(Grid grid) {
    return basePlayer.countOwnedCards(grid);
  }

  @Override
  public void setStrategy(AIStrategy strategy) {
    throw new UnsupportedOperationException("Human players don't use strategies");
  }

  @Override
  public AIMove getNextMove(MainModelInterface model) {
    throw new UnsupportedOperationException("Human players don't use AI moves");
  }
}