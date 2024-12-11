package cs3500.threetrios.model;

import cs3500.threetrios.strategy.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reverse rule decorator class.
 * Cards with lower attack power beat cards with higher values.
 */
public class ReverseRuleDecorator extends ModelDecorator {
  public ReverseRuleDecorator(MainModelInterface base) {
    super(base);
  }

  @Override
  public void executeBattlePhase(Position newCardPosition) {
    Set<Position> toProcess = new HashSet<>();
    Set<Position> processed = new HashSet<>();
    toProcess.add(newCardPosition);

    while (!toProcess.isEmpty()) {
      Position currentPos = toProcess.iterator().next();
      toProcess.remove(currentPos);
      processed.add(currentPos);

      Card currentCard = getCardAt(currentPos.row, currentPos.col);
      if (currentCard == null) {
        continue;
      }

      List<Position> adjacent = getAdjacentPositions(currentPos);
      for (Position adjPos : adjacent) {
        if (processed.contains(adjPos)) {
          continue;
        }

        Card adjCard = getCardAt(adjPos.row, adjPos.col);
        if (adjCard != null && adjCard.getOwner() != currentCard.getOwner()) {
          Direction battleDir = getBattleDirection(currentPos, adjPos);
          int attackValue = currentCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());
          if (attackValue < defenseValue) {
            adjCard.setOwner(currentCard.getOwner());
            toProcess.add(adjPos);
          }
        }
      }
    }
  }

  @Override
  public int getFlippableCards(int row, int col, Card card) {
    if (!canPlaceCard(row, col, card)) {
      return 0;
    }

    Set<Position> flippedCards = new HashSet<>();
    Set<Position> toProcess = new HashSet<>();
    Set<Position> processed = new HashSet<>();

    Position initialPos = new Position(row, col);
    toProcess.add(initialPos);

    while (!toProcess.isEmpty()) {
      Position currentPos = toProcess.iterator().next();
      toProcess.remove(currentPos);
      processed.add(currentPos);

      List<Position> adjacent = getAdjacentPositions(currentPos);
      Card attackingCard = (currentPos.equals(initialPos)) ? card :
              getCardAt(currentPos.row, currentPos.col);

      for (Position adjPos : adjacent) {
        if (processed.contains(adjPos)) {
          continue;
        }

        Card adjCard = getCardAt(adjPos.row, adjPos.col);
        if (adjCard != null && adjCard.getOwner() != getCurrentPlayer()) {
          Direction battleDir = getBattleDirection(currentPos, adjPos);
          int attackValue = attackingCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

          if (attackValue < defenseValue) {
            flippedCards.add(adjPos);
            toProcess.add(adjPos);
          }
        }
      }
    }

    return flippedCards.size();
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

  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < grid.getRows()
            && col >= 0 && col < grid.getCols()
            && !isHole(row, col);
  }

  @Override
  public void placeCard(int row, int col, Card card) {
    super.placeCard(row, col, card);
    executeBattlePhase(new Position(row, col));
  }

  @Override
  public void placeCard(Player player, int row, int col, Card card) {
    super.placeCard(player, row, col, card);
    executeBattlePhase(new Position(row, col));
  }

}