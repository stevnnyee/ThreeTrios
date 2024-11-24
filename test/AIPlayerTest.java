import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.threetrios.controller.AIPlayer;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.MockPlayer;
import cs3500.threetrios.strategy.MockStrategy;
import cs3500.threetrios.strategy.MockThreeTriosModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AIPlayerTest {
  private StringBuilder log;
  private Player basePlayer;
  private AIPlayer aiPlayer;
  private AIStrategy strategy;
  private MockThreeTriosModel model;
  private Card testCard;

  @Before
  public void setUp() {
    log = new StringBuilder();
    basePlayer = new MockPlayer("RED");
    strategy = new MockStrategy(log);
    model = new MockThreeTriosModel(log);
    testCard = new MockCard("test", 5, 5, 5, 5);
    aiPlayer = new AIPlayer(basePlayer);
    aiPlayer.setStrategy(strategy);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullBasePlayer() {
    new AIPlayer(null);
  }

  @Test
  public void testGetColor() {
    assertEquals("RED", aiPlayer.getColor());
  }

  @Test
  public void testGetHand() {
    List<Card> hand = new ArrayList<>();
    hand.add(testCard);
    ((MockPlayer)basePlayer).getHand();
    assertEquals(hand, aiPlayer.getHand());
  }

  @Test
  public void testGetNextMove() {
    AIMove move = aiPlayer.getNextMove(model);
    assertTrue(log.toString().contains("Finding best move"));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNextMoveNoStrategy() {
    aiPlayer = new AIPlayer(basePlayer);
    aiPlayer.getNextMove(model);
  }
}