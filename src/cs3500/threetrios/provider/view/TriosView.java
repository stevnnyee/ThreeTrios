package cs3500.threetrios.provider.view;

import cs3500.threetrios.provider.controller.TriosController;

/**
 * The interface for all types of View. Can be text-based or GUI-based.
 */
public interface TriosView {

  /**
   * Adds a click listener to the view.
   *
   * @param listener the listener to add
   */
  void addClickListener(TriosController listener);


  /**
   * Refresh the view to reflect any changes in the game state.
   */
  void refresh();

  /**
   * Make the view visible to start the game session.
   */
  void makeVisible();

  /**
   * Gets the width of each cell in the view.
   *
   * @return the width of each cell
   */
  int getCellWidth();

  /**
   * Gets the height of each cell in the view.
   *
   * @return the height of each cell
   */
  int getCellHeight();

  /**
   * Selects a card.
   *
   * @param row the row of the card to select
   * @param col the col of the card to select
   * @return true if the card was selected, false otherwise
   */
  boolean selectCard(int row, int col);
}
