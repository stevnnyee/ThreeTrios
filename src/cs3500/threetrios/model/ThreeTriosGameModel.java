package cs3500.threetrios.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   * Creates a new game from configuration files.
   *
   * @param boardFile path to board configuration file
   * @param cardFile  path to card configuration file
   * @throws IOException              if files cannot be read
   * @throws IllegalArgumentException if configurations are invalid
   */
  public void startGameFromConfig(String boardFile, String cardFile) throws IOException {
    Grid grid = BoardConfigReader.readBoardConfig(boardFile);
    List<Card> deck = CardConfigReader.readCardConfig(cardFile);
    startGame(grid, deck);
  }

  /**
   * Checks to see if the game setup is valid, useful for checking invariant before calling methods.
   *
   * @param grid grid
   * @param deck deck
   */
  private void validateGameSetup(Grid grid, List<Card> deck) {
    if (grid == null || deck == null) {
      throw new IllegalArgumentException("Grid and deck cannot be null");
    }

    int cardCells = grid.getCardCellCount();
    if (cardCells % 2 == 0) {
      throw new IllegalArgumentException("Grid must have odd number of card cells");
    }

    int requiredCards = cardCells + 1;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Not enough cards for the game");
    }
  }

  @Override
  public void initialize(Grid grid) {
    this.redPlayer = new ThreeTriosPlayer("RED");
    this.bluePlayer = new ThreeTriosPlayer("BLUE");
    playerHands.clear();
    playerHands.put(redPlayer, new ArrayList<>());
    playerHands.put(bluePlayer, new ArrayList<>());
  }

  @Override
  public void dealCards(List<Card> deck) {
    int cardCells = grid.getCardCellCount();
    int handSize = (cardCells + 1) / 2;

    if (deck.size() < cardCells + 1) {
      throw new IllegalArgumentException(
              String.format("Not enough cards. Need %d, got %d",
                      cardCells + 1, deck.size()));
    }

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
   * Validates a move before using it in a method.
   *
   * @param player player
   * @param row    row
   * @param col    column
   * @param card   card
   */
  private void validateMove(Player player, int row, int col, Card card) {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("Game not in progress");
    }
    if (player != currentPlayer) {
      throw new IllegalArgumentException("Not your turn");
    }
    if (!playerHands.get(currentPlayer).contains(card)) {
      throw new IllegalArgumentException("Card not in current player's hand");
    }
    if (grid.getCard(row, col) != null) {
      throw new IllegalStateException("Can't place a card on top of another card.");
    }
    if (grid.isHole(row, col)) {
      throw new IllegalArgumentException("Can't place card in a hole");
    }
  }

  @Override
  public boolean canPlaceCard(int row, int col, Card card) {
    if (!gameStarted || gameOver) {
      return false;
    }
    if (!playerHands.get(currentPlayer).contains(card)) {
      return false;
    }
    if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) {
      return false;
    }
    if (grid.isHole(row, col)) {
      return false;
    }
    return grid.getCard(row, col) == null;
  }

  @Override
  public int getPlayerScore(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    return countPlayerCards(player);
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    return new ArrayList<>(playerHands.get(player));
  }

  @Override
  public void executeBattlePhase(Position newCardPosition) {
    List<Position> toFlip = new ArrayList<>();
    List<Position> adjacent = getAdjacentPositions(newCardPosition);

    for (Position pos : adjacent) {
      Card adjacentCard = grid.getCard(pos.row, pos.col);
      if (adjacentCard != null && adjacentCard.getOwner() != currentPlayer) {
        if (checkCardWinsBattle(newCardPosition, pos)) {
          toFlip.add(pos);
        }
      }
    }
    for (Position pos : toFlip) {
      Card card = grid.getCard(pos.row, pos.col);
      card.setOwner(currentPlayer);
      executeBattlePhase(pos);
    }
  }

  /**
   * Checks if a card wins the battle in comparison to another card.
   *
   * @param attackerPos attacker position
   * @param defenderPos defender position
   * @return true if attacker wins.
   */
  private boolean checkCardWinsBattle(Position attackerPos, Position defenderPos) {
    Card attacker = grid.getCard(attackerPos.row, attackerPos.col);
    Card defender = grid.getCard(defenderPos.row, defenderPos.col);

    Direction battleDirection = getBattleDirection(attackerPos, defenderPos);
    return attacker.getAttackPower(battleDirection)
            > defender.getAttackPower(battleDirection.getOpposite());
  }

  /**
   * Returns the list adjacent positions to the cards.
   *
   * @param position position
   * @return a list of adjacent positions
   */
  private List<Position> getAdjacentPositions(Position position) {
    List<Position> positions = new ArrayList<>();
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int newRow = position.row + dir[0];
      int newCol = position.col + dir[1];
      if (isValidPosition(newRow, newCol)) {
        positions.add(new Position(newRow, newCol));
      }
    }
    return positions;
  }

  /**
   * Checks if the row and column is valid.
   *
   * @param row row
   * @param col column
   * @return true if position is valid, false otherwise
   */
  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < grid.getRows()
            && col >= 0 && col < grid.getCols()
            && !grid.isHole(row, col);
  }

  /**
   * Checks the direction of the battle taking place.
   *
   * @param from original position
   * @param to   final position
   * @return direction
   */
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

  /**
   * Checks to see if a grid is full.
   *
   * @return true if grid is full, false otherwise
   */
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

  /**
   * Counts and returns the number of cards a player has.
   *
   * @param player player
   * @return returns count
   */
  private int countPlayerCards(Player player) {
    int count = playerHands.get(player).size();
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

  /**
   * Represents a position on the game grid.
   */
  static class Position {
    final int row;
    final int col;

    /**
     * Constructs a position given a row and column.
     *
     * @param row row
     * @param col column
     */
    Position(int row, int col) {
      this.row = row;
      this.col = col;
    }
  }
}