package cs3500.threetrios.strategy;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;

import java.util.List;

/**
 * A strategy that looks ahead to minimize the opponent's best possible moves.
 * Uses another strategy to evaluate the opponent's potential moves.
 */
public class MinimaxStrat implements AIStrategy {
  private final AIStrategy opponentStrategy;
  private static final int MAX_DEPTH = 2;

  /**
   * Constructs a minimax strategy with a given opponent strategy.
   *
   * @param opponentStrategy the strategy used to evaluate opponent moves
   * @throws IllegalArgumentException if opponentStrategy is null
   */
  public MinimaxStrat(AIStrategy opponentStrategy) {
    if (opponentStrategy == null) {
      throw new IllegalArgumentException("Opponent strategy cannot be null");
    }
    this.opponentStrategy = opponentStrategy;
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    validateInputs(model, player);
    List<Card> hand = getPlayerHand(model, player);
    return findBestPossibleMove(model, hand, player);
  }

  /**
   * Validates the model and player inputs.
   *
   * @param model the game model
   * @param player the current player
   * @throws IllegalArgumentException if either input is null
   */
  private void validateInputs(MainModelInterface model, Player player) {
    if (model == null || player == null) {
      throw new IllegalArgumentException("Model and player cannot be null");
    }
  }

  /**
   * Retrieves the player's hand from the model.
   *
   * @param model the game model
   * @param player the current player
   * @return list of cards in player's hand
   * @throws IllegalStateException if hand is empty or player not found
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
   * Finds the best possible move by evaluating all valid positions.
   *
   * @param model the game model
   * @param hand the player's hand
   * @param player the current player
   * @return the best move found or a default move if no valid moves exist
   */
  private AIMove findBestPossibleMove(MainModelInterface model, List<Card> hand, Player player) {
    int bestScore = Integer.MIN_VALUE;
    AIMove bestMove = null;

    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        if (model.isHole(row, col) || model.getCardAt(row, col) != null) {
          continue;
        }

        for (Card card : hand) {
          if (model.canPlaceCard(row, col, card)) {
            int score = evaluateMove(model, new Position(row, col), card, player);
            // Ensure positive score and create new card instance
            if (score > bestScore) {
              Card moveCard = new MockCard(card.getName(),
                      card.getAttackPower(Direction.NORTH),
                      card.getAttackPower(Direction.SOUTH),
                      card.getAttackPower(Direction.EAST),
                      card.getAttackPower(Direction.WEST));
              bestScore = score;
              bestMove = new AIMove(moveCard, new Position(row, col), Math.max(score + 1000, 0));
            }
          }
        }
      }
    }

    return bestMove != null ? bestMove : StrategyUtil.getDefaultMove(model, player);
  }

  /**
   * Evaluates a move considering both immediate benefits and opponent's response.
   *
   * @param model the game model
   * @param pos the position to evaluate
   * @param card the card to evaluate
   * @param currentPlayer the current player
   * @return the evaluated score for the move
   * @throws IllegalArgumentException if card ownership is invalid
   */
  private int evaluateMove(MainModelInterface model, Position pos,
                           Card card, Player currentPlayer) {
    Player cardOwner = model.getCardOwnerAt(pos.row, pos.col);
    if (cardOwner != null && !cardOwner.getColor().equals(currentPlayer.getColor())) {
      throw new IllegalArgumentException("Invalid card owner");
    }

    int immediateScore = calculateImmediateScore(model, pos, card, currentPlayer);
    Player opponent = findOpponent(model, currentPlayer);

    if (opponent == null) {
      return immediateScore;
    }
    try {
      AIMove opponentMove = opponentStrategy.findBestMove(model, opponent);
      int opponentScore = opponentMove.getScore();
      return immediateScore - (opponentScore / 2); // Weight opponent moves less
    } catch (Exception e) {
      return immediateScore;
    }
  }

  /**
   * Finds the opponent player in the game.
   *
   * @param model the game model
   * @param currentPlayer the current player
   * @return the opponent player or null if not found
   */
  private Player findOpponent(MainModelInterface model, Player currentPlayer) {
    for (Player p : model.getPlayers()) {
      if (!p.getColor().equals(currentPlayer.getColor())) {
        return p;
      }
    }
    return null;
  }

  /**
   * Calculates the immediate value of a move.
   *
   * @param model the game model
   * @param pos the position to evaluate
   * @param card the card to evaluate
   * @param player the current player
   * @return the immediate score for the move
   */
  private int calculateImmediateScore(MainModelInterface model, Position pos, Card card,
                                      Player player) {
    int score = 0;

    int flips = model.getFlippableCards(pos.row, pos.col, card);
    if (flips < 0) {
      throw new IllegalArgumentException("Invalid flip count");
    }

    score += flips * 1000;
    score += (card.getAttackPower(Direction.NORTH) +
            card.getAttackPower(Direction.SOUTH) +
            card.getAttackPower(Direction.EAST) +
            card.getAttackPower(Direction.WEST)) * 10;

    if ((pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1) &&
            (pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1)) {
      score += 500;
    } else if (pos.row == 0 || pos.row == model.getGridDimensions()[0] - 1 ||
            pos.col == 0 || pos.col == model.getGridDimensions()[1] - 1) {
      score += 200;
    }
    return score;
  }
}