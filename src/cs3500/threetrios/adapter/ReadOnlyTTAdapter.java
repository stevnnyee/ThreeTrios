package cs3500.threetrios.adapter;

import cs3500.threetrios.model.ReadOnlyThreeTriosModel;
import cs3500.threetrios.provider.model.ReadOnlyTT;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.model.Cell;
import cs3500.threetrios.provider.model.CardCell;
import cs3500.threetrios.provider.model.Card;
import cs3500.threetrios.model.Player;
import java.util.List;
import java.util.ArrayList;

public class ReadOnlyTTAdapter implements ReadOnlyTT {
  private final ReadOnlyThreeTriosModel model;

  public ReadOnlyTTAdapter(ReadOnlyThreeTriosModel model) {
    this.model = model;
  }

  @Override
  public PlayerColor getCurrentTurn() {
    Player current = model.getCurrentPlayer();
    if (current == null) return null;
    return current.getColor().equals("RED") ? PlayerColor.RED : PlayerColor.BLUE;
  }

  @Override
  public Cell getCell(int row, int col) {
    CardCell cell = new CardCell();
    if (model.isHole(row, col)) {
      return cell; // Their CardCell defaults to being a hole
    }
    cs3500.threetrios.model.Card modelCard = model.getCardAt(row, col);
    if (modelCard != null) {
      cell.updateCard(new CardAdapter(modelCard), false);
    }
    return cell;
  }

  @Override
  public PlayerColor getCardOwner(int row, int col) {
    Player owner = model.getCardOwnerAt(row, col);
    if (owner == null) return null;
    return owner.getColor().equals("RED") ? PlayerColor.RED : PlayerColor.BLUE;
  }

  @Override
  public boolean isValidMove(int row, int col) {
    // In your model, a valid move is one where:
    // - The position is within bounds
    // - The position is not a hole
    // - The position is empty
    try {
      return !model.isHole(row, col) && model.getCardAt(row, col) == null;
    } catch (IllegalArgumentException e) {
      return false; // Out of bounds or invalid position
    }
  }

  @Override
  public int countNumFlipped(Card toPlace, int row, int col) {
    // Convert their Card back to our Card type to use with our model
    cs3500.threetrios.model.Card ourCard = adaptProviderCard(toPlace);
    return model.getFlippableCards(row, col, ourCard);
  }

  @Override
  public List<List<Cell>> getBoard() {
    List<List<Cell>> board = new ArrayList<>();
    int[] dims = model.getGridDimensions();

    for (int i = 0; i < dims[0]; i++) {
      List<Cell> row = new ArrayList<>();
      for (int j = 0; j < dims[1]; j++) {
        row.add(getCell(i, j));
      }
      board.add(row);
    }
    return board;
  }

  @Override
  public int numRows() {
    int[] dims = model.getGridDimensions();
    return dims[0];
  }

  @Override
  public int numCols() {
    int[] dims = model.getGridDimensions();
    return dims[1];
  }

  @Override
  public List<Card> getHand(PlayerColor color) {
    String colorStr = (color == PlayerColor.RED) ? "RED" : "BLUE";
    for (Player p : model.getPlayers()) {
      if (p.getColor().equals(colorStr)) {
        List<Card> adaptedHand = new ArrayList<>();
        for (cs3500.threetrios.model.Card modelCard : model.getPlayerHand(p)) {
          adaptedHand.add(new CardAdapter(modelCard));
        }
        return adaptedHand;
      }
    }
    return new ArrayList<>();
  }

  @Override
  public boolean isGameOver() {
    return model.isGameOver();
  }

  @Override
  public int getScore(PlayerColor player) {
    for (Player p : model.getPlayers()) {
      if ((player == PlayerColor.RED && p.getColor().equals("RED"))
              || (player == PlayerColor.BLUE && p.getColor().equals("BLUE"))) {
        return model.getPlayerScore(p);
      }
    }
    return 0;
  }

  @Override
  public PlayerColor getWinner() {
    if (!model.isGameOver()) {
      throw new IllegalStateException("Game is not over yet");
    }
    Player winner = model.getWinner();
    if (winner == null) return null;
    return winner.getColor().equals("RED") ? PlayerColor.RED : PlayerColor.BLUE;
  }

  // Helper method to convert provider Card to our Card type
  private cs3500.threetrios.model.Card adaptProviderCard(Card providerCard) {
    // You'll need to implement this based on your Card implementation
    // This is just a placeholder - implement the actual conversion
    throw new UnsupportedOperationException("Need to implement card conversion");
  }
}