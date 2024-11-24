package cs3500.threetrios.features;


import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;

/**
 * Interface for handling user interaction events from the view component of the game,
 * such as selecting cards or grid cells.
 */
public interface ViewFeatures {

  /**
   * Handles the event when a player selects a card from their hand.
   * This method is called when the user clicks or otherwise selects a card in the game interface.
   *
   * @param player the Player who selected the card
   * @param card   the Card that was selected
   * @throws IllegalArgumentException if either parameter is null
   */
  void onCardSelected(Player player, Card card);

  /**
   * Handles the event when a cell on the game grid is selected.
   * This method is called when the user clicks or otherwise selects a position on the game board.
   *
   * @param row the row index of the selected cell
   * @param col the column index of the selected cell
   * @throws IllegalArgumentException if the indices are out of bounds
   */
  void onCellSelected(int row, int col);

}