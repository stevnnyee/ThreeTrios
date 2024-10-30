package cs3500.threetrios.model;

import java.util.List;


/**
 * Interface that represents a Grid in the ThreeTrios game.
 */
public interface Grid {

  /**
   * Gets the number of rows in the grid.
   *
   * @return number of rows
   */
  int getRows();

  /**
   * Gets the number of columns in the grid.
   *
   * @return number of columns
   */
  int getCols();

  /**
   * Check if a cell is a hole.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return true if the cell is a hole, false otherwise
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  boolean isHole(int row, int col);

  /**
   * Checks if a cell is empty.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return true if the cell is empty, false otherwise
   * @throws IllegalArgumentException if the coordinates are invalid
   */
  boolean isEmpty(int row, int col);

  /**
   * Gets the card at the specified cell.
   *
   * @param row the row of the cell
   * @param col the column of the cell
   * @return the card of the cell
   * @throws IllegalArgumentException if the coordinates are invalid or if the cell is a hole
   */
  Card getCard(int row, int col);

  /**
   * Places a card at a sepcific cell.
   *
   * @param row  the row of the cell
   * @param col  the column of the cell
   * @param card the card to place
   * @throws IllegalArgumentException if the coordinates are invalid,
   *                                  the cell is a hole,
   *                                  or already occupied
   */
  void placeCard(int row, int col, Card card);

  /**
   * Returns the count of cells, excluding cells with holes.
   *
   * @return cell count
   */
  int getCardCellCount();

  /**
   * Check if the grid is full-- all card cells are occupied.
   *
   * @return true if the grid is full, false otherwise
   */
  boolean isFull();

  /**
   * Get a list of all empty card cells.
   *
   * @return a list of coordinates of empty card cells
   */
  List<int[]> getEmptyCells();
}