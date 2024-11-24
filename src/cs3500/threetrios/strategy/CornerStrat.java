package cs3500.threetrios.strategy;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

/**
 * An AI strategy that prioritizes placing cards in corner positions on the game board.
 * If no valid corner moves are available, it falls back to evaluating all possible positions.
 */
public class CornerStrat implements AIStrategy {

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    validateInputs(model, player);
    List<Card> hand = getPlayerHand(model, player);
    int[] dimensions = model.getGridDimensions();

    AIMove bestCornerMove = findBestMoveInCorners(model, hand, dimensions);
    if (bestCornerMove != null) {
      return bestCornerMove;
    }

    AIMove bestGridMove = findBestMoveInGrid(model, hand, dimensions);
    if (bestGridMove != null) {
      return bestGridMove;
    }

    return new AIMove(hand.get(0), new Position(0, 0), 0);
  }

  /**
   * Validates that the input parameters are not null.
   *
   * @param model  the game model
   * @param player the current player
   * @throws IllegalArgumentException if either parameter is null
   */
  private void validateInputs(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }
  }

  /**
   * Retrieves the hand of cards for the given player from the model.
   *
   * @param model  the game model
   * @param player the player whose hand to retrieve
   * @return the list of cards in the player's hand
   * @throws IllegalStateException if the player's hand is empty or the player is not found
   */
  private List<Card> getPlayerHand(MainModelInterface model, Player player) {
    for (Player p : model.getPlayers()) {
      if (p.getColor().equals(player.getColor())) {
        List<Card> hand = model.getPlayerHand(p);
        if (hand == null || hand.isEmpty()) {
          throw new IllegalStateException("No cards in player's hand");
        }
        return hand;
      }
    }
    throw new IllegalStateException("Player not found in model");
  }

  /**
   * Generates a list of corner positions based on the board dimensions.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   * @return list of corner positions
   */
  private List<Position> getCornerPositions(int rows, int cols) {
    List<Position> corners = new ArrayList<>(4);
    corners.add(new Position(0, 0));
    corners.add(new Position(0, cols - 1));
    corners.add(new Position(rows - 1, 0));
    corners.add(new Position(rows - 1, cols - 1));
    return corners;
  }

  /**
   * Finds the best possible move among the corner positions.
   *
   * @param model      the game model
   * @param hand       the player's hand of cards
   * @param dimensions the grid dimensions
   * @return the best move found, or null if no valid corner moves exist
   */
  private AIMove findBestMoveInCorners(MainModelInterface model,
                                       List<Card> hand, int[] dimensions) {
    List<Position> corners = getCornerPositions(dimensions[0], dimensions[1]);
    return findBestMoveInPositions(model, hand, corners);
  }

  /**
   * Finds the best possible move among all grid positions.
   *
   * @param model      the game model
   * @param hand       the player's hand of cards
   * @param dimensions the grid dimensions
   * @return the best move found, or null if no valid moves exist
   */
  private AIMove findBestMoveInGrid(MainModelInterface model, List<Card> hand, int[] dimensions) {
    List<Position> allPositions = getAllGridPositions(dimensions[0], dimensions[1]);
    return findBestMoveInPositions(model, hand, allPositions);
  }

  /**
   * Generates a list of all positions in the grid.
   *
   * @param rows number of rows in the grid
   * @param cols number of columns in the grid
   * @return list of all grid positions
   */
  private List<Position> getAllGridPositions(int rows, int cols) {
    List<Position> positions = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        positions.add(new Position(r, c));
      }
    }
    return positions;
  }

  /**
   * Finds the best possible move among a given list of positions.
   *
   * @param model     the game model
   * @param hand      the player's hand of cards
   * @param positions the positions to evaluate
   * @return the best move found, or null if no valid moves exist
   */
  private AIMove findBestMoveInPositions(MainModelInterface model, List<Card> hand,
                                         List<Position> positions) {
    AIMove bestMove = null;
    int bestScore = -1;

    for (Position pos : positions) {
      if (isValidPosition(model, pos)) {
        bestMove = evaluateCardsAtPosition(model, hand, pos, bestMove, bestScore);
        if (bestMove != null) {
          bestScore = bestMove.getScore();
        }
      }
    }

    return bestMove;
  }

  /**
   * Checks if a position is valid for card placement.
   *
   * @param model the game model
   * @param pos   the position to check
   * @return true if the position is valid for card placement
   */
  private boolean isValidPosition(MainModelInterface model, Position pos) {
    return !model.isHole(pos.row, pos.col) && model.getCardAt(pos.row, pos.col) == null;
  }

  /**
   * Evaluates all possible cards at a given position.
   *
   * @param model       the game model
   * @param hand        the player's hand of cards
   * @param pos         the position to evaluate
   * @param currentBest the current best move
   * @param bestScore   the current best score
   * @return the best move found, or the current best if no better move is found
   */
  private AIMove evaluateCardsAtPosition(MainModelInterface model, List<Card> hand,
                                         Position pos, AIMove currentBest, int bestScore) {
    AIMove bestMove = currentBest;
    int currentBestScore = bestScore;

    for (Card card : hand) {
      if (model.canPlaceCard(pos.row, pos.col, card)) {
        int score = evaluateMove(model, pos, card);
        if (score > currentBestScore) {
          currentBestScore = score;

          Card moveCard = new MockCard(card.getName(),
                  card.getAttackPower(Direction.NORTH),
                  card.getAttackPower(Direction.SOUTH),
                  card.getAttackPower(Direction.EAST),
                  card.getAttackPower(Direction.WEST));
          bestMove = new AIMove(moveCard, pos, Math.max(score, 0));
        }
      }
    }

    return bestMove;
  }

  /**
   * Evaluates the potential value of placing a card at a given position.
   *
   * @param model the game model
   * @param pos   the position to evaluate
   * @param card  the card to evaluate
   * @return a score representing the value of the move
   */
  private int evaluateMove(MainModelInterface model, Position pos, Card card) {
    int score = calculatePositionScore(model, pos);
    score += calculateCardStrengthScore(card);
    return score;
  }

  /**
   * Calculates the score based on the position on the board.
   *
   * @param model the game model
   * @param pos   the position to evaluate
   * @return the position-based score component
   */
  private int calculatePositionScore(MainModelInterface model, Position pos) {
    int rows = model.getGridDimensions()[0];
    int cols = model.getGridDimensions()[1];

    if ((pos.row == 0 || pos.row == rows - 1) && (pos.col == 0 || pos.col == cols - 1)) {
      return 1000;  // Corner position bonus
    }
    return 0;
  }

  /**
   * Calculates the score based on the card's attack values.
   *
   * @param card the card to evaluate
   * @return the card strength-based score component
   */
  private int calculateCardStrengthScore(Card card) {
    int maxAttack = Math.max(
            Math.max(card.getAttackPower(Direction.NORTH), card.getAttackPower(Direction.SOUTH)),
            Math.max(card.getAttackPower(Direction.EAST), card.getAttackPower(Direction.WEST))
    );

    return (maxAttack * 2) +
            card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST);
  }
}