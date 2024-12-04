package cs3500.threetrios.provider.model;

import java.awt.Color;

/**
 * Represents a cell in the Trios game.
 */
public interface Cell {

  /**
   * Adds a card to this given cell.
   *
   * @param card to be placed.
   * @param makeMove indicates whether a card is being placed or not.
   * @throws IllegalStateException if there exists a card already.
   * @throws IllegalArgumentException if the card is not in a valid hand.
   */
  void updateCard(Card card, boolean makeMove);

  /**
   * Returns true if this CardCell is empty, or playable.
   */
  boolean isEmpty();

  /**
   * Checks if this given cell is a hole.
   *
   * @return true or false if hole.
   */
  boolean isHole();

  /**
   * Returns a copy of this cell.
   *
   * @return a copy of this cell.
   */
  Cell clone();

  /**
   * Returns the color of this cell.
   */
  Color getColor();

}

