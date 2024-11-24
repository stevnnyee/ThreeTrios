package cs3500.threetrios.view;


import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;

/**
 * Interface for the main game frame.
 */
public interface ThreeTriosFrame {
  /**
   * Refreshes the view to reflect current game state.
   */
  void refresh();

  /**
   * Shows the frame.
   */
  void display();

  /**
   * Sets the title of the game window frame.
   *
   * @param title the text to display as the window title
   * @throws IllegalArgumentException if the title parameter is null
   */
  void setTitle(String title);

  /**
   * Sets the currently selected card and its associated player in the view.
   *
   * @param card   the card that has been selected
   * @param player the player who selected the card
   * @throws IllegalArgumentException if either parameter is null
   */
  void setSelectedCard(Card card, Player player);

  /**
   * Adds a features listener to handle view-related events and user interactions.
   * This enables communication between the view and other components of the application.
   *
   * @param features the ViewFeatures listener to handle view events
   * @throws IllegalArgumentException if the features parameter is null
   */
  void addViewFeatures(ViewFeatures features);
}
