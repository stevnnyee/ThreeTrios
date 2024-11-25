package cs3500.threetrios.controller;

import cs3500.threetrios.features.ModelFeatures;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.view.ThreeTriosFrame;
import cs3500.threetrios.view.ThreeTriosSwingView;
import cs3500.threetrios.strategy.AIMove;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Controller for the ThreeTrios game that manages game flow and coordinates between
 * the model, view, and players.
 * Handles both human and AI player interactions, processing view events for human players
 * and automatically triggering moves for AI players.
 */
public class ThreeTriosController implements ModelFeatures, ViewFeatures {
  private final MainModelInterface model;
  private final ThreeTriosFrame view;
  private final Player controlledPlayer;
  private Card selectedCard;
  private boolean isMyTurn;

  /**
   * Constructs a new controller for the ThreeTrios game.
   * Initializes the connection between the parameters and sets up event listeners.
   *
   * @param model  the game model interface
   * @param view   the game view interface
   * @param player the player this controller is responsible for
   * @throws IllegalArgumentException if any parameter is null
   */
  public ThreeTriosController(MainModelInterface model, ThreeTriosFrame view, Player player) {
    if (model == null || view == null || player == null) {
      throw new IllegalArgumentException("No arguments can be null");
    }

    this.model = model;
    this.view = view;
    this.controlledPlayer = player;

    Player currentPlayer = model.getCurrentPlayer();
    this.isMyTurn = (currentPlayer != null
            && player != null
            && currentPlayer.getColor().equals(player.getColor()));
    model.addFeaturesListener(this);

    if (view instanceof ThreeTriosSwingView) {
      ((ThreeTriosSwingView) view).addViewFeatures(this);
    }

    updateViewTitle();
  }

  @Override
  public void onCardSelected(Player player, Card card) {
    if (!isMyTurn) {
      clearSelection();
      showErrorDialog("Not your turn!");
      return;
    }

    if (!player.getColor().equals(controlledPlayer.getColor())) {
      clearSelection();
      showErrorDialog("You can only select cards from your own hand!");
      return;
    }

    selectedCard = card;
    view.setSelectedCard(card, player);
  }

  @Override
  public void onCellSelected(int row, int col) {
    if (!isMyTurn) {
      clearSelection();
      showErrorDialog("Not your turn!");
      return;
    }

    if (selectedCard == null) {
      showErrorDialog("Please select a card first!");
      return;
    }

    try {
      if (model.canPlaceCard(row, col, selectedCard)) {
        model.placeCard(row, col, selectedCard);
        clearSelection();
        view.refresh();
      } else {
        showErrorDialog("Invalid move!");
      }
    } catch (Exception e) {
      showErrorDialog(e.getMessage());
      clearSelection();
    }
  }

  @Override
  public void notifyGameOver(Player winner) {
    clearSelection();
    view.refresh();
    String message;
    Player redPlayer = model.getPlayers().get(0);
    Player bluePlayer = model.getPlayers().get(1);

    int redScore = model.getPlayerScore(redPlayer);
    int blueScore = model.getPlayerScore(bluePlayer);

    if (winner != null) {
      message = String.format(
              "%s wins!\nFinal Score:\nRED: %d\nBLUE: %d",
              winner.getColor(),
              redScore,
              blueScore
      );
    } else {
      message = String.format(
              "It's a tie!\nFinal Score:\nRED: %d\nBLUE: %d",
              redScore,
              blueScore
      );
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JOptionPane.showMessageDialog(
                null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE
        );
      }
    });
  }

  /**
   * Handles the AI player's turn by computing and executing their move.
   * This method is called automatically when it becomes an AI player's turn.
   */
  private void makeAIMove() {
    if (!isMyTurn || model.isGameOver()) {  // Add isGameOver check
      return;
    }

    try {
      AIPlayer aiPlayer = (AIPlayer) controlledPlayer;

      AIMove move = aiPlayer.getNextMove(model);
      if (move == null) {
        return;
      }

      Card card = move.getCard();
      if (card == null) {
        return;
      }

      if (model.canPlaceCard(move.getRow(), move.getCol(), card)) {
        model.placeCard(move.getRow(), move.getCol(), card);
        view.refresh();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyTurnChange(Player player) {
    boolean wasMyTurn = isMyTurn;
    isMyTurn = player != null && player.getColor().equals(controlledPlayer.getColor());
    updateViewTitle();
    view.refresh();

    // Only make AI move if game isn't over
    if (isMyTurn && !wasMyTurn && controlledPlayer instanceof AIPlayer && !model.isGameOver()) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          makeAIMove();
        }
      });
    }
  }

  /**
   * Updates the view's title to reflect the current player's turn status.
   * The title shows the player's color and whether it's their turn or they're waiting.
   */
  private void updateViewTitle() {
    String playerStatus = isMyTurn ? "Your Turn" : "Waiting";
    String title = String.format("%s Player - %s", controlledPlayer.getColor(), playerStatus);
    view.setTitle(title);
  }

  /**
   * Updates the view's title to reflect the current player's turn status.
   * The title shows the player's color and whether it's their turn or they're waiting.
   */
  private void clearSelection() {
    selectedCard = null;
    view.setSelectedCard(null, null);
  }

  /**
   * Displays an error message to the user in a dialog box.
   *
   * @param message the error message to display
   */
  private void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Initiates the game by updating the view and triggering the first AI move if applicable.
   * Should be called only when all game components are properly initialized.
   */
  public void startGame() {
    updateViewTitle();
    view.display();

    if (isMyTurn && controlledPlayer instanceof AIPlayer) {
      makeAIMove();
    }
  }
}