import org.junit.Before;
import org.junit.Test;

import cs3500.threetrios.controller.HumanPlayer;
import cs3500.threetrios.features.MockViewFeatures;
import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.MockPlayer;
import cs3500.threetrios.strategy.MockStrategy;
import cs3500.threetrios.strategy.MockThreeTriosModel;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality of the methods in the HumanPlayer class.
 */
public class HumanPlayerTest {
  private StringBuilder log;
  private Player basePlayer;
  private ViewFeatures features;
  private HumanPlayer humanPlayer;

  @Before
  public void setUp() {
    log = new StringBuilder();
    basePlayer = new MockPlayer("BLUE");
    features = new MockViewFeatures(log);
    humanPlayer = new HumanPlayer(basePlayer, features);
    Card testCard = new MockCard("test", 5, 5, 5, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullBasePlayer() {
    new HumanPlayer(null, features);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullFeatures() {
    new HumanPlayer(basePlayer, null);
  }

  @Test
  public void testGetColor() {
    assertEquals("BLUE", humanPlayer.getColor());
  }


  @Test(expected = UnsupportedOperationException.class)
  public void testSetStrategyThrowsException() {
    humanPlayer.setStrategy(new MockStrategy(log));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetNextMoveThrowsException() {
    humanPlayer.getNextMove(new MockThreeTriosModel(log));
  }
}
