package cs3500.threetrios.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Position;

/**
 * Implementation of the ThreeTriosModel Interface.
 * This handles the game state, card placement, battling, and turn mechanics.
 */
public class ThreeTriosGameModel implements MainModelInterface {
  private Grid grid;
  private Player redPlayer;
  private Player bluePlayer;
  private Player currentPlayer;
  private final Map<Player, List<Card>> playerHands;
  private boolean gameStarted;
  private boolean gameOver;

  /**
   * Constructs a new ThreeTriosGameModel with an initial state.
   */
  public ThreeTriosGameModel() {
    this.playerHands = new HashMap<>();
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
    this.gameOver = false;
  }

  /**
   * Method that helps validate the game setup parameters.
   *
   * @param grid the grid of the game
   * @param deck deck of cards
   */
  private void validateGameSetup(Grid grid, List<Card> deck) {
    if (grid == null || deck == null) {
      throw new IllegalArgumentException("Grid and deck cannot be null");
    }

    int cardCells = grid.getCardCellCount();
    if (cardCells % 2 == 0) {
      throw new IllegalArgumentException("Grid must have odd number of card cells");
    }

    int requiredCards = grid.getCardCellCount() + 1;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Not enough cards for the game");
    }
  }

  @Override
  public void initialize(Grid grid) {
    this.redPlayer = new ThreeTriosPlayer("RED");
    this.bluePlayer = new ThreeTriosPlayer("BLUE");
    playerHands.clear(); // ensure hands are clear before drawing
    playerHands.put(redPlayer, new ArrayList<>());
    playerHands.put(bluePlayer, new ArrayList<>());
  }

  @Override
  public void dealCards(List<Card> deck) {
    int handSize = (grid.getCardCellCount() + 1) / 2;
    List<Card> shuffledDeck = new ArrayList<>(deck);
    Collections.shuffle(shuffledDeck);

    playerHands.get(redPlayer).clear();
    playerHands.get(bluePlayer).clear();

    for (int i = 0; i < handSize; i++) {
      Card redCard = shuffledDeck.get(i);
      Card blueCard = shuffledDeck.get(i + handSize);

      redCard.setOwner(redPlayer);
      blueCard.setOwner(bluePlayer);

      playerHands.get(redPlayer).add(redCard);
      playerHands.get(bluePlayer).add(blueCard);
    }
  }

  @Override
  public void placeCard(Player player, int row, int col, Card card) {
    validateMove(player, row, col, card);
    grid.placeCard(row, col, card);
    playerHands.get(player).remove(card);
    executeBattlePhase(new Position(row, col));
    currentPlayer = (currentPlayer == redPlayer) ? bluePlayer : redPlayer;
    gameOver = isGridFull();
  }

  /**
   * Validates a move before it performs its action.
   *
   * @param player player
   * @param row row
   * @param col column
   * @param card card
   * @throws IllegalArgumentException if the move is invalid
   * @throws IllegalStateException if the game state doesn't allow the move
   */
  private void validateMove(Player player, int row, int col, Card card) {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("Game not in progress");
    }
    if (player != currentPlayer) {
      throw new IllegalArgumentException("Not your turn");
    }
    if (!currentPlayer.getHand().contains(card)) {
      throw new IllegalArgumentException("Card not in current player's hand");
    }
    if (grid.getCard(row, col) != null) {
      throw new IllegalStateException("Cannot place a card on top of another card.");
    }
    if (grid.isHole(row, col)) {
      throw new IllegalArgumentException("Cannot place card in a hole");
    }
  }

  @Override
  public boolean canPlaceCard(int row, int col, Card card) {
    try {
      grid.placeCard(row, col, card);
      currentPlayer.removeCardFromHand(card);
      return true;
    } catch (IllegalStateException | IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public int getPlayerScore(Player player) {
    return 0; //NEED TO COMPLETE
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    return List.of();
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

