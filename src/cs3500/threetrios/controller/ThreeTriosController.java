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

public class ThreeTriosController implements ModelFeatures, ViewFeatures {
  private final MainModelInterface model;
  private final ThreeTriosFrame view;
  private final Player controlledPlayer;
  private Card selectedCard;
  private boolean isMyTurn;

  public ThreeTriosController(MainModelInterface model, ThreeTriosFrame view, Player player) {
    if (model == null || view == null || player == null) {
      throw new IllegalArgumentException("No arguments can be null");
    }

    this.model = model;
    this.view = view;
    this.controlledPlayer = player;

    // Initialize turn state based on current player
    Player currentPlayer = model.getCurrentPlayer();
    this.isMyTurn = (currentPlayer != null &&
            player != null &&
            currentPlayer.getColor().equals(player.getColor()));

    System.out.println("Initializing controller for " + player.getColor() +
            ", isMyTurn=" + isMyTurn);

    // Register this controller as a listener for model events
    model.addFeaturesListener(this);

    // Register with view if it's a SwingView
    if (view instanceof ThreeTriosSwingView) {
      ((ThreeTriosSwingView) view).addViewFeatures(this);
    }

    updateViewTitle();
  }

  @Override
  public void onCardSelected(Player player, Card card) {
    System.out.println(controlledPlayer.getColor() + " controller handling card selection");
    System.out.println("Is my turn: " + isMyTurn);
    System.out.println("Current player: " + model.getCurrentPlayer().getColor());

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
    System.out.println(controlledPlayer.getColor() + " controller handling cell selection");
    System.out.println("Is my turn: " + isMyTurn);

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
  public void notifyTurnChange(Player player) {
    System.out.println(controlledPlayer.getColor() + " controller notified of turn change to " +
            (player != null ? player.getColor() : "null"));

    boolean wasMyTurn = isMyTurn;
    isMyTurn = player != null && player.getColor().equals(controlledPlayer.getColor());

    System.out.println(controlledPlayer.getColor() + " turn state changed from " +
            wasMyTurn + " to " + isMyTurn);

    updateViewTitle();
    view.refresh();

    if (isMyTurn && !wasMyTurn && controlledPlayer instanceof AIPlayer) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          makeAIMove();
        }
      });
    }
  }

  @Override
  public void notifyGameOver(Player winner) {
    clearSelection();
    view.refresh();  // Refresh the view one last time

    String message;
    Player redPlayer = model.getPlayers().get(0);  // First player is RED
    Player bluePlayer = model.getPlayers().get(1); // Second player is BLUE

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

    // Use SwingUtilities.invokeLater to ensure dialog shows on EDT
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JOptionPane.showMessageDialog(
                null,  // Use null for parent to center on screen
                message,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
      }
    });
  }

  private void makeAIMove() {
    if (!isMyTurn) {
      System.out.println("Not AI's turn anymore");
      return;
    }

    try {
      AIPlayer aiPlayer = (AIPlayer) controlledPlayer;
      System.out.println("AI " + aiPlayer.getColor() + " making move");

      AIMove move = aiPlayer.getNextMove(model);
      if (move == null) {
        System.out.println("AI returned null move");
        return;
      }

      Card card = move.getCard();
      if (card == null) {
        System.out.println("AI selected null card");
        return;
      }

      if (model.canPlaceCard(move.getRow(), move.getCol(), card)) {
        System.out.println("AI placing card at " + move.getRow() + "," + move.getCol());
        model.placeCard(move.getRow(), move.getCol(), card);
        view.refresh();
      } else {
        System.out.println("AI attempted invalid move");
      }

    } catch (Exception e) {
      System.out.println("AI Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void updateViewTitle() {
    String playerStatus = isMyTurn ? "Your Turn" : "Waiting";
    String title = String.format("%s Player - %s", controlledPlayer.getColor(), playerStatus);
    view.setTitle(title);
  }

  private void clearSelection() {
    selectedCard = null;
    view.setSelectedCard(null, null);
  }

  private void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void startGame() {
    System.out.println("Starting game for " + controlledPlayer.getColor() +
            " controller, isMyTurn=" + isMyTurn);
    updateViewTitle();
    view.display();

    // If this is an AI player and it's their turn, make a move
    if (isMyTurn && controlledPlayer instanceof AIPlayer) {
      makeAIMove();
    }
  }
}