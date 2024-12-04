package cs3500.threetrios.provider.model;

import java.util.List;

import cs3500.threetrios.provider.controller.PlayerActionListener;

/**
 * Interface for player actions in ThreeTrios game.
 * Both human and machine players implement this interface.
 */
public interface Player {
  /**
   * Gets the color of this player.
   *
   * @return the player's color
   */
  PlayerColor getColor();
  
  /**
   * Gets a read-only copy of this player's hand.
   *
   * @return a defensive copy of the player's hand
   */
  List<Card> getHand();

  /**
   * Notifies listeners that this player wants to make a move.
   * For human players, this is triggered by the view.
   * For machine players, this is triggered by their strategy.
   *
   * @param cardName the name of the card to play
   * @param row the row to play at
   * @param col the column to play at
   */
  void notifyMove(String cardName, int row, int col);


  /**
   * Makes a move in the game with the given card and location.
   * @param card the card to play
   * @param row the row to play the card
   * @param col the column to play the card
   *
   * @throws IllegalArgumentException if the move is invalid
   * @throws IllegalStateException if the game is over
   * @throws IllegalStateException if it is not the player's turn
   *
   */
  void makeMove(Card card, int row, int col);

  /**
   * Adds a listener for player actions.
   * 
   * @param listener the listener to add
   * @throws IllegalArgumentException if the listener is null
   */
  void addMoveListener(PlayerActionListener listener);
  
  /**
   * Requests the player to make a move using the current game state.
   * @param model The current game model
   */
  void requestMove(TriosModel model);
}