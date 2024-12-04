package cs3500.threetrios.provider.controller;

import cs3500.threetrios.provider.model.Card;
import cs3500.threetrios.provider.model.PlayerColor;

/**
 * Interface for listening to player actions.
 * Implemented by controllers to receive notifications when players want to make moves.
 */
public interface PlayerActionListener {
  /**
   * Called when a player wants to make a move.
   *
   * @param color    the color of the player making the move
   * @param cardName the name of the card to play
   * @param row      the row to play at
   * @param col      the column to play at
   */
  void onPlayerMove(PlayerColor color, String cardName, int row, int col);


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
   * Called when the game starts. Acts as a catalyst for AI players to begin gameplay. 
   */
  void onGameStart();
}