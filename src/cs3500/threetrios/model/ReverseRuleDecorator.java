package cs3500.threetrios.model;

import java.util.List;

import cs3500.threetrios.strategy.Position;
import java.util.ArrayList;

public class ReverseRuleDecorator extends ModelDecorator {
  public ReverseRuleDecorator(MainModelInterface base) {
    super(base);
  }

  @Override
  public void executeBattlePhase(cs3500.threetrios.strategy.Position newCardPosition) {
    System.out.println("=== Reverse Battle Phase Starting ===");
    Card newCard = grid.getCard(newCardPosition.row, newCardPosition.col);
    List<Position> adjacent = getAdjacentPositions(newCardPosition);
    List<Card> toFlip = new ArrayList<>();

    for (Position pos : adjacent) {
      Card adjCard = grid.getCard(pos.row, pos.col);
      if (adjCard != null && adjCard.getOwner() != getCurrentPlayer()) {
        Direction battleDir = getBattleDirection(newCardPosition, pos);
        int attackValue = newCard.getAttackPower(battleDir);
        int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

        System.out.println(String.format(
                "Battle at (%d,%d) -> (%d,%d): %d vs %d using direction %s",
                newCardPosition.row, newCardPosition.col,
                pos.row, pos.col,
                attackValue, defenseValue,
                battleDir));

        if (attackValue < defenseValue) {
          System.out.println("Flipping card - attack value is lower!");
          System.out.println("Before flip - Owner: " + adjCard.getOwner().getColor());
          adjCard.setOwner(getCurrentPlayer());  // Directly set the owner
          System.out.println("After flip - Owner: " + adjCard.getOwner().getColor());
          grid.placeCard(pos.row, pos.col, adjCard);  // Update the grid
          toFlip.add(adjCard);
        }
      }
    }

    System.out.println(String.format("Battle complete - flipped %d cards", toFlip.size()));
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
        if (attackValue < defenseValue) {  // Reversed comparison
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
    if (from.row < to.row) return Direction.SOUTH;  // Attacking downward
    if (from.row > to.row) return Direction.NORTH;  // Attacking upward
    if (from.col < to.col) return Direction.EAST;   // Attacking rightward
    return Direction.WEST;                          // Attacking leftward
  }

  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < grid.getRows()
            && col >= 0 && col < grid.getCols()
            && !isHole(row, col);
  }
}