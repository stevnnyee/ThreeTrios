package cs3500.threetrios.features;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;

/**
 * A mock class for mock view features that will be used for testing.
 */
public class MockViewFeatures implements ViewFeatures {
  private final StringBuilder log;

  /**
   * Constructs a mock for View Features.
   *
   * @param log the log to print
   */
  public MockViewFeatures(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void onCardSelected(Player player, Card card) {
    log.append("Card selected\n");
  }

  @Override
  public void onCellSelected(int row, int col) {
    log.append("Cell selected: ").append(row).append(",").append(col).append("\n");
  }
}