import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;
import cs3500.threetrios.strategy.CompositeStrategy;
import cs3500.threetrios.strategy.CornerStrat;
import cs3500.threetrios.strategy.DefensiveStrat;
import cs3500.threetrios.strategy.MaxFlipsStrat;
import cs3500.threetrios.strategy.MinimaxStrat;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.MockPlayer;
import cs3500.threetrios.strategy.MockThreeTriosModel;
import cs3500.threetrios.strategy.Position;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Class testing the strategies in the program.
 */
public class StrategyTests {
  private StringBuilder log;
  private MockThreeTriosModel model;
  private Player redPlayer;
  private CornerStrat cornerStrategy;
  private MaxFlipsStrat maxFlipsStrategy;
  private Card strongCard;
  private Card weakCard;
  private DefensiveStrat defensiveStrategy;
  private MinimaxStrat minimaxStrategy;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockThreeTriosModel(log) {
      private final List<Position> checkedPositions = new ArrayList<>();

      @Override
      public List<Card> getPlayerHand(Player player) {
        log.append("Getting hand for player: ").append(player.getColor()).append("\n");
        List<Card> hand = new ArrayList<>(player.getHand());
        if (hand.isEmpty()) {
          hand.add(new MockCard("defaultCard", 5, 5, 5, 5));
        }
        return hand;
      }

      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        log.append(String.format("Checked if can place at (%d,%d)\n", row, col));
        checkedPositions.add(new Position(row, col));
        return true;
      }

      @Override
      public boolean verifiedAllCorners() {
        int rows = getGridDimensions()[0];
        int cols = getGridDimensions()[1];

        boolean topLeft = false;
        boolean topRight = false;
        boolean bottomLeft = false;
        boolean bottomRight = false;

        for (Position pos : checkedPositions) {
          if (pos.row == 0 && pos.col == 0) {
            topLeft = true;
          }
          if (pos.row == 0 && pos.col == cols - 1) {
            topRight = true;
          }
          if (pos.row == rows - 1 && pos.col == 0) {
            bottomLeft = true;
          }
          if (pos.row == rows - 1 && pos.col == cols - 1) {
            bottomRight = true;
          }
        }

        return topLeft && topRight && bottomLeft && bottomRight;
      }

      @Override
      public int[] getGridDimensions() {
        return new int[]{5, 7};
      }
    };

    redPlayer = new MockPlayer("RED");
    Player bluePlayer = new MockPlayer("BLUE");
    cornerStrategy = new CornerStrat();
    maxFlipsStrategy = new MaxFlipsStrat();
    defensiveStrategy = new DefensiveStrat();
    minimaxStrategy = new MinimaxStrat(maxFlipsStrategy); // using MaxFlips as opponent strategy
    strongCard = new MockCard("strong", 9, 9, 9, 9);
    weakCard = new MockCard("weak", 1, 1, 1, 1);
    model.setCurrentPlayer(redPlayer);
  }


  @Test
  public void testCornerStratPrefersCorners() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    AIMove move = cornerStrategy.findBestMove(model, redPlayer);

    assertTrue(model.verifiedAllCorners());
    boolean isCorner = (move.getPosition().row == 0 && move.getPosition().col == 0)
            || (move.getPosition().row == 0 && move.getPosition().col == 6)
            || (move.getPosition().row == 4 && move.getPosition().col == 0)
            || (move.getPosition().row == 4 && move.getPosition().col == 6);

    assertTrue(isCorner);
    assertEquals(strongCard, move.getCard());
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

  @Test
  public void testDefensiveStratBasicMove() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    AIMove move = defensiveStrategy.findBestMove(model, redPlayer);
    assertNotNull(move);
    assertNotNull(move.getCard());
    assertTrue(move.getPosition().row >= 0 && move.getPosition().col >= 0);
  }

  @Test
  public void testMinimaxStratBasicMove() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    AIMove move = minimaxStrategy.findBestMove(model, redPlayer);
    assertNotNull(move);
    assertNotNull(move.getCard());
    assertTrue(move.getPosition().row >= 0 && move.getPosition().col >= 0);
  }


  @Test
  public void testCompositeStrategyBasic() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    List<AIStrategy> strategies = Arrays.asList(maxFlipsStrategy, cornerStrategy);
    List<Integer> weights = Arrays.asList(1, 1);
    CompositeStrategy compositeStrategy = new CompositeStrategy(strategies, weights);

    AIMove move = compositeStrategy.findBestMove(model, redPlayer);
    assertNotNull(move);
    assertNotNull(move.getCard());
    assertTrue(move.getPosition().row >= 0 && move.getPosition().col >= 0);
  }

  @Test
  public void testCompositeWithAllStrategies() {
    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    List<AIStrategy> strategies = Arrays.asList(
            maxFlipsStrategy,
            cornerStrategy,
            defensiveStrategy,
            minimaxStrategy
    );
    List<Integer> weights = Arrays.asList(1, 1, 1, 1);
    CompositeStrategy compositeStrategy = new CompositeStrategy(strategies, weights);

    AIMove move = compositeStrategy.findBestMove(model, redPlayer);
    assertNotNull(move);
    assertNotNull(move.getCard());
    assertTrue(move.getPosition().row < model.getGridDimensions()[0]
            && move.getPosition().col < model.getGridDimensions()[1]);
  }

  @Test
  public void testAIMoveConstructorAndGetters() {
    Card testCard = new MockCard("test", 5, 5, 5, 5);
    Position testPosition = new Position(1, 2);
    int testScore = 10;

    AIMove move = new AIMove(testCard, testPosition, testScore);

    assertEquals(testCard, move.getCard());
    assertEquals(testPosition, move.getPosition());
    assertEquals(testScore, move.getScore());
  }

  @Test
  public void testAIMoveComparePosition() {
    Card card = new MockCard("test", 5, 5, 5, 5);
    AIMove move1 = new AIMove(card, new Position(1, 1), 10);
    AIMove move2 = new AIMove(card, new Position(1, 2), 10);
    AIMove move3 = new AIMove(card, new Position(2, 1), 10);

    assertTrue(move1.comparePosition(move2) < 0);
    assertTrue(move2.comparePosition(move1) > 0);

    assertTrue(move1.comparePosition(move3) < 0);
    assertTrue(move3.comparePosition(move1) > 0);

    assertEquals(0,
            move1.comparePosition(new AIMove(card, new Position(1, 1), 5)));
  }

  @Test
  public void testAIMoveWithNullCard() {
    Position pos = new Position(0, 0);
    AIMove move = new AIMove(null, pos, 0);
    assertNull(move.getCard());
    assertEquals(pos, move.getPosition());
  }

  @Test
  public void testStrategyTieBreaking() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log);
    Player player = new MockPlayer("RED");
    Card card = new MockCard("test", 5, 5, 5, 5);
    player.addCardToHand(card);

    Position pos1 = new Position(0, 1);
    Position pos2 = new Position(0, 0);
    model.setFlipValue(pos1, 3);
    model.setFlipValue(pos2, 3);

    MaxFlipsStrat strategy = new MaxFlipsStrat();
    AIMove move = strategy.findBestMove(model, player);

    assertEquals(0, move.getPosition().row);
    assertEquals(0, move.getPosition().col);
  }

  @Test
  public void testStrategyWithEmptyHand() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log);
    Player player = new MockPlayer("RED");
    MaxFlipsStrat strategy = new MaxFlipsStrat();

    AIMove move = strategy.findBestMove(model, player);

    assertNotNull(move);
    assertNotNull(move.getPosition());
  }

  @Test
  public void testStrategyInvalidPositions() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        return row == 0 && col == 0;
      }
    };

    Player player = new MockPlayer("RED");
    Card card = new MockCard("test", 5, 5, 5, 5);
    player.addCardToHand(card);

    MaxFlipsStrat strategy = new MaxFlipsStrat();
    AIMove move = strategy.findBestMove(model, player);
    assertEquals(0, move.getPosition().row);
    assertEquals(0, move.getPosition().col);
  }

  @Test
  public void testStrategyWithFullBoard() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        return false;
      }
    };

    Player player = new MockPlayer("RED");
    Card card = new MockCard("test", 5, 5, 5, 5);
    player.addCardToHand(card);

    CornerStrat strategy = new CornerStrat();
    AIMove move = strategy.findBestMove(model, player);

    assertEquals(0, move.getPosition().row);
    assertEquals(0, move.getPosition().col);
  }

  @Test
  public void testCompositeStrategyWeights() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log);
    Player player = new MockPlayer("RED");
    Card card = new MockCard("test", 5, 5, 5, 5);
    player.addCardToHand(card);

    MaxFlipsStrat maxFlips = new MaxFlipsStrat();
    CornerStrat corner = new CornerStrat();

    List<AIStrategy> strategies = Arrays.asList(maxFlips, corner);
    List<Integer> weights = Arrays.asList(2, 1);

    CompositeStrategy composite = new CompositeStrategy(strategies, weights);
    AIMove move = composite.findBestMove(model, player);

    assertNotNull(move);
    assertTrue(move.getScore() >= 0);
  }

  // Override methods
  @Test
  public void testMinimaxStratLooksAhead() {
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public int getFlippableCards(int row, int col, Card card) {
        if (row == 1 && col == 1) {
          return 2;
        }
        return 1;
      }

      @Override
      public List<Card> getPlayerHand(Player player) {
        List<Card> hand = new ArrayList<>();
        hand.add(strongCard);
        return hand;
      }
    };

    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    MinimaxStrat minimaxStrat = new MinimaxStrat(new DefensiveStrat());
    AIMove move = minimaxStrat.findBestMove(model, redPlayer);

    assertEquals(1, move.getPosition().row);
    assertEquals(1, move.getPosition().col);
    assertTrue(move.getScore() >= 0);
  }

  @Test
  public void testDefensiveStratPositioning() {
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        return (row == 0 && col == 0) || (row == 1 && col == 1);
      }

      @Override
      public List<Card> getPlayerHand(Player player) {
        List<Card> hand = new ArrayList<>();
        if (player.getColor().equals(Color.BLUE)) {
          hand.add(weakCard);
        } else {
          hand.add(strongCard);
        }
        return hand;
      }
    };

    redPlayer.addCardToHand(strongCard);
    model.setCurrentPlayer(redPlayer);

    DefensiveStrat defensiveStrat = new DefensiveStrat();
    AIMove move = defensiveStrat.findBestMove(model, redPlayer);

    assertEquals(0, move.getPosition().row);
    assertEquals(0, move.getPosition().col);
    assertTrue(move.getScore() >= 0);
  }

  @Test
  public void testMaxFlipsStrategyChecksAllLocations() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      private final Set<Position> checkedPositions = new HashSet<>();

      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        checkedPositions.add(new Position(row, col));
        log.append(String.format("Checked position (%d,%d)\n", row, col));
        return true;
      }

      @Override
      public List<Card> getPlayerHand(Player player) {
        return Arrays.asList(new MockCard("test", 5, 5, 5, 5));
      }
    };

    MaxFlipsStrat strategy = new MaxFlipsStrat();
    strategy.findBestMove(model, redPlayer);

    String transcriptContent = log.toString();
    for (int row = 0; row < model.getGridDimensions()[0]; row++) {
      for (int col = 0; col < model.getGridDimensions()[1]; col++) {
        assertTrue(String.format("Position (%d,%d) was not checked", row, col),
                transcriptContent.contains(String.format("Checked position (%d,%d)", row, col)));
      }
    }
  }

  @Test
  public void testCornerStrategyMethodCalls() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      private final List<String> methodCalls = new ArrayList<>();

      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        methodCalls.add(String.format("canPlaceCard(%d,%d)", row, col));
        log.append(String.format("Attempted placement at (%d,%d)\n", row, col));
        return true;
      }

      @Override
      public Card getCardAt(int row, int col) {
        methodCalls.add(String.format("getCardAt(%d,%d)", row, col));
        return null;
      }
    };

    redPlayer.addCardToHand(strongCard);
    CornerStrat strategy = new CornerStrat();
    strategy.findBestMove(model, redPlayer);

    String transcriptContent = log.toString();
    assertTrue(transcriptContent.contains("Attempted placement at (0,0)"));
    assertTrue(transcriptContent.contains("Attempted placement at (0,6)"));
    assertTrue(transcriptContent.contains("Attempted placement at (4,0)"));
    assertTrue(transcriptContent.contains("Attempted placement at (4,6)"));
  }

  @Test
  public void testStrategyForcedMoveSelection() {
    StringBuilder log = new StringBuilder();
    Position forcedPosition = new Position(2, 2);
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        log.append(String.format("Checking position (%d,%d)\n", row, col));
        return row == forcedPosition.row && col == forcedPosition.col;
      }

      @Override
      public int getFlippableCards(int row, int col, Card card) {
        return (row == forcedPosition.row && col == forcedPosition.col) ? 100 : 0;
      }
    };

    redPlayer.addCardToHand(strongCard);
    MaxFlipsStrat strategy = new MaxFlipsStrat();
    AIMove move = strategy.findBestMove(model, redPlayer);

    assertEquals(forcedPosition.row, move.getPosition().row);
    assertEquals(forcedPosition.col, move.getPosition().col);
    assertTrue(log.toString().contains(
            String.format("Checking position (%d,%d)", forcedPosition.row, forcedPosition.col)));
  }

  @Test
  public void testComparisonBetweenStrategies() {
    StringBuilder log = new StringBuilder();
    MockThreeTriosModel model = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        log.append(String.format("%s checking (%d,%d)\n",
                Thread.currentThread().getStackTrace()[2].getMethodName(), row, col));
        return true;
      }

      @Override
      public int getFlippableCards(int row, int col, Card card) {
        return (row == 0 && col == 0) ? 3 : 1;
      }
    };

    redPlayer.addCardToHand(strongCard);

    MaxFlipsStrat maxFlipsStrat = new MaxFlipsStrat();
    CornerStrat cornerStrat = new CornerStrat();

    AIMove maxFlipsMove = maxFlipsStrat.findBestMove(model, redPlayer);
    AIMove cornerMove = cornerStrat.findBestMove(model, redPlayer);

    assertEquals(0, maxFlipsMove.getPosition().row);
    assertEquals(0, maxFlipsMove.getPosition().col);
    assertEquals(0, cornerMove.getPosition().row);
    assertEquals(0, cornerMove.getPosition().col);

    String transcriptContent = log.toString();
    assertTrue(transcriptContent.contains("findBestMove checking"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyMismatchedWeights() {
    List<AIStrategy> strategies = Arrays.asList(maxFlipsStrategy, cornerStrategy);
    List<Integer> weights = Arrays.asList(1);
    new CompositeStrategy(strategies, weights);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyNullStrategies() {
    new CompositeStrategy(null, Arrays.asList(1, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyNullWeights() {
    List<AIStrategy> strategies = Arrays.asList(maxFlipsStrategy, cornerStrategy);
    new CompositeStrategy(strategies, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyEmptyLists() {
    new CompositeStrategy(new ArrayList<>(), new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMinimaxStratNullOpponentStrategy() {
    new MinimaxStrat(null);
  }

  @Test(expected = IllegalStateException.class)
  public void testMaxFlipsStratWithInvalidGridDimensions() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public int[] getGridDimensions() {
        return new int[]{-1, -1};
      }
    };
    maxFlipsStrategy.findBestMove(invalidModel, redPlayer);
  }

  @Test(expected = IllegalStateException.class)
  public void testCornerStratWithNullHand() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public List<Card> getPlayerHand(Player player) {
        return null;
      }
    };
    cornerStrategy.findBestMove(invalidModel, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAIMoveWithNullPosition() {
    new AIMove(strongCard, null, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDefensiveStratWithNullModel() {
    defensiveStrategy.findBestMove(null, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCornerStratWithNullPlayer() {
    cornerStrategy.findBestMove(model, null);
  }

  @Test(expected = IllegalStateException.class)
  public void testMaxFlipsStratWithInconsistentModel() {
    MockThreeTriosModel inconsistentModel = new MockThreeTriosModel(log) {
      @Override
      public boolean canPlaceCard(int row, int col, Card card) {
        return true;
      }

      @Override
      public Card getCardAt(int row, int col) {
        return new MockCard("inconsistent", 1, 1, 1, 1);
      }
    };
    maxFlipsStrategy.findBestMove(inconsistentModel, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMinimaxStratWithInvalidFlipCount() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public int getFlippableCards(int row, int col, Card card) {
        return -1;
      }
    };
    minimaxStrategy.findBestMove(invalidModel, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyWithNegativeWeights() {
    List<AIStrategy> strategies = Arrays.asList(maxFlipsStrategy, cornerStrategy);
    List<Integer> weights = Arrays.asList(-1, 1);
    new CompositeStrategy(strategies, weights);
  }

  @Test(expected = IllegalStateException.class)
  public void testCornerStratWithInvalidBoardState() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public boolean isHole(int row, int col) {
        return true;
      }
    };
    cornerStrategy.findBestMove(invalidModel, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAIMoveWithInvalidScore() {
    new AIMove(strongCard, new Position(0, 0), -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMinimaxStratWithInvalidCardOwner() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public Player getCardOwnerAt(int row, int col) {
        return new MockPlayer("INVALID");
      }
    };
    minimaxStrategy.findBestMove(invalidModel, redPlayer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCompositeStrategyWithNullStrategyInList() {
    List<AIStrategy> strategies = Arrays.asList(maxFlipsStrategy, null);
    List<Integer> weights = Arrays.asList(1, 1);
    new CompositeStrategy(strategies, weights);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testDefensiveStratWithInvalidBoardAccess() {
    MockThreeTriosModel invalidModel = new MockThreeTriosModel(log) {
      @Override
      public Card getCardAt(int row, int col) {
        throw new IndexOutOfBoundsException();
      }
    };
    defensiveStrategy.findBestMove(invalidModel, redPlayer);
  }

}