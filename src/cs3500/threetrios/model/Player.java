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

  /**
   * Sets the AI strategy that this player will use to determine their moves.
   * This strategy defines the decision-making logic for the player's turns.
   *
   * @param strategy the AI strategy implementation to be used by this player
   * @throws IllegalArgumentException if the provided strategy is null
   */
  void setStrategy(AIStrategy strategy);

  /**
   * Determines and returns the next move for this player using their current AI strategy
   * and the current state of the game. The move is calculated based on the strategy's
   * logic and the information available in the game model.
   *
   * @param model the current game state and rules interface
   * @return an AIMove object representing the player's chosen move
   * @throws IllegalStateException    if no strategy has been set for this player
   * @throws IllegalArgumentException if the provided model is null
   */
  AIMove getNextMove(MainModelInterface model);
}