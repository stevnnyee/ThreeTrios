package cs3500.threetrios.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Grid interface for the ThreeTrios game.
 */
public class ThreeTriosGrid implements Grid {
  private final int rows;
  private final int cols;
  private final boolean[][] holes;
  private final Card[][] cards;
  private final int cardCellCount;

  /**
   * Constructs a ThreeTriosGrid with the specified dimensions and holes.
   *
   * @param rows  number of rows
   * @param cols  number of columns
   * @param holes an array representing the hole locations
   * @throws IllegalArgumentException if the grid dimensions are invalid
   *                                  or the number of card cells is even
   */
  public ThreeTriosGrid(int rows, int cols, boolean[][] holes) {
    if (rows <= 0 || cols <= 0) {
      throw new IllegalArgumentException("Grid dimensions must be positive");
    }
    if (holes.length != rows || holes[0].length != cols) {
      throw new IllegalArgumentException("Holes array dimensions do not match grid dimensions");
    }

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
    System.out.println("Attempting to place card at (" + row + ", " + col + ")");

    if (holes[row][col]) {
      throw new IllegalStateException("Cannot place card in a hole");
    }
    if (cards[row][col] != null) {
      throw new IllegalStateException("Position already contains a card");
    }
    cards[row][col] = card;
    System.out.println("Card placed successfully.");
  }

  @Override
  public int getCardCellCount() {
    return cardCellCount;
  }

  @Override
  public boolean isFull() {
    return getEmptyCells().isEmpty();
  }

  @Override
  public List<int[]> getEmptyCells() {
    List<int[]> emptyCells = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        if (!isHole(i, j) && cards[i][j] == null) {
          emptyCells.add(new int[]{i, j});
        }
      }
    }
    return emptyCells;
  }

  /**
   * Checks to see if the coordinate is valid within the bounds of the set game.
   *
   * @param row the row coordinate
   * @param col the column coordinate
   */
  private void validatePosition(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
      throw new IllegalArgumentException("Position out of bounds");
    }
  }

  /**
   * Counts the total number of cells that are not holes.
   *
   * @return cell count
   */
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
}

