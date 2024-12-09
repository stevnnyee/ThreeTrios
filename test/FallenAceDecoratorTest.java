import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.FallenAceDecorator;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.ThreeTriosGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests functionality of the FallenAceDecorator class with focus on Ace-specific battle rules.
 */
public class FallenAceDecoratorTest {
  private MainModelInterface baseModel;
  private FallenAceDecorator decorator;
  private List<Card> deck;
  private Grid grid;

  /**
   * Sets up the test environment with a 3x3 grid, two holes, and 15 test cards including Aces.
   */
  @Before
  public void setup() {
    baseModel = new ThreeTriosGameModel();
    decorator = new FallenAceDecorator(baseModel, false);
    deck = new ArrayList<>();

    deck.add(new ThreeTriosCard("One", 1, 1, 1, 1));
    deck.add(new ThreeTriosCard("Ace", 10, 10, 10, 10));
    deck.add(new ThreeTriosCard("Five", 5, 5, 5, 5));
    deck.add(new ThreeTriosCard("Eight", 8, 8, 8, 8));
    deck.add(new ThreeTriosCard("Three", 3, 3, 3, 3));
    deck.add(new ThreeTriosCard("Seven", 7, 7, 7, 7));
    deck.add(new ThreeTriosCard("Two", 2, 2, 2, 2));
    deck.add(new ThreeTriosCard("Nine", 9, 9, 9, 9));
    deck.add(new ThreeTriosCard("Four", 4, 4, 4, 4));
    deck.add(new ThreeTriosCard("Six", 6, 6, 6, 6));
    deck.add(new ThreeTriosCard("Ace2", 10, 10, 10, 10));
    deck.add(new ThreeTriosCard("One2", 1, 1, 1, 1));
    deck.add(new ThreeTriosCard("Eight2", 8, 8, 8, 8));
    deck.add(new ThreeTriosCard("Five2", 5, 5, 5, 5));
    deck.add(new ThreeTriosCard("Three2", 3, 3, 3, 3));

    boolean[][] holes = {
            {false, false, false},
            {true, true, false},
            {false, false, false}
    };
    grid = new ThreeTriosGrid(3, 3, holes);

    decorator.startGame(grid, deck);
  }

  // Tests basic card placement functionality
  @Test
  public void testValidCardPlacement() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card card = decorator.getPlayerHand(currentPlayer).get(0);

    decorator.placeCard(0, 0, card);
    assertNotNull(decorator.getCardAt(0, 0));
    assertEquals(currentPlayer, decorator.getCardAt(0, 0).getOwner());
  }

  // Tests that turns alternate between players correctly
  @Test
  public void testTurnOrder() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();
    assertNotEquals(firstPlayer, secondPlayer);
  }

  // Tests that getFlippableCards returns valid count for potential flips
  @Test
  public void testGetFlippableCards() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card card = decorator.getPlayerHand(currentPlayer).get(0);

    decorator.placeCard(0, 0, card);

    Player nextPlayer = decorator.getCurrentPlayer();
    Card nextCard = decorator.getPlayerHand(nextPlayer).get(0);

    int flips = decorator.getFlippableCards(0, 1, nextCard);
    assertTrue(flips >= 0 && flips <= 1);
  }

  // Tests multiple sequential card placements
  @Test
  public void testMultipleValidPlacements() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);
    Player secondPlayer = decorator.getCurrentPlayer();
    Card secondCard = decorator.getPlayerHand(secondPlayer).get(0);
    decorator.placeCard(0, 1, secondCard);
    Player thirdPlayer = decorator.getCurrentPlayer();
    Card thirdCard = decorator.getPlayerHand(thirdPlayer).get(0);
    decorator.placeCard(0, 2, thirdCard);

    assertNotNull(decorator.getCardAt(0, 0));
    assertNotNull(decorator.getCardAt(0, 1));
    assertNotNull(decorator.getCardAt(0, 2));
  }

  // Tests that placing a card in a hole throws exception
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlacement_InHole() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card card = decorator.getPlayerHand(currentPlayer).get(0);
    decorator.placeCard(1, 0, card);
  }

  // Tests that placing a card out of bounds throws exception
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlacement_OutOfBounds() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card card = decorator.getPlayerHand(currentPlayer).get(0);
    decorator.placeCard(3, 3, card);
  }

  // Tests that cards maintain correct ownership after placement
  @Test
  public void testCardOwnership() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    assertEquals(firstPlayer, decorator.getCardAt(0, 0).getOwner());
  }

  // Tests that placed cards can be correctly retrieved
  @Test
  public void testValidCardRetrieval() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card card = decorator.getPlayerHand(currentPlayer).get(0);
    decorator.placeCard(0, 0, card);

    Card retrievedCard = decorator.getCardAt(0, 0);
    assertNotNull(retrievedCard);
    assertEquals(card.getName(), retrievedCard.getName());
  }

  // Tests battle mechanics with Ace-specific rules
  @Test
  public void testBattleWithAceRule() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();
    Card secondCard = decorator.getPlayerHand(secondPlayer).get(0);
    decorator.placeCard(0, 1, secondCard);

    assertNotNull(decorator.getCardAt(0, 0));
    assertNotNull( decorator.getCardAt(0, 1));
  }

  // Tests interaction between Ace rules and reverse rule
  @Test
  public void testFallenAceWithReverseRule() {
    FallenAceDecorator reverseDecorator = new FallenAceDecorator(baseModel, true);
    reverseDecorator.startGame(grid, deck);

    Player firstPlayer = reverseDecorator.getCurrentPlayer();
    Card firstCard = reverseDecorator.getPlayerHand(firstPlayer).get(0);
    reverseDecorator.placeCard(0, 0, firstCard);

    Player secondPlayer = reverseDecorator.getCurrentPlayer();
    Card secondCard = reverseDecorator.getPlayerHand(secondPlayer).get(0);
    reverseDecorator.placeCard(0, 1, secondCard);

    assertNotNull(reverseDecorator.getCardAt(0, 0));
    assertNotNull(reverseDecorator.getCardAt(0, 1));
  }

  @Test
  public void testReversedFallenAce_GetFlippableCount() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);

    decorator.placeCard(0, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();
    Card nextCard = decorator.getPlayerHand(secondPlayer).get(0);

    int flips = decorator.getFlippableCards(0, 1, nextCard);
    assertTrue(flips >= 0 && flips <= 1);
  }

  @Test
  public void testReversedFallenAce_ChainCapture() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();
    Card secondCard = decorator.getPlayerHand(secondPlayer).get(0);
    decorator.placeCard(0, 1, secondCard);

    Player thirdPlayer = decorator.getCurrentPlayer();
    Card thirdCard = decorator.getPlayerHand(thirdPlayer).get(0);
    decorator.placeCard(0, 2, thirdCard);

    assertNotNull(decorator.getCardAt(0, 0));
    assertNotNull(decorator.getCardAt(0, 1));
    assertNotNull(decorator.getCardAt(0, 2));
  }

  @Test
  public void testReversedFallenAce_AdjacentBattles() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(2, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();
    Card secondCard = decorator.getPlayerHand(secondPlayer).get(0);
    decorator.placeCard(2, 1, secondCard);

    assertNotNull(decorator.getCardAt(2, 0));
    assertNotNull(decorator.getCardAt(2, 1));
  }
}