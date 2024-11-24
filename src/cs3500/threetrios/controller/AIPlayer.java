package cs3500.threetrios.controller;

import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;

/**
 * A decorator for an AI-controlled player in the ThreeTrios game.
 * This implementation uses a strategy pattern to determine moves and gives basic player operations
 * to a base player instance.
 * The AI player computes moves automatically when it's their turn instead of waiting for input.
 */
public class AIPlayer implements Player {
  private final Player basePlayer;
  private AIStrategy strategy;

  /**
   * Constructs a new AI player that wraps around an existing base player.
   * The base player provides the core player functionality while this class adds
   * AI-specific behavior.
   *
   * @param basePlayer the underlying player implementation
   * @throws IllegalArgumentException if basePlayer is null
   */
  public AIPlayer(Player basePlayer) {
    if (basePlayer == null) {
      throw new IllegalArgumentException("basePlayer cannot be null");
    }
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