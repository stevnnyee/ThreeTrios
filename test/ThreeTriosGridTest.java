import org.junit.Before;
import org.junit.Test;
import java.util.List;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.ThreeTriosCard;
import cs3500.threetrios.model.ThreeTriosGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Class containing tests regarding the methods in the grid class.
 */
public class ThreeTriosGridTest {
  private ThreeTriosGrid grid;

  @Before
  public void setup() {
    boolean[][] standardHoles = new boolean[][]{
            {false, true, false},
            {true, false, true},
            {false, true, false}
    };
    grid = new ThreeTriosGrid(3, 3, standardHoles);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNegativeRows() {
    new ThreeTriosGrid(-1, 3, new boolean[1][1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorZeroColumns() {
    new ThreeTriosGrid(3, 0, new boolean[1][1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorMismatchedHolesDimensions() {
    new ThreeTriosGrid(3, 3, new boolean[2][2]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorEvenCardCells() {
    boolean[][] evenHoles = new boolean[][]{
            {false, true},
            {true, false}
    };
    new ThreeTriosGrid(2, 2, evenHoles);
  }

  @Test
  public void testGetRows() {
    assertEquals(3, grid.getRows());
  }

  @Test
  public void testGetCols() {
    assertEquals(3, grid.getCols());
  }

  @Test
  public void testIsHole() {
    assertTrue(grid.isHole(0, 1));
    assertFalse(grid.isHole(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsHoleOutOfBounds() {
    grid.isHole(3, 3);
  }

  @Test
  public void testIsEmpty() {
    assertFalse(grid.isEmpty(0, 0));
    assertFalse(grid.isEmpty(1, 1));
  }

  @Test
  public void testGetCardEmptyCell() {
    assertNull(grid.getCard(0, 0));
  }

  @Test
  public void testGetCardAfterPlacement() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 0, testCard);
    assertEquals(testCard, grid.getCard(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCardOutOfBounds() {
    grid.getCard(3, 3);
  }

  @Test
  public void testPlaceCardValidPosition() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 0, testCard);
    assertEquals(testCard, grid.getCard(0, 0));
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardInHole() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 1, testCard);
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardInOccupiedCell() {
    Card testCard1 = new ThreeTriosCard("TestCard1", 5, 6, 7, 8);
    Card testCard2 = new ThreeTriosCard("TestCard2", 1, 2, 3, 4);
    grid.placeCard(0, 0, testCard1);
    grid.placeCard(0, 0, testCard2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlaceCardOutOfBounds() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(3, 3, testCard);
  }

  @Test
  public void testGetCardCellCount() {
    assertEquals(5, grid.getCardCellCount());
  }

  @Test
  public void testIsFullEmptyGrid() {
    assertFalse(grid.isFull());
  }

  @Test
  public void testIsFullPartiallyFilledGrid() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 0, testCard);
    assertFalse(grid.isFull());
  }

  @Test
  public void testIsFullCompletedGrid() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 0, testCard);
    grid.placeCard(0, 2, testCard);
    grid.placeCard(1, 1, testCard);
    grid.placeCard(2, 0, testCard);
    grid.placeCard(2, 2, testCard);
    assertTrue(grid.isFull());
  }

  @Test
  public void testGetEmptyCellsInitialGrid() {
    List<int[]> emptyCells = grid.getEmptyCells();
    assertEquals(5, emptyCells.size());

    boolean foundCell00 = false;
    boolean foundCell02 = false;
    boolean foundCell11 = false;
    boolean foundCell20 = false;
    boolean foundCell22 = false;

    for (int[] cell : emptyCells) {
      if (cell[0] == 0 && cell[1] == 0) {
        foundCell00 = true;
      }
      if (cell[0] == 0 && cell[1] == 2) {
        foundCell02 = true;
      }
      if (cell[0] == 1 && cell[1] == 1) {
        foundCell11 = true;
      }
      if (cell[0] == 2 && cell[1] == 0) {
        foundCell20 = true;
      }
      if (cell[0] == 2 && cell[1] == 2) {
        foundCell22 = true;
      }
    }

    assertTrue(foundCell00 && foundCell02 && foundCell11 && foundCell20 && foundCell22);
  }

  @Test
  public void testGetEmptyCellsAfterPlacement() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);
    grid.placeCard(0, 0, testCard);

    List<int[]> emptyCells = grid.getEmptyCells();
    assertEquals(4, emptyCells.size());

    for (int[] cell : emptyCells) {
      assertFalse(cell[0] == 0 && cell[1] == 0);
    }
  }

  @Test
  public void testGetEmptyCellsFullGrid() {
    Card testCard = new ThreeTriosCard("TestCard", 5, 6, 7, 8);

    grid.placeCard(0, 0, testCard);
    grid.placeCard(0, 2, testCard);
    grid.placeCard(1, 1, testCard);
    grid.placeCard(2, 0, testCard);
    grid.placeCard(2, 2, testCard);

    List<int[]> emptyCells = grid.getEmptyCells();
    assertTrue(emptyCells.isEmpty());
  }

  @Test
  public void testDifferentGridSizes() {
    boolean[][] customHoles = new boolean[][]{
            {false, true, false, true},
            {true, false, true, false},
            {false, true, false, false}
    };
    ThreeTriosGrid customGrid = new ThreeTriosGrid(3, 4, customHoles);
    assertEquals(3, customGrid.getRows());
    assertEquals(4, customGrid.getCols());
    assertEquals(7, customGrid.getCardCellCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidHoleConfiguration() {
    boolean[][] invalidHoles = new boolean[][]{
            {true, true, true},
            {true, true, true},
            {true, true, true}
    };
    new ThreeTriosGrid(3, 3, invalidHoles);
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceCardInHoleWithNull() {
    grid.placeCard(0, 1, null);
  }

  @Test(expected = IllegalStateException.class)
  public void testPlaceNullCardOutOfBounds() {
    grid.placeCard(3, 3, null);
  }
}