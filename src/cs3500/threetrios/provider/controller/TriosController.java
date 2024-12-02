package cs3500.threetrios.provider.controller;

/**
 * Represents a controller for a Trios game. Controls the game and handles user input.
 */
public interface TriosController {

  /**
   * Handle an action in a single cell of the board, such as to make a move.
   *
   * @param x the x coordinate clicked
   * @param y the y coordinate clicked
   */
  void handleCellClick(int x, int y);
}
