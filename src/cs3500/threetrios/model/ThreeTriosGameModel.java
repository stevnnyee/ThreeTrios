package cs3500.threetrios.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  private boolean isScoring = false;

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

    this.currentPlayer = Math.random() < 0.5 ? redPlayer : bluePlayer;

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

  public void initialize(Grid grid) {
    this.redPlayer = new ThreeTriosPlayer("RED");
    this.bluePlayer = new ThreeTriosPlayer("BLUE");
    playerHands.clear();
    playerHands.put(redPlayer, new ArrayList<>());
    playerHands.put(bluePlayer, new ArrayList<>());
  }

  public void dealCards(List<Card> deck) {
    // Calculate required hand size based on grid
    int totalRequiredCards = grid.getCardCellCount() + 1;
    int handSize = (totalRequiredCards + 1) / 2;  // Round up to next even number divided by 2

    if (deck.size() < handSize * 2) {
      throw new IllegalArgumentException(
              String.format("Not enough cards. Need %d, got %d",
                      handSize * 2, deck.size()));
    }

    List<Card> shuffledDeck = new ArrayList<>(deck);
    Collections.shuffle(shuffledDeck);
    playerHands.get(redPlayer).clear();
    playerHands.get(bluePlayer).clear();

    // Deal cards to each player
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

  // Keep the old method for backward compatibility if needed, internally calling the new one
  @Override
  public void placeCard(int row, int col, Card card) {
    placeCard(currentPlayer, row, col, card);
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
    if (grid == null) {
      throw new IllegalStateException("Game has not been started");
    }
    if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) {
      throw new IllegalArgumentException("Position out of bounds");
    }
    if (grid.isHole(row, col)) {
      throw new IllegalArgumentException("Can't place card in a hole");
    }
    if (grid.getCard(row, col) != null) {
      throw new IllegalStateException("Position already contains a card");
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
  public void executeBattlePhase(Position newCardPosition) {
    Set<Position> toProcess = new HashSet<>();
    Set<Position> processed = new HashSet<>();
    List<Position> cardsToFlip = new ArrayList<>();

    // Start with the newly placed card
    toProcess.add(newCardPosition);

    // Continue while we have cards to process
    while (!toProcess.isEmpty()) {
      // Get next card to process
      Position currentPos = toProcess.iterator().next();
      toProcess.remove(currentPos);
      processed.add(currentPos);

      // Get adjacent positions
      List<Position> adjacentPositions = getAdjacentPositions(currentPos);

      // Check each adjacent position for potential battles
      for (Position adjPos : adjacentPositions) {
        Card adjacentCard = grid.getCard(adjPos.row, adjPos.col);
        Card currentCard = grid.getCard(currentPos.row, currentPos.col);

        // Skip if no card, or already processed
        if (adjacentCard == null || processed.contains(adjPos)) {
          continue;
        }

        // Only battle against opponent's cards
        if (adjacentCard.getOwner() != currentCard.getOwner()) {
          if (checkCardWinsBattle(currentPos, adjPos)) {
            cardsToFlip.add(adjPos);

            // Add this position to be processed for combo battles
            if (!processed.contains(adjPos)) {
              toProcess.add(adjPos);
            }
          }
        }
      }
    }

    // Flip all cards at once after determining all battles
    for (Position pos : cardsToFlip) {
      Card card = grid.getCard(pos.row, pos.col);
      card.setOwner(currentPlayer);
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

    if (attacker == null || defender == null) {
      return false;
    }

    Direction battleDir = getBattleDirection(attackerPos, defenderPos);
    int attackValue = attacker.getAttackPower(battleDir);
    int defenseValue = defender.getAttackPower(battleDir.getOpposite());

    return attackValue > defenseValue;
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
    if (gameOver || isGridFull()) {
      return true;
    }
    return false;
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
    // Prevent recursive scoring
    if (isScoring) {
      return 0;
    }

    isScoring = true;
    int count = 0;

    // Only count hand cards if game is NOT over
    if (!isGameOver()) {
      count += playerHands.get(player).size();
    }

    // Count cards on the board
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Card card = grid.getCard(i, j);
        if (card != null && card.getOwner() == player) {
          count++;
        }
      }
    }

    isScoring = false;
    return count;
  }

  @Override
  public Grid getGrid() {
    // Return a deep copy of the grid to prevent modification
    ThreeTriosGrid newGrid = new ThreeTriosGrid(grid.getRows(), grid.getCols(), getHoles());
    // Copy all cards to the new grid
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        Card card = grid.getCard(i, j);
        if (card != null) {
          newGrid.placeCard(i, j, card);
        }
      }
    }
    return newGrid;
  }

  private boolean[][] getHoles() {
    boolean[][] holes = new boolean[grid.getRows()][grid.getCols()];
    for (int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getCols(); j++) {
        holes[i][j] = grid.isHole(i, j);
      }
    }
    return holes;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  @Override
  public Player getWinner() {
    return determineWinner();
  }

  @Override
  public int getFlippableCards(int row, int col, Card card) {
    if (!canPlaceCard(row, col, card)) {
      return 0;
    }
    int flippableCount = 0;
    List<Position> adjacent = getAdjacentPositions(new Position(row, col));
    for (Position pos : adjacent) {
      Card adjacentCard = grid.getCard(pos.row, pos.col);
      if (adjacentCard != null && adjacentCard.getOwner() != currentPlayer) {
        if (checkCardWinsBattle(new Position(row, col), pos)) {
          flippableCount++;
        }
      }
    }
    return flippableCount;
  }

  @Override
  public int[] getGridDimensions() {
    if (grid == null) {
      throw new IllegalStateException("Game has not been started");
    }
    return new int[]{grid.getRows(), grid.getCols()};
  }

  @Override
  public Card getCardAt(int row, int col) {
    if (grid == null) {
      throw new IllegalStateException("Game has not been started");
    }
    if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) {
      throw new IllegalArgumentException("Invalid grid coordinates");
    }
    return grid.getCard(row, col);
  }

  @Override
  public Player getCardOwnerAt(int row, int col) {
    if (grid == null) {
      throw new IllegalStateException("Game has not been started");
    }
    if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) {
      throw new IllegalArgumentException("Invalid grid coordinates");
    }
    Card card = grid.getCard(row, col);
    return card != null ? card.getOwner() : null;
  }

  @Override
  public boolean isHole(int row, int col) {
    if (grid == null) {
      throw new IllegalStateException("Game has not been started");
    }
    if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) {
      throw new IllegalArgumentException("Invalid grid coordinates");
    }
    return grid.isHole(row, col);
  }

  @Override
  public List<Player> getPlayers() {
    if (!gameStarted) {
      return Collections.emptyList();
    }
    return List.of(redPlayer, bluePlayer);
  }

  public void setCurrentPlayer(String color) {
    if (color.equals("RED")) {
      this.currentPlayer = redPlayer;
    } else {
      this.currentPlayer = bluePlayer;
    }
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