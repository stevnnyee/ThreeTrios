package cs3500.threetrios.strategy;

import java.util.Objects;

/**
 * Represents a position on a game grid using row and column coordinates.
 * Rows increase from top to bottom, and columns increase from left to right.
 * (0,0) is the top-left position.
 */
public class Position {
  public final int row;
  public final int col;

  /**
   * Constructs a new Position with the given row and column.
   *
   * @param row the row
   * @param col the column
   */
  public Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Position position = (Position) o;
    return row == position.row && col == position.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
}
