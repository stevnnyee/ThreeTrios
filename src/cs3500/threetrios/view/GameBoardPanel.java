package cs3500.threetrios.view;


import java.awt.*;

/**
 * Interface for the game board panel.
 */
public interface GameBoardPanel {
  /**
   * Gets the cell size based on panel dimensions and grid size.
   * @return The size of each cell in pixels
   */
  Dimension getCellSize();

  /**
   * Refreshes the board panel.
   */
  void refresh();
}