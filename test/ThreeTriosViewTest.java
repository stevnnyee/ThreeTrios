
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.view.ThreeTriosView;
import cs3500.threetrios.view.ThreeTriosViewImpl;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class specifically for ThreeTrios view functionality.
 * Tests the textual representation of the game state.
 */
public class ThreeTriosViewTest {
  private Path tempDir;
  private ThreeTriosGameModel game;
  private ThreeTriosView view;

  @Before
  public void setup() throws IOException {
    tempDir = Files.createTempDirectory("threetrios-view-test");
    game = new ThreeTriosGameModel();

    createTestFile("test-board",
            "// 3x3 test board with 5 playable cells\n" +
                    "3 3\n" +
                    "_X_\n" +
                    "X_X\n" +
                    "_X_");

    createTestFile("test-cards",
            "// Cards with specific values for testing\n" +
                    "Knight A 5 6 7\n" +
                    "Dragon 9 8 7 6\n" +
                    "Wizard 5 A 7 8\n" +
                    "Archer 6 7 8 9\n" +
                    "Witch 7 6 5 4\n" +
                    "Pekka 8 7 6 5\n" +
                    "Giant 6 5 4 3\n" +
                    "Golem 9 9 8 7\n");
  }

  @After
  public void cleanup() throws IOException {
    deleteDirectory(tempDir.toFile());
  }

  private void createTestFile(String filename, String content) throws IOException {
    Path filePath = tempDir.resolve(filename);
    try (FileWriter writer = new FileWriter(filePath.toFile())) {
      writer.write(content);
    }
  }

  private void deleteDirectory(File directory) {
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    directory.delete();
  }

  @Test
  public void testInitialGameStateVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );
    view = new ThreeTriosViewImpl(game);

    String visualization = view.toString();
    assertTrue("Should show current player", visualization.startsWith("Player: RED"));
    assertTrue("Should contain hand section", visualization.contains("Hand:"));

    String[] lines = visualization.split("\n");

    assertTrue("Should have at least 5 lines before hand cards", lines.length >= 5);

    // Check first grid row
    String firstRow = lines[1].trim();
    assertEquals("First row should have correct format",
            "_   _", firstRow);
  }

  @Test
  public void testCardPlacementVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );
    view = new ThreeTriosViewImpl(game);

    String initialView = view.toString();

    Player redPlayer = game.getCurrentPlayer();
    Card cardToPlace = game.getPlayerHand(redPlayer).get(0);
    game.placeCard(redPlayer, 0, 0, cardToPlace);

    String afterPlacementView = view.toString();

    assertNotEquals("View should change after card placement",
            initialView, afterPlacementView);

    String[] lines = afterPlacementView.split("\n");
    assertTrue("First cell should show Red's card",
            lines[1].trim().startsWith("R"));
    assertTrue("Should now be Blue's turn",
            lines[0].contains("BLUE"));
  }


  @Test
  public void testHandVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );
    view = new ThreeTriosViewImpl(game);

    String visualization = view.toString();
    String[] sections = visualization.split("Hand:\n");
    assertEquals("Should have grid and hand sections", 2, sections.length);

    String handSection = sections[1];
    String[] handLines = handSection.trim().split("\n");

    assertEquals("Should have correct number of cards in hand", 3, handLines.length);

    for (String cardLine : handLines) {
      String[] cardParts = cardLine.trim().split(" ");
      assertEquals("Each card should have name and 4 attack values",
              5, cardParts.length);

      for (int i = 1; i < cardParts.length; i++) {
        assertTrue("Attack values should be 1-9 or A",
                cardParts[i].matches("[1-9A]"));
      }
    }
  }

  @Test
  public void testMultipleCardPlacementVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );
    view = new ThreeTriosViewImpl(game);
    Player redPlayer = game.getCurrentPlayer();
    Card redCard = game.getPlayerHand(redPlayer).get(0);
    game.placeCard(redPlayer, 0, 0, redCard);

    Player bluePlayer = game.getCurrentPlayer();
    Card blueCard = game.getPlayerHand(bluePlayer).get(0);
    game.placeCard(bluePlayer, 2, 0, blueCard);

    String visualization = view.toString();
    String[] lines = visualization.split("\n");

    assertTrue("Should show Red's card in top-left",
            lines[1].trim().startsWith("R"));
    assertTrue("Should show Blue's card in bottom-left",
            lines[3].trim().startsWith("B"));
  }

  @Test
  public void testVisualizationWithHoles() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );

    view = new ThreeTriosViewImpl(game);
    String visualization = view.toString();
    String[] lines = visualization.split("\n");
    String firstRow = lines[1].trim();
    String secondRow = lines[2];
    String thirdRow = lines[3].trim();

    assertEquals("First row should have correct format", "_   _", firstRow);
    assertEquals("Second row should have correct format", "  _  ", secondRow);
    assertEquals("Third row should have correct format", "_   _", thirdRow);
  }


  @Test
  public void testFullGameVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString()
    );
    view = new ThreeTriosViewImpl(game);

    while (!game.isGameOver()) {
      Player currentPlayer = game.getCurrentPlayer();
      Card card = game.getPlayerHand(currentPlayer).get(0);

      // Find first empty cell
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (!game.getGrid().isHole(i, j) &&
                  game.getGrid().getCard(i, j) == null) {
            try {
              game.placeCard(currentPlayer, i, j, card);
              break;
            } catch (IllegalArgumentException e) {
              continue;
            }
          }
        }
      }
    }

    String finalVisualization = view.toString();
    assertTrue("Final view should contain both R and B",
            finalVisualization.contains("R") && finalVisualization.contains("B"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewConstructorWithNullModel() {
    new ThreeTriosViewImpl(null);
  }
}