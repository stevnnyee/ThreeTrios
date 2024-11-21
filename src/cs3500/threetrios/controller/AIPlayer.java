package cs3500.threetrios.controller;

import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;

public class AIPlayer implements Player {
  private final Player basePlayer;
  private AIStrategy strategy;

  public AIPlayer(Player basePlayer) {
    this.basePlayer = basePlayer;
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
    this.strategy = strategy;
  }

  @Override
  public AIMove getNextMove(MainModelInterface model) {
    if (strategy == null) {
      throw new IllegalStateException("No strategy set for AI player");
    }
    return strategy.findBestMove(model, this);
  }
}