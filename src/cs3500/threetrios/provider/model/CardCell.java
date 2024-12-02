package cs3500.threetrios.provider.model;

import java.awt.Color;

/**
 * Represents either an empty cell or a card cell.
 */
public class CardCell implements Cell {
  private Card card;

  /**
   * Creates a new, empty CardCell.
   */
  public CardCell() {
    this.card = null;
  }

  /**
   * If present, returns the card in the cell.
   *
   * @return the card inside.
   * @throws IllegalStateException if no current card.
   */
  public Card getCard() {
    if (card == null) {
      throw new IllegalStateException("This cell is empty.");
    }
    return this.card;
  }

  @Override
  public void updateCard(Card card, boolean makeMove) {
    if (!this.isEmpty()) {
      throw new IllegalStateException("There is already a card here.");
    }
    if (card.getColor() == null) {
      throw new IllegalArgumentException("The card must be colored.");
    }
    if (makeMove) {
      this.card = card;
    }
  }

  @Override
  public boolean isEmpty() {
    return this.card == null;
  }

  @Override
  public String toString() {
    if (this.isEmpty()) {
      return "_";
    }
    return this.card.toString();
  }

  @Override
  public boolean isHole() {
    return false;
  }


  @Override
  public Cell clone() {
    CardCell c = new CardCell();
    if (this.card == null) {
      return c;
    }
    c.updateCard(new Card(this.card), true);
    return c;
  }

  @Override
  public Color getColor() {
    if (this.isEmpty()) {
      return Color.YELLOW;
    }
    Color customRed = new Color(209, 70, 70);
    Color customBlue = new Color(0, 128, 255);
    return (this.card.getColor() == PlayerColor.RED) ? customRed : customBlue;
  }
}
