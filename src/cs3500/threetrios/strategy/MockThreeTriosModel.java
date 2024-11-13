package cs3500.threetrios.strategy;

import cs3500.threetrios.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    initializeDefaultHand();
  }

  private void initializeDefaultHand() {
    mockHand.add(new MockCard("card1", 5, 5, 5, 5));
    mockHand.add(new MockCard("card2", 7, 7, 7, 7));
  }

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
    return new ArrayList<>(mockHand);
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
  public void placeCard(Player player, int row, int col, Card card) {
    log.append(String.format("Placing card for player %s at (%d,%d)\n", player.getColor(), row, col));
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

  public void setCurrentPlayer(Player player) {
    this.currentPlayer = player;
  }
}