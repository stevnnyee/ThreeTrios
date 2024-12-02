package cs3500.threetrios.adapter;

import java.util.List;

import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.model.Card;
import cs3500.threetrios.provider.view.ThreeTriosView;

public class ControllerAdapter implements TriosController {
  private final MainModelInterface model;
  private final ReadOnlyTTAdapter modelAdapter;
  private final ThreeTriosView view;  // Add reference to their view
  private Card selectedCard;
  private PlayerColor selectedColor;

  public ControllerAdapter(MainModelInterface model, ThreeTriosView view) {  // Add view parameter
    this.model = model;
    this.modelAdapter = new ReadOnlyTTAdapter(model);
    this.view = view;
  }

  @Override
  public void handleCellClick(int x, int y) {
    // Get cell dimensions

    int cellWidth = view.getCellWidth();
    int cellHeight = view.getCellHeight();

    // Convert pixel coordinates to grid position
    int row = y / cellHeight;
    int col = x / cellWidth;

    System.out.println("Click at: x=" + x + ", y=" + y + " (row=" + row + ", col=" + col + ")");

    // Handle clicks on player hands (col 0 is RED hand, last col is BLUE hand)
    if (col == 0 || col == modelAdapter.numCols() + 1) {
      PlayerColor color = (col == 0) ? PlayerColor.RED : PlayerColor.BLUE;
      handleHandClick(row, col, color);
    } else {
      // Handle click on game board (adjust for left hand)
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
      // Use their view's selectCard method to highlight the selection
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
      model.placeCard(row, col, ourCard);
      selectedCard = null;
      selectedColor = null;
      view.refresh(); // Make sure their view updates
    }
  }
}