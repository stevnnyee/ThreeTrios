package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;

public class AIMove {
  private final Card card;
  private final Position position;
  private final int score;

  public AIMove(Card card, Position position, int score) {
    this.card = card;
    this.position = position;
    this.score = score;
  }

  public Card getCard() { return card; }
  public Position getPosition() { return position; }
  public int getScore() { return score; }

  public int comparePosition(AIMove other) {
    if (this.position.row != other.position.row) {
      return Integer.compare(this.position.row, other.position.row);
    }
    return Integer.compare(this.position.col, other.position.col);
  }
}