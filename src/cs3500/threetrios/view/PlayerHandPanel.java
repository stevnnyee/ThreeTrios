package cs3500.threetrios.view;

import java.awt.*;

/**
 * Interface for the player hand panel.
 */
public interface PlayerHandPanel {
  /**
   * Gets the card size based on panel dimensions and hand size.
   *
   * @return The size of each card in pixels
   */
  Dimension getCardSize();

  /**
   * Refreshes the hand panel.
   */
  void refresh();
}
