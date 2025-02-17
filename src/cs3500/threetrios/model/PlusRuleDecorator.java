package cs3500.threetrios.model;

import cs3500.threetrios.strategy.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlusRuleDecorator extends ModelDecorator {
  public PlusRuleDecorator(MainModelInterface base) {
    super(base);
  }

  @Override
  public void executeBattlePhase(Position newCardPosition) {
    List<Card> sameFlips = checkSameRule(newCardPosition);
    for (Card card : sameFlips) {
      card.setOwner(getCurrentPlayer());
    }
    base.executeBattlePhase(newCardPosition);
  }

  private List<Card> checkSameRule(Position pos) {
    List<Card> toFlip = new ArrayList<>();
    Card placedCard = grid.getCard(pos.row, pos.col);
    Map<Integer, List<Position>> matchingCards = new HashMap<>();

    for (Position adjPos : getAdjacentPositions(pos)) {
      Card adjCard = grid.getCard(adjPos.row, adjPos.col);
      if (adjCard != null) {
        Direction dir = getBattleDirection(pos, adjPos);
        int placedValue = placedCard.getAttackPower(dir);
        int adjValue = adjCard.getAttackPower(dir.getOpposite());

        if (placedValue == adjValue) {
          matchingCards.computeIfAbsent(placedValue, k -> new ArrayList<>()).add(adjPos);
        }
      }
    }

    for (List<Position> positions : matchingCards.values()) {
      if (positions.size() >= 2) {
        for (Position matchPos : positions) {
          Card card = grid.getCard(matchPos.row, matchPos.col);
          if (card.getOwner() != getCurrentPlayer()) {
            toFlip.add(card);
          }
        }
      }
    }
    return toFlip;
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

  @Override
  public int getFlippableCards(int row, int col, Card card) {
    return 0;
  }
}