package cs3500.threetrios.provider.view;
/**
 * Controller interface for the Three Trios game.
 */
public interface TriosController {
  /**
   * Handles mouse click events on the game board.
   *
   * @param x the x coordinate of the click
   * @param y the y coordinate of the click
   */
  void handleCellClick(int x, int y);
}