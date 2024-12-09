package cs3500.threetrios.model;

import java.io.IOException;
import java.util.List;
import cs3500.threetrios.strategy.Position;
import cs3500.threetrios.features.ModelFeatures;

public abstract class ModelDecorator implements MainModelInterface {
  protected final MainModelInterface base;
  protected Grid grid;

  protected ModelDecorator(MainModelInterface base) {
    this.base = base;
  }

  @Override
  public void placeCard(Player player, int row, int col, Card card) {
    if (!canPlaceCard(row, col, card)) {
      throw new IllegalArgumentException("Invalid card placement");
    }

    // Place card directly on grid
    card.setOwner(player);
    grid.placeCard(row, col, card);

    // Remove from player's hand using base model (which modifies the actual hand)
    base.getPlayerHand(player).remove(card);

    // Execute only our battle phase
    executeBattlePhase(new Position(row, col));

    // Change turns
    String nextPlayer = player.getColor().equals("RED") ? "BLUE" : "RED";
    base.setCurrentPlayer(nextPlayer);
  }

  @Override
  public void placeCard(int row, int col, Card card) {
    placeCard(getCurrentPlayer(), row, col, card);
  }

  // All other methods remain unchanged, delegating to base
  @Override
  public void startGame(Grid grid, List<Card> deck) {
    base.startGame(grid, deck);
    this.grid = grid;
  }

  @Override
  public void startGameFromConfig(String boardFile, String cardFile) throws IOException {
    base.startGameFromConfig(boardFile, cardFile);
  }

  @Override
  public boolean canPlaceCard(int row, int col, Card card) {
    return base.canPlaceCard(row, col, card);
  }

  @Override
  public int getPlayerScore(Player player) {
    return base.getPlayerScore(player);
  }

  @Override
  public List<Card> getPlayerHand(Player player) {
    return base.getPlayerHand(player);
  }

  @Override
  public Grid getGrid() {
    return base.getGrid();
  }

  @Override
  public Player getCurrentPlayer() {
    return base.getCurrentPlayer();
  }

  @Override
  public void setCurrentPlayer(String color) {
    base.setCurrentPlayer(color);
  }

  @Override
  public Player getWinner() {
    return base.getWinner();
  }

  @Override
  public int[] getGridDimensions() {
    return base.getGridDimensions();
  }

  @Override
  public Card getCardAt(int row, int col) {
    return base.getCardAt(row, col);
  }

  @Override
  public Player getCardOwnerAt(int row, int col) {
    return base.getCardOwnerAt(row, col);
  }

  @Override
  public boolean isHole(int row, int col) {
    return base.isHole(row, col);
  }

  @Override
  public List<Player> getPlayers() {
    return base.getPlayers();
  }

  @Override
  public boolean isGameOver() {
    return base.isGameOver();
  }

  @Override
  public void addFeaturesListener(ModelFeatures listener) {
    base.addFeaturesListener(listener);
  }

  public abstract void executeBattlePhase(Position newCardPosition);
}