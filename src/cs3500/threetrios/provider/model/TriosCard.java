package cs3500.threetrios.provider.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the interface for a card in the game of Trios.
 */
public interface TriosCard {

  /**
   * Returns the color of this card.
   *
   * @return the color of this card
   */
  PlayerColor getColor();

  /**
   * Compares this card's values with its neighbors and flips their color if this card has a
   * higher number in that direction.
   *
   * @param neighbors a map containing this card's neighbors and their relative direction
   */

  List<Card> battle(Map<String, Card> neighbors);


  /**
   * Compares this card's value in the given direction with the provided value.
   *
   * @param direction the direction to compare (north, south, east, west)
   * @param value     the value to compare against
   * @return true if this card's value in the given direction is less than
   *     the provided value, and false otherwise
   */
  boolean compare(String direction, int value);

  /**
   * Returns the value of this card in the given direction.
   *
   * @param direction the direction to compare (north, south, east, west)
   * @return the value of this card in the given direction
   * @throws IllegalArgumentException if the direction is invalid
   */
  int getDirection(String direction);

}
