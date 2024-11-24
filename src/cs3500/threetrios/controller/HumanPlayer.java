package cs3500.threetrios.controller;

import java.util.List;

import cs3500.threetrios.model.Player;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.strategy.AIStrategy;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.strategy.AIMove;

/**
 * A decorator for a human-controlled player in the ThreeTrios game.
 * This implementation delegates basic player operations to a base player instance while managing
 * the connection to the view features for human interaction.
 * Human players make moves through the view interface manually.
 */
public class HumanPlayer implements Player {
  private final Player basePlayer;

  /**
   * Constructs a new human player that wraps around an existing base player.
   * Links the player to the view features interface for handling user interactions.
   *
   * @param basePlayer the underlying player implementation to delegate basic operations to
   * @param features   the view features interface for handling user interactions
   * @throws IllegalArgumentException if either parameter is null
   */
  public HumanPlayer(Player basePlayer, ViewFeatures features) {
    if (basePlayer == null || features == null) {
      throw new IllegalArgumentException("Baseplayer or features cannot be null.");
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
    throw new UnsupportedOperationException("Human players don't use strategies");
  }

  @Override
  public AIMove getNextMove(MainModelInterface model) {
    throw new UnsupportedOperationException("Human players don't use AI moves");
  }
}