package cs3500.threetrios.view;



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

  void setTitle(String title);

  void setSelectedCard(Card card, Player player);
}
