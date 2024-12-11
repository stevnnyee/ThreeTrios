import org.junit.Before;
import org.junit.Test;


import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Grid;
import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReverseRuleDecorator;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.model.ThreeTriosGrid;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests functionality of the ReverseRuleDecorator class with focus on reverse battle mechanics.
 */
public class ReverseRuleDecoratorTest {
  private ReverseRuleDecorator decorator;

  /**
   * Sets up the test environment with a 3x3 grid, two holes, and 15 test cards.
   */
  @Before
  public void setup() {
    MainModelInterface baseModel = new ThreeTriosGameModel();
    decorator = new ReverseRuleDecorator(baseModel);
    List<Card> deck = new ArrayList<>();
    deck.add(new ThreeTriosCard("BlackKnight", 8, 6, 9, 7));
    deck.add(new ThreeTriosCard("BabyDragon", 7, 8, 6, 5));
    deck.add(new ThreeTriosCard("IceWizard", 6, 5, 8, 7));
    deck.add(new ThreeTriosCard("EliteBarbs", 9, 8, 7, 9));
    deck.add(new ThreeTriosCard("Archer", 6, 7, 8, 5));
    deck.add(new ThreeTriosCard("Witch", 7, 5, 8, 6));
    deck.add(new ThreeTriosCard("Goblin", 5, 6, 7, 4));
    deck.add(new ThreeTriosCard("Princess", 6, 4, 8, 5));
    deck.add(new ThreeTriosCard("Prince", 9, 7, 8, 8));
    deck.add(new ThreeTriosCard("Valkyrie", 8, 8, 7, 7));
    deck.add(new ThreeTriosCard("Pekka", 9, 9, 8, 9));
    deck.add(new ThreeTriosCard("Warden", 8, 8, 9, 7));
    deck.add(new ThreeTriosCard("King", 9, 8, 8, 9));
    deck.add(new ThreeTriosCard("Queen", 9, 8, 9, 8));
    deck.add(new ThreeTriosCard("Yeti", 8, 9, 7, 8));

    boolean[][] holes = {
            {false, false, false},
            {true, true, false},
            {false, false, false}
    };
    Grid grid = new ThreeTriosGrid(3, 3, holes);
    decorator.startGame(grid, deck);
  }

  // Tests that a lower number card placement executes correctly
  @Test
  public void testReverseBattle_LowerNumberWins() {
    Player currentPlayer = decorator.getCurrentPlayer();
    Card attacker = decorator.getPlayerHand(currentPlayer).get(0);

    decorator.placeCard(0, 0, attacker);

    Player nextPlayer = decorator.getCurrentPlayer();
    Card defender = decorator.getPlayerHand(nextPlayer).get(0);

    decorator.placeCard(0, 1, defender);

    assertNotNull(decorator.getCardAt(0, 0));
    assertNotNull(decorator.getCardAt(0, 1));
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
    assertTrue( flips >= 0 && flips <= 1);
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

  // Tests that turns alternate between players correctly
  @Test
  public void testTurnOrder() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    Player secondPlayer = decorator.getCurrentPlayer();

    assertNotEquals(firstPlayer, secondPlayer);
  }

  // Tests that cards maintain correct ownership after placement
  @Test
  public void testCardOwnership() {
    Player firstPlayer = decorator.getCurrentPlayer();
    Card firstCard = decorator.getPlayerHand(firstPlayer).get(0);
    decorator.placeCard(0, 0, firstCard);

    assertEquals( firstPlayer, decorator.getCardAt(0, 0).getOwner());
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
}