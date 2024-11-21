package cs3500.threetrios.controller;

import cs3500.threetrios.features.ModelFeatures;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.view.ThreeTriosFrame;
import javax.swing.JOptionPane;

public class ThreeTriosController implements ModelFeatures, ViewFeatures {
  private final MainModelInterface model;
  private final ThreeTriosFrame view;
  private final Player controlledPlayer;
  private Card selectedCard;
  private boolean isMyTurn;

  public ThreeTriosController(MainModelInterface model, ThreeTriosFrame view, Player player) {
    this.model = model;
    this.view = view;
    this.controlledPlayer = player;
    this.isMyTurn = model.getCurrentPlayer() == controlledPlayer;
    updateViewTitle();
  }

  @Override
  public void onCardSelected(Player player, Card card) {
    if (!isMyTurn) {
      showError("Not your turn!");
      view.setSelectedCard(null, null);  // Clear selection
      return;
    }
    if (player != controlledPlayer) {
      showError("You can only select cards from your own hand!");
      view.setSelectedCard(null, null);  // Clear selection
      return;
    }
    selectedCard = card;
    view.setSelectedCard(card, player);
  }

  @Override
  public void onCellSelected(int row, int col) {
    if (!isMyTurn) {
      showError("Not your turn!");
      return;
    }
    if (selectedCard == null) {
      showError("Please select a card from your hand first!");
      return;
    }
    try {
      if (model.canPlaceCard(row, col, selectedCard)) {
        model.placeCard(row, col, selectedCard);
        selectedCard = null;
        view.refresh();
      } else {
        showError("Invalid move!");
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      showError(e.getMessage());
    }
  }

  @Override
  public void notifyTurnChange(Player player) {
    isMyTurn = (player == controlledPlayer);
    selectedCard = null;  // Clear selection on turn change
    view.setSelectedCard(null, null);
    view.refresh();
  }

  @Override
  public void notifyGameOver(Player winner) {
    String message;
    if (winner != null) {
      message = String.format("%s wins!\nScore: %d to %d",
              winner.getColor(),
              model.getPlayerScore(model.getPlayers().get(0)),
              model.getPlayerScore(model.getPlayers().get(1)));
    } else {
      message = "Game is a tie!";
    }
    JOptionPane.showMessageDialog(null, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void updateViewTitle() {
    String title = String.format("%s Player - %s",
            controlledPlayer.getColor(),
            isMyTurn ? "Your Turn" : "Waiting");
    view.setTitle(title);
  }

  public void startGame() {
    view.display();
  }
}