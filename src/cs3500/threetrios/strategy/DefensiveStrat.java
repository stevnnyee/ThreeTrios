package cs3500.threetrios.strategy;

import cs3500.threetrios.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy that chooses positions and cards that are least likely to be flipped by opponents.
 * Considers surrounding positions and opponent's possible cards.
 */
public class DefensiveStrat implements AIStrategy {
  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    List<Card> hand = model.getPlayerHand(player);
    List<AIMove> possibleMoves = new ArrayList<>();

    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (Card card : hand) {
          if (model.canPlaceCard(row, col, card)) {
            int defensibility = calculateDefensibility(model, new Position(row, col), card, player);
            possibleMoves.add(new AIMove(card, new Position(row, col), defensibility));
          }
        }
      }
    }

    if (possibleMoves.isEmpty()) {
      return StrategyUtil.getDefaultMove(model, player);
    }

    possibleMoves.sort(new MoveComparator());
    return possibleMoves.get(0);
  }

  /**
   * Calculates a defensibility score for a potential card placement. Higher scores indicates
   * more defensible positions.
   *
   * @param model  model
   * @param pos    position of the card
   * @param card   the card
   * @param player the current player
   * @return an integer representing the defensibility of the move
   */
  private int calculateDefensibility(MainModelInterface model, Position pos, Card card, Player player) {
    int score = 0;
    // Get opponent
    Player opponent = null;
    for (Player p : model.getPlayers()) {
      if (!p.getColor().equals(player.getColor())) {
        opponent = p;
        break;
      }
    }

    if (opponent == null) {
      return score;
    }

    List<Card> opponentHand = model.getPlayerHand(opponent);

    Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    for (Direction dir : directions) {
      Position adjPos = getAdjacentPosition(pos, dir);
      if (isValidPosition(adjPos, model)) {
        int vulnerableToFlips = 0;
        for (Card oppCard : opponentHand) {
          if (canBeFlippedBy(card, oppCard, dir)) {
            vulnerableToFlips++;
          }
        }
        score -= vulnerableToFlips * 100;
      } else {
        score += 50;
      }
    }

    return score;
  }

  /**
   * Determines whether a placed card can be flipped by an opponents card.
   *
   * @param placedCard   placed card
   * @param opponentCard opponents card
   * @param dir          direction
   * @return
   */
  private boolean canBeFlippedBy(Card placedCard, Card opponentCard, Direction dir) {
    Direction opposite = getOppositeDirection(dir);
    return placedCard.getAttackPower(dir) < opponentCard.getAttackPower(opposite);
  }

  /**
   * Returns the adjacent position to the given position.
   *
   * @param pos position
   * @param dir direction
   * @return the adjacent position of the give position
   */
  private Position getAdjacentPosition(Position pos, Direction dir) {
    switch (dir) {
      case NORTH:
        return new Position(pos.row - 1, pos.col);
      case SOUTH:
        return new Position(pos.row + 1, pos.col);
      case EAST:
        return new Position(pos.row, pos.col + 1);
      case WEST:
        return new Position(pos.row, pos.col - 1);
      default:
        throw new IllegalArgumentException("Invalid direction");
    }
  }

  /**
   * Returns the opposite direction of the given direction.
   *
   * @param dir direction
   * @return the opposite direction of the given direction
   */
  private Direction getOppositeDirection(Direction dir) {
    switch (dir) {
      case NORTH:
        return Direction.SOUTH;
      case SOUTH:
        return Direction.NORTH;
      case EAST:
        return Direction.WEST;
      case WEST:
        return Direction.EAST;
      default:
        throw new IllegalArgumentException("Invalid direction");
    }
  }

  /**
   * Checks if a position is valid within the game's rules and grid bounds.
   *
   * @param pos   position
   * @param model game model
   * @return true if position is valid, false otherwise
   */
  private boolean isValidPosition(Position pos, MainModelInterface model) {
    return pos.row >= 0 && pos.row < model.getGridDimensions()[0] &&
            pos.col >= 0 && pos.col < model.getGridDimensions()[1] &&
            !model.isHole(pos.row, pos.col);
  }
}