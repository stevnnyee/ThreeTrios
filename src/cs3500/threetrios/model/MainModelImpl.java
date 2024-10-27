package cs3500.threetrios.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainModelImpl implements MainModelInterface {
  private Grid grid;
  private Player redPlayer;
  private Player bluePlayer;
  private Player currentPlayer;
  private boolean gameStarted;
  private final boolean gameOver;

  public MainModelImpl() {
    this.gameStarted = false;
    this.gameOver = false;
  }

  @Override
  public void startGame(Grid grid, List<Card> deck) {
    validateGameSetup(grid, deck);
    this.grid = grid;
    initialize(grid);
    dealCards(deck);
    this.currentPlayer = redPlayer;
    this.gameStarted = true;
  }

  private void validateGameSetup(Grid grid, List<Card> deck) {
    if (grid == null || deck == null) {
      throw new IllegalArgumentException("Grid and deck cannot be null");
    }
    int requiredCards = grid.getCardCellCount() + 1;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Not enough cards for the game");
    }
  }

  @Override
  public void initialize(Grid grid) {
    this.redPlayer = new PlayerImpl("RED");
    this.bluePlayer = new PlayerImpl("BLUE");
  }

  @Override
  public void dealCards(List<Card> deck) {
    int cardsPerPlayer = (grid.getCardCellCount() + 1) / 2;

    List<Card> shuffledDeck = new ArrayList<>(deck);
    Collections.shuffle(shuffledDeck);

    for (int i = 0; i < cardsPerPlayer; i++) {
      Card redCard = shuffledDeck.get(i);
      redCard.setOwner(redPlayer);
      redPlayer.addCardToHand(redCard);

      Card blueCard = shuffledDeck.get(i + cardsPerPlayer);
      blueCard.setOwner(bluePlayer);
      bluePlayer.addCardToHand(blueCard);
    }
  }

  @Override
  public void makeMove(int row, int col, Card card) {
    validateMove(row, col, card);
    placeCard(row, col, card);
    executeBattlePhase(new Position(row, col));
    currentPlayer = (currentPlayer == redPlayer) ? bluePlayer : redPlayer;
  }

  private void validateMove(int row, int col, Card card) {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("Game not in progress");
    }
    if (!currentPlayer.getHand().contains(card)) {
      throw new IllegalArgumentException("Card not in current player's hand");
    }
  }

  @Override
  public boolean placeCard(int row, int col, Card card) {
    try {
      grid.placeCard(row, col, card);
      currentPlayer.removeCardFromHand(card);
      return true;
    } catch (IllegalStateException | IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public void executeBattlePhase(javax.swing.text.Position newCardPosition) {

  }

  @Override
  public void executeBattlePhase(Position newCardPosition) {
    List<Position> adjacentPositions = getAdjacentPositions(newCardPosition);
    for (Position pos : adjacentPositions) {
      Card adjacentCard = grid.getCard(pos.row, pos.col);
      if (adjacentCard != null && adjacentCard.getOwner() != currentPlayer) {
        checkAndFlipCard(newCardPosition, pos);
      }
    }
  }

  private List<Position> getAdjacentPositions(Position position) {
    List<Position> positions = new ArrayList<>();
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int newRow = position.row + dir[0];
      int newCol = position.col + dir[1];

      if (newRow >= 0 && newRow < grid.getRows() && newCol >= 0 && newCol < grid.getCols()) {
        positions.add(new Position(newRow, newCol));
      }
    }

    return positions;
  }


  private void checkAndFlipCard(Position attackerPos, Position defenderPos) {
    Card attacker = grid.getCard(attackerPos.row, attackerPos.col);
    Card defender = grid.getCard(defenderPos.row, defenderPos.col);

    Direction battleDirection = getBattleDirection(attackerPos, defenderPos);
    if (attacker.getAttackPower(battleDirection) >
            defender.getAttackPower(battleDirection.getOpposite())) {
      defender.setOwner(currentPlayer);
    }
  }

  private Direction getBattleDirection(Position from, Position to) {
    if (from.row < to.row) {
      return Direction.SOUTH;
    }
    if (from.row > to.row) {
      return Direction.NORTH;
    }
    if (from.col < to.col) {
      return Direction.EAST;
    }
    return Direction.WEST;
  }

  @Override
  public boolean isGameOver() {
    return gameOver || isGridFull();
  }

  private boolean isGridFull() {
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        if (!grid.isHole(i, j) && grid.getCard(i, j) == null) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Player determineWinner() {
    if (!isGameOver()) {
      return null;
    }

    int redScore = countPlayerCards(redPlayer);
    int blueScore = countPlayerCards(bluePlayer);

    if (redScore > blueScore) {
      return redPlayer;
    }
    if (blueScore > redScore) {
      return bluePlayer;
    }
    return null;
  }

  private int countPlayerCards(Player player) {
    int count = player.getHand().size();
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Card card = grid.getCard(i, j);
        if (card != null && card.getOwner() == player) {
          count++;
        }
      }
    }
    return count;
  }

  @Override
  public Grid getGrid() {
    return grid;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  @Override
  public Player getWinner() {
    return determineWinner();
  }

  static class Position {
    final int row;
    final int col;

    Position(int row, int col) {
      this.row = row;
      this.col = col;
    }
  }
}