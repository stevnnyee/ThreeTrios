package cs3500.threetrios.strategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs3500.threetrios.features.ModelFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * A mock implementation of MainModelInterface used for testing game strategies and components.
 * This class provides a testable game model that logs operations and allows for
 * controlled testing scenarios.
 */
public class MockThreeTriosModel implements MainModelInterface {
  private final StringBuilder log;
  private final List<Position> checkedPositions;
  private final Map<Position, Integer> positionValues;
  private final boolean[][] holes;
  private final int rows;
  private final int cols;
  private final List<Card> mockHand;
  private final List<Player> players;
  private Player currentPlayer;
  private Player winner;
  private boolean isGameOver;
  private Grid mockGrid;
  private final List<ModelFeatures> featureListeners;

  /**
   * Constructs a new MockThreeTriosModel with default settings and logging capability.
   *
   * @param log Stringbuilder to record changes to the game state and called methods
   */
  public MockThreeTriosModel(StringBuilder log) {
    this.log = log;
    this.checkedPositions = new ArrayList<>();
    this.positionValues = new HashMap<>();
    this.rows = 5;
    this.cols = 7;
    this.holes = new boolean[rows][cols];
    this.mockHand = new ArrayList<>();
    this.players = new ArrayList<>();
    this.players.add(new MockPlayer("RED"));
    this.players.add(new MockPlayer("BLUE"));
    this.currentPlayer = players.get(0);
    this.winner = null;
    this.isGameOver = false;
    this.featureListeners = new ArrayList<>();
    initializeDefaultHand();
  }

  /**
   * Initializes a default hand, which will be used for testing.
   */
  private void initializeDefaultHand() {
    mockHand.add(new MockCard("card1", 5, 5, 5, 5));
    mockHand.add(new MockCard("card2", 7, 7, 7, 7));
  }

  /**
   * Sets the number of cards that can be flipped at a specific position.
   * Used for testing strategies that consider flips.
   *
   * @param pos   the position to set the flip value for
   * @param value the number of cards that can be flipped
   */
  public void setFlipValue(Position pos, int value) {
    positionValues.put(pos, value);
  }

  @Override
  public Grid getGrid() {
    return mockGrid;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    log.append("Getting hand for player: ").append(player.getColor()).append("\n");
    List<Card> hand = new ArrayList<>(player.getHand());
    if (hand.isEmpty()) {
      hand.add(new MockCard("defaultCard", 5, 5, 5, 5));
    }
    return hand;
  }

  @Override
  public boolean canPlaceCard(int row, int col, Card card) {
    log.append(String.format("Checked if can place at (%d,%d)\n", row, col));
    checkedPositions.add(new Position(row, col));
    return !isHole(row, col) && getCardAt(row, col) == null;
  }

  @Override
  public int getFlippableCards(int row, int col, Card card) {
    Position pos = new Position(row, col);
    return positionValues.getOrDefault(pos, 0);
  }

  @Override
  public int[] getGridDimensions() {
    return new int[]{rows, cols};
  }

  @Override
  public Card getCardAt(int row, int col) {
    log.append(String.format("Getting card at (%d,%d)\n", row, col));
    return null;
  }

  @Override
  public Player getCardOwnerAt(int row, int col) {
    log.append(String.format("Getting card owner at (%d,%d)\n", row, col));
    return null;
  }

  @Override
  public boolean isHole(int row, int col) {
    return holes[row][col];
  }

  @Override
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  @Override
  public void setCurrentPlayer(String color) {
    log.append(String.format("Setting current player to: %s\n", color));

    Player previousPlayer = this.currentPlayer;

    if (color.equalsIgnoreCase("RED")) {
      this.currentPlayer = players.get(0);  // RED is first player
    } else if (color.equalsIgnoreCase("BLUE")) {
      this.currentPlayer = players.get(1);  // BLUE is second player
    } else {
      throw new IllegalArgumentException("Invalid player color: " + color);
    }

    // Log the actual change
    if (previousPlayer != this.currentPlayer) {
      log.append(String.format("Current player changed from %s to %s\n",
              previousPlayer.getColor(),
              this.currentPlayer.getColor()));
      // Notify listeners
      for (ModelFeatures listener : featureListeners) {
        log.append("Notifying listener of turn change\n");
        listener.notifyTurnChange(this.currentPlayer);
      }
    }
  }

  /**
   * Sets the current active player.
   *
   * @param player the player to set as the current player
   */
  public void setCurrentPlayer(Player player) {
    log.append(String.format("Setting current player directly to: %s\n", player.getColor()));
    this.currentPlayer = player;
  }

  @Override
  public void addFeaturesListener(ModelFeatures listener) {
    log.append("Adding features listener\n");
    if (listener != null) {
      featureListeners.add(listener);
    }
  }


  @Override
  public void placeCard(Player player, int row, int col, Card card) {
    log.append(String.format("Placing card for player %s at (%d,%d)\n",
            player.getColor(), row, col));
  }

  @Override
  public void placeCard(int row, int col, Card card) {
    log.append(String.format("Placing card at (%d,%d)\n", row, col));
  }

  @Override
  public int getPlayerScore(Player player) {
    log.append("Getting score for player: ").append(player.getColor()).append("\n");
    return 0;
  }

  @Override
  public boolean isGameOver() {
    return isGameOver;
  }

  @Override
  public Player getWinner() {
    return winner;
  }

  /**
   * Verifies if all corners have been checked, which will be used for testing corner strategies.
   *
   * @return true if all four corners of the grid have been checked, false otherwise
   */
  public boolean verifiedAllCorners() {
    boolean topLeft = checkedPositions.contains(new Position(0, 0));
    boolean topRight = checkedPositions.contains(new Position(0, cols - 1));
    boolean bottomLeft = checkedPositions.contains(new Position(rows - 1, 0));
    boolean bottomRight = checkedPositions.contains(new Position(rows - 1, cols - 1));
    return topLeft && topRight && bottomLeft && bottomRight;
  }

  @Override
  public void startGame(Grid grid, List<Card> deck) {
    log.append("Starting game with grid and deck\n");
    this.mockGrid = grid;

    this.isGameOver = false;
    this.winner = null;

    this.mockHand.clear();
    if (deck != null && !deck.isEmpty()) {
      for (int i = 0; i < Math.min(deck.size(), 8); i++) {
        this.mockHand.add(deck.get(i));
      }
    }
  }

  @Override
  public void startGameFromConfig(String boardFile, String cardFile) throws IOException {
    log.append(String.format("Starting game from config files: %s, %s\n", boardFile, cardFile));
    this.isGameOver = false;
    this.winner = null;

    if (this.mockHand.isEmpty()) {
      initializeDefaultHand();
    }
  }
}