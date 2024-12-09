package cs3500.threetrios.model;

import cs3500.threetrios.strategy.Position;
import java.util.ArrayList;
import java.util.List;


public class FallenAceDecorator extends ModelDecorator {
  public FallenAceDecorator(MainModelInterface base) {
    super(base);
  }

  @Override
  public void executeBattlePhase(Position newCardPosition) {
    Card newCard = grid.getCard(newCardPosition.row, newCardPosition.col);
    List<Position> adjacent = getAdjacentPositions(newCardPosition);
    List<Card> toFlip = new ArrayList<>();

    for (Position pos : adjacent) {
      Card adjCard = grid.getCard(pos.row, pos.col);
      if (adjCard != null && adjCard.getOwner() != getCurrentPlayer()) {
        Direction battleDir = getBattleDirection(newCardPosition, pos);
        int attackValue = newCard.getAttackPower(battleDir);
        int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

        if ((attackValue == 1 && defenseValue == 10) || attackValue > defenseValue) {
          toFlip.add(adjCard);
        }
      }
    }

    if (base instanceof ThreeTriosGameModel) {
      ((ThreeTriosGameModel) base).executeBattlePhase(
              new ThreeTriosGameModel.Position(newCardPosition.row, newCardPosition.col));
    }

    for (Card card : toFlip) {
      card.setOwner(getCurrentPlayer());
    }
  }

  @Override
  public int getFlippableCards(int row, int col, Card card) {
    if (!canPlaceCard(row, col, card)) {
      return 0;
    }
    int flippableCount = 0;
    List<Position> adjacent = getAdjacentPositions(new Position(row, col));
    for (Position pos : adjacent) {
      Card adjacentCard = getCardAt(pos.row, pos.col);
      if (adjacentCard != null && adjacentCard.getOwner() != getCurrentPlayer()) {
        Direction battleDir = getBattleDirection(new Position(row, col), pos);
        int attackValue = card.getAttackPower(battleDir);
        int defenseValue = adjacentCard.getAttackPower(battleDir.getOpposite());
        if ((attackValue == 1 && defenseValue == 10) || attackValue > defenseValue) {
          flippableCount++;
        }
      }
    }
    return flippableCount;
  }

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

  private Direction getBattleDirection(Position from, Position to) {
    if (from.row < to.row) return Direction.SOUTH;
    if (from.row > to.row) return Direction.NORTH;
    if (from.col < to.col) return Direction.EAST;
    return Direction.WEST;
  }

  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < grid.getRows()
            && col >= 0 && col < grid.getCols()
            && !isHole(row, col);
  }
}