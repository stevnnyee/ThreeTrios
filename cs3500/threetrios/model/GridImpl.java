package cs3500.threetrios.model;

import java.util.List;

import javax.swing.text.Position;

public class GridImpl implements Grid {
  private final int rows;
  private final int cols;
  private final boolean[][] holes;
  private final Card[][] cards;
  private final int cardCellCount;

  public GridImpl(int rows, int cols, boolean[][] holes) {
    this.rows = rows;
    this.cols = cols;
    this.holes = new boolean[rows][cols];
    this.cards = new Card[rows][cols];

    for (int i = 0; i < rows; i++) {
      System.arraycopy(holes[i], 0, this.holes[i], 0, cols);
    }

    this.cardCellCount = countCardCells();
    if (cardCellCount % 2 == 0) {
      throw new IllegalArgumentException("Grid must have odd number of card cells");
    }
  }

  private int countCardCells() {
    int count = 0;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (!holes[i][j]) {
          count++;
        }
      }
    }
    return count;
  }

  @Override
  public int getRows() {
    return rows;
  }

  @Override
  public int getCols() {
    return cols;
  }

  @Override
  public boolean isHole(int row, int col) {
    validatePosition(row, col);
    return holes[row][col];
  }

  @Override
  public boolean isEmpty(int row, int col) {
    return false;
  }

  @Override
  public Card getCard(int row, int col) {
    validatePosition(row, col);
    return cards[row][col];
  }

  @Override
  public void placeCard(int row, int col, Card card) {
    validatePosition(row, col);
    if (holes[row][col]) {
      throw new IllegalStateException("Cannot place card in a hole");
    }
    if (cards[row][col] != null) {
      throw new IllegalStateException("Position already contains a card");
    }
    cards[row][col] = card;
  }

  @Override
  public List<Position> getAdjacentCardPositions(Position pos) {
    return List.of();
  }

  @Override
  public boolean isFull() {
    return false;
  }

  @Override
  public int getCardCellCount() {
    return cardCellCount;
  }

  private void validatePosition(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IllegalArgumentException("Position out of bounds");
    }
  }
}
