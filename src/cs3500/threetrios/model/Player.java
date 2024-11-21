package cs3500.threetrios.model;

import java.util.List;

import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;

/**
 * Interface that represents a player in the ThreeTrios game,
 * which has a color and a hand of cards.
 */
public interface Player {

  /**
   * Gets the color of the player.
   *
   * @return color
   */
  String getColor();

  /**
   * Gets the current hand of the player.
   *
   * @return current hand
   */
  List<Card> getHand();

  /**
   * Adds a card to a player's hand.
   *
   * @param card card
   */
  void addCardToHand(Card card);

  /**
   * Removes a card from a player's hand.
   *
   * @param card card
   */
  void removeCardFromHand(Card card);

  /**
   * Returns the count of owned cards.
   *
   * @param grid grid
   * @return count of owned cards
   */
  int countOwnedCards(Grid grid);

  void setStrategy(AIStrategy strategy);

  AIMove getNextMove(MainModelInterface model);
}