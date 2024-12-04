package cs3500.threetrios.adapter;

import java.util.List;

import cs3500.threetrios.controller.AIPlayer;
import cs3500.threetrios.features.ModelFeatures;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.model.Card;

/**
 * Adapts our controller implementation to work with the provider's TriosController interface.
 * This adapter manages game flow, user interactions, and coordinates between the model and view.
 * Implements both TriosController for view interactions and ModelFeatures for model notifications.
 */
public class ControllerAdapter implements TriosController, ModelFeatures {
  private final MainModelInterface model;
  private final ReadOnlyTTAdapter modelAdapter;
  private final ViewAdapter view;  // Changed from ThreeTriosView to ViewAdapter
  private final Player controlledPlayer;
  private Card selectedCard;
  private PlayerColor selectedColor;

  /**
   * Constructs a new ControllerAdapter.
   *
   * @param model  the game model to control
   * @param view   the view adapter to display the game
   * @param player the player this controller is responsible for
   * @throws IllegalArgumentException if any parameter is null
   */
  public ControllerAdapter(MainModelInterface model, ViewAdapter view, Player player) {  // Changed parameter type
    this.model = model;
    this.modelAdapter = new ReadOnlyTTAdapter(model);
    this.view = view;
    this.controlledPlayer = player;
    updateTitle();
  }

  /**
   * Updates the view's title based on the current game state.
   * Shows the current player's color and indicates if it's their turn or if the game is over.
   */
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
        // First, find the matching provider card and select it
        PlayerColor aiColor = controlledPlayer.getColor().equals("RED") ?
                PlayerColor.RED : PlayerColor.BLUE;
        List<Card> hand = modelAdapter.getHand(aiColor);

        for (int i = 0; i < hand.size(); i++) {
          Card providerCard = hand.get(i);
          if (providerCard.getName().equals(move.getCard().getName())) {
            // Simulate card selection
            selectedCard = providerCard;
            selectedColor = aiColor;
            int col = aiColor == PlayerColor.RED ? 0 : modelAdapter.numCols() + 1;
            view.selectCard(i, col);
            break;
          }
        }

        // Now place the card
        if (selectedCard != null) {
          model.placeCard(move.getRow(), move.getCol(), move.getCard());
          selectedCard = null;
          selectedColor = null;
          view.selectCard(-1, -1);  // Clear selection
          view.refresh();
        }
      }
    } catch (Exception e) {
      System.err.println("Error making AI move: " + e.getMessage());
      e.printStackTrace();  // Add stack trace for debugging
    }
  }

  @Override
  public void notifyGameOver(Player winner) {
    updateTitle();
    view.refresh();
  }
}