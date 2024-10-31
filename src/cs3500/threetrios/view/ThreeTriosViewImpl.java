package cs3500.threetrios.view;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * The implementation of ThreeTriosView, creating a new view of the game.
 */
public class ThreeTriosViewImpl implements ThreeTriosView {
  private final MainModelInterface model;

  /**
   * Constructs a textual view with the given model.
   *
   * @param model the game model to visualize
   * @throws IllegalArgumentException if model is null
   */
  public ThreeTriosViewImpl(MainModelInterface model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  @Override
  public String toString() {
    StringBuilder output = new StringBuilder();
    Player currentPlayer = model.getCurrentPlayer();

    output.append("Player: ").append(currentPlayer.getColor()).append("\n");

    Grid grid = model.getGrid();
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        if (j > 0) {
          output.append(" ");
        }
        if (grid.isHole(i, j)) {
          output.append(" ");
        } else {
          Card card = grid.getCard(i, j);
          if (card == null) {
            output.append("_");
          } else {
            output.append(card.getOwner().getColor().charAt(0));
          }
        }
      }
      output.append("\n");
    }

    output.append("Hand:\n");
    for (Card card : model.getPlayerHand(currentPlayer)) {
      output.append(card.getName()).append(" ")
              .append(formatAttackValue(card.getAttackPower(Direction.NORTH))).append(" ")
              .append(formatAttackValue(card.getAttackPower(Direction.SOUTH))).append(" ")
              .append(formatAttackValue(card.getAttackPower(Direction.EAST))).append(" ")
              .append(formatAttackValue(card.getAttackPower(Direction.WEST)))
              .append("\n");
    }

    return output.toString();
  }

  /**
   * Formats attack values, converting 10 to 'A' as per game specifications.
   *
   * @param value the attack value to format
   * @return formatted string representation
   */
  private String formatAttackValue(int value) {
    return value == 10 ? "A" : String.valueOf(value);
  }
}