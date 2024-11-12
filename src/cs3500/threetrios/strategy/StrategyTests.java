package cs3500.threetrios.strategy;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;

/**
 * Class testing the strategies in the program.
 */
public class StrategyTests {
  private StringBuilder log;
  private MockThreeTriosModel model;
  private Player redPlayer;
  private Player bluePlayer;
  private CornerStrat cornerStrategy;
  private MaxFlipsStrat maxFlipsStrategy;
  private Card strongCard;
  private Card weakCard;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockThreeTriosModel(log) {
      @Override
      public List<Card> getPlayerHand(Player player) {
        log.append("Getting hand for player: ").append(player.getColor()).append("\n");
        return new ArrayList<>(player.getHand());
      }
    };
    redPlayer = new MockPlayer("RED");
    bluePlayer = new MockPlayer("BLUE");
    cornerStrategy = new CornerStrat();
    maxFlipsStrategy = new MaxFlipsStrat();

    strongCard = new MockCard("strong", 9, 9, 9, 9);
    weakCard = new MockCard("weak", 1, 1, 1, 1);
  }

  @Test
  public void testCornerStratPrefersCorners() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    AIMove move = cornerStrategy.findBestMove(model, redPlayer);

    assertTrue(model.verifiedAllCorners());

    boolean isCorner = (move.getPosition().row == 0 && move.getPosition().col == 0) ||
            (move.getPosition().row == 0 && move.getPosition().col == model.getGridDimensions()[1] - 1) ||
            (move.getPosition().row == model.getGridDimensions()[0] - 1 && move.getPosition().col == 0) ||
            (move.getPosition().row == model.getGridDimensions()[0] - 1 &&
                    move.getPosition().col == model.getGridDimensions()[1] - 1);

    assertTrue(isCorner);
  }

  @Test
  public void testCornerStratConsidersCardStrength() {
    redPlayer.addCardToHand(weakCard);
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    AIMove move = cornerStrategy.findBestMove(model, redPlayer);

    assertEquals(strongCard.getName(), move.getCard().getName());
  }

  @Test
  public void testMaxFlipsStratChoosesHighestFlips() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    Position lowFlips = new Position(1, 1);
    Position highFlips = new Position(2, 2);

    model.setFlipValue(lowFlips, 1);
    model.setFlipValue(highFlips, 3);

    AIMove move = maxFlipsStrategy.findBestMove(model, redPlayer);

    assertEquals(highFlips.row, move.getPosition().row);
    assertEquals(highFlips.col, move.getPosition().col);
  }

  @Test
  public void testMaxFlipsStratHandlesNoValidMoves() {
    MockThreeTriosModel fullModel = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        return false;
      }

      @Override
      public List<Card> getPlayerHand(Player player) {
        log.append("Getting hand for player: ").append(player.getColor()).append("\n");
        return new ArrayList<>(player.getHand());
      }
    };

    redPlayer.addCardToHand(strongCard);
    fullModel.setCurrentPlayer(redPlayer);

    AIMove move = maxFlipsStrategy.findBestMove(fullModel, redPlayer);
    assertNotNull(move);
  }

  @Test
  public void testMaxFlipsStratConsidersMultipleCards() {
    redPlayer.addCardToHand(weakCard);
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    Position pos1 = new Position(1, 1);
    Position pos2 = new Position(2, 2);

    model.setFlipValue(pos1, 2);
    model.setFlipValue(pos2, 4);

    AIMove move = maxFlipsStrategy.findBestMove(model, redPlayer);

    assertEquals(pos2.row, move.getPosition().row);
    assertEquals(pos2.col, move.getPosition().col);
  }

  @Test
  public void testMaxFlipsStratLogsCorrectly() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    maxFlipsStrategy.findBestMove(model, redPlayer);

    String logContent = log.toString();
    assertTrue(logContent.contains("Getting hand for player: RED"));
    assertTrue(logContent.contains("Checked if can place at"));
  }
}