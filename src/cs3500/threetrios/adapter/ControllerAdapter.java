package cs3500.threetrios.adapter;

import java.util.List;

import cs3500.threetrios.controller.AIPlayer;
import cs3500.threetrios.features.ModelFeatures;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.model.Card;
import cs3500.threetrios.provider.view.ThreeTriosView;

public class ControllerAdapter implements TriosController, ModelFeatures {
  private final MainModelInterface model;
  private final ReadOnlyTTAdapter modelAdapter;
  private final ThreeTriosView view;
  private final Player controlledPlayer;
  private Card selectedCard;
  private PlayerColor selectedColor;

  public ControllerAdapter(MainModelInterface model, ThreeTriosView view, Player player) {
    this.model = model;
    this.modelAdapter = new ReadOnlyTTAdapter(model);
    this.view = view;
    this.controlledPlayer = player;
    updateTitle(); // Set initial title
  }

  private void updateTitle() {
    String title = "Three Trios - " + controlledPlayer.getColor();
    if (model.isGameOver()) {
      Player winner = model.getWinner();
      title = "Game Over - " + (winner != null ? winner.getColor() + " WINS!" : "TIE!");
    } else if (model.getCurrentPlayer().equals(controlledPlayer)) {
      title += " (Your Turn)";
    }
    view.setTitle(title);
  }

  @Override
  public void handleCellClick(int x, int y) {
    int cellWidth = view.getCellWidth();
    int cellHeight = view.getCellHeight();

    int row = y / cellHeight;
    int col = x / cellWidth;

    System.out.println("Click at: x=" + x + ", y=" + y + " (row=" + row + ", col=" + col + ")");

    if (col == 0 || col == modelAdapter.numCols() + 1) {
      PlayerColor color = (col == 0) ? PlayerColor.RED : PlayerColor.BLUE;
      handleHandClick(row, col, color);
    } else {
      handleBoardClick(row, col - 1);
    }
  }

  private void handleHandClick(int index, int col, PlayerColor color) {
    String currentColor = model.getCurrentPlayer().getColor();
    String clickedColor = (color == PlayerColor.RED) ? "RED" : "BLUE";

    if (!currentColor.equals(clickedColor)) {
      System.out.println("Not " + clickedColor + "'s turn!");
      return;
    }

    List<Card> hand = modelAdapter.getHand(color);
    if (index >= 0 && index < hand.size()) {
      selectedCard = hand.get(index);
      selectedColor = color;
      view.selectCard(index, col);
      System.out.println("Selected card at index " + index + " for " + color);
    }
  }

  private void handleBoardClick(int row, int col) {
    if (selectedCard == null) {
      System.out.println("No card selected!");
      return;
    }

    if (!modelAdapter.isValidMove(row, col)) {
      System.out.println("Invalid move position: " + row + "," + col);
      return;
    }

    cs3500.threetrios.model.Card ourCard = null;
    for (cs3500.threetrios.model.Card card : model.getPlayerHand(model.getCurrentPlayer())) {
      if (card.getName().equals(selectedCard.getName())) {
        ourCard = card;
        break;
      }
    }

    if (ourCard != null) {
      try {
        model.placeCard(row, col, ourCard);
        selectedCard = null;
        selectedColor = null;
        System.out.println("Card placed at " + row + "," + col);
        view.selectCard(-1, -1);  // Clear selection
        view.refresh();
      } catch (Exception e) {
        System.out.println("Error placing card: " + e.getMessage());
      }
    }
  }

  @Override
  public void notifyTurnChange(Player player) {
    updateTitle();
    view.refresh();
    view.makeVisible();
    if (player.getColor().equals(controlledPlayer.getColor()) &&
            controlledPlayer instanceof AIPlayer) {
      makeAIMove();
    }
  }

  private void makeAIMove() {
    if (model.isGameOver()) {
      return;
    }

    try {
      AIPlayer aiPlayer = (AIPlayer) controlledPlayer;
      cs3500.threetrios.strategy.AIMove move = aiPlayer.getNextMove(model);
      if (move != null && move.getCard() != null) {
        model.placeCard(move.getRow(), move.getCol(), move.getCard());
        view.refresh();
      }
    } catch (Exception e) {
      System.err.println("Error making AI move: " + e.getMessage());
    }
  }

  @Override
  public void notifyGameOver(Player winner) {
    updateTitle();
    view.refresh();
  }
}