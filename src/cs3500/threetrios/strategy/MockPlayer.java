package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * A mock implementation of the Player interface used for testing game logic and strategies.
 * This class provides a simplified player representation with basic hand management
 * and no strategic decision-making capabilities.
 */
public class MockPlayer implements Player {
  private final String color;
  private final List<Card> hand;
  // created AIStrategy as a local variable to ensure that the strategy instance is not shared,
  // which helps prevent unintended side effects.
  AIStrategy strategy;

  /**
   * Constructs a new MockPlayer with a color as its user.
   *
   * @param color the color of the player
   */
  public MockPlayer(String color) {
    this.color = color;
    this.hand = new ArrayList<>();
  }

  @Override
  public String getColor() {
    return color;
  }

  @Override
  public List<Card> getHand() {
    return new ArrayList<>(hand);
  }

  @Override
  public void addCardToHand(Card card) {
    hand.add(card);
  }

  @Override
  public void removeCardFromHand(Card card) {
    hand.remove(card);
  }

  @Override
  public int countOwnedCards(Grid grid) {
    return hand.size();
  }

  @Override
  public void setStrategy(AIStrategy strategy) {
    this.strategy = strategy;
  }

  @Override
  public AIMove getNextMove(MainModelInterface model) {
    return null;
  }
}
