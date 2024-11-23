package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;

/**
 * AIMove class that represents a potential move in the ThreeTrios game combining a card to be
 * played, its intended position on the game board, and the score of this move.
 * This class is primarily used by AI strategies to evaluate and compare different
 * possible moves in the game.
 */
public class AIMove {
  private final Card card;
  private final Position position;
  private final int score;

  /**
   * Constructs a new AIMove with the specified card, position, and score.
   *
   * @param card     the card to be played
   * @param position the position of the card to be placed
   * @param score    the evaluated score of the move
   */
  public AIMove(Card card, Position position, int score) {
    if (position == null) {
      throw new IllegalArgumentException("Position cannot be null");
    }
    this.card = card;
    this.position = position;
    this.score = score;
  }

  /**
   * Returns the card associated with the AI move.
   *
   * @return the card to be played
   */
  public Card getCard() {
    return card;
  }

  /**
   * Returns the row coordinate for this move.
   *
   * @return the row number
   */
  public int getRow() {
    return position.row;
  }

  /**
   * Returns the column coordinate for this move.
   *
   * @return the column number
   */
  public int getCol() {
    return position.col;
  }

  /**
   * Returns the position associated with the AI move.
   *
   * @return the position of the card
   */
  public Position getPosition() {
    return position;
  }

  /**
   * Returns the score for the current move.
   *
   * @return score assigned to the current move
   */
  public int getScore() {
    return score;
  }

  /**
   * Compares this move's position with another move's position, first by row,
   * then by column. This method aids in determining the behavior in AI strategies.
   *
   * @param other the other AI move
   * @return positive if the position comes after other,
   *         negative if the position comes before,
   *         0 if the positions are the same
   */
  public int comparePosition(AIMove other) {
    if (this.position.row != other.position.row) {
      return Integer.compare(this.position.row, other.position.row);
    }
    return Integer.compare(this.position.col, other.position.col);
  }
}