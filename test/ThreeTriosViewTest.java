
import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ThreeTriosGameModel;
import cs3500.threetrios.view.ThreeTriosSwingView;
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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JFrame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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
            "// 3x3 test board with 5 playable cells\n"
                    + "3 3\n"
                    + "_X_\n"
                    + "X_X\n"
                    + "_X_");

    createTestFile("test-cards",
            "// Cards with specific values for testing\n"
                    + "Knight A 5 6 7\n"
                    + "Dragon 9 8 7 6\n"
                    + "Wizard 5 A 7 8\n"
                    + "Archer 6 7 8 9\n"
                    + "Witch 7 6 5 4\n"
                    + "Pekka 8 7 6 5\n"
                    + "Giant 6 5 4 3\n"
                    + "Golem 9 9 8 7\n");
  }

  @After
  public void cleanup() {
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
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);

    String visualization = view.toString();
    assertTrue(visualization.startsWith("Player: RED"));
    assertTrue(visualization.contains("Hand:"));

    String[] lines = visualization.split("\n");

    assertTrue(lines.length >= 5);

    String firstRow = lines[1].trim();
    assertEquals("_   _", firstRow);
  }

  @Test
  public void testCardPlacementVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);

    String initialView = view.toString();

    Player redPlayer = game.getCurrentPlayer();
    Card cardToPlace = game.getPlayerHand(redPlayer).get(0);
    game.placeCard(redPlayer, 0, 0, cardToPlace);

    String afterPlacementView = view.toString();

    assertNotEquals(initialView, afterPlacementView);

    String[] lines = afterPlacementView.split("\n");
    assertTrue(lines[1].trim().startsWith("R"));
    assertTrue(lines[0].contains("BLUE"));
  }


  @Test
  public void testHandVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);

    String visualization = view.toString();
    String[] sections = visualization.split("Hand:\n");
    assertEquals(2, sections.length);

    String handSection = sections[1];
    String[] handLines = handSection.trim().split("\n");

    assertEquals(3, handLines.length);

    for (String cardLine : handLines) {
      String[] cardParts = cardLine.trim().split(" ");
      assertEquals(5, cardParts.length);

      for (int i = 1; i < cardParts.length; i++) {
        assertTrue(cardParts[i].matches("[1-9A]"));
      }
    }
  }

  @Test
  public void testMultipleCardPlacementVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);
    Player redPlayer = game.getCurrentPlayer();
    Card redCard = game.getPlayerHand(redPlayer).get(0);
    game.placeCard(redPlayer, 0, 0, redCard);

    Player bluePlayer = game.getCurrentPlayer();
    Card blueCard = game.getPlayerHand(bluePlayer).get(0);
    game.placeCard(bluePlayer, 2, 0, blueCard);

    String visualization = view.toString();
    String[] lines = visualization.split("\n");

    assertTrue(lines[1].trim().startsWith("R"));
    assertTrue(lines[3].trim().startsWith("B"));
  }

  @Test
  public void testVisualizationWithHoles() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());

    view = new ThreeTriosViewImpl(game);
    String visualization = view.toString();
    String[] lines = visualization.split("\n");
    String firstRow = lines[1].trim();
    String secondRow = lines[2];
    String thirdRow = lines[3].trim();

    assertEquals("_   _", firstRow);
    assertEquals("  _  ", secondRow);
    assertEquals("_   _", thirdRow);
  }

  @Test
  public void testFullGameVisualization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);

    while (!game.isGameOver()) {
      Player currentPlayer = game.getCurrentPlayer();
      Card card = game.getPlayerHand(currentPlayer).get(0);

      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (!game.getGrid().isHole(i, j)
                  && game.getGrid().getCard(i, j) == null) {
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
    assertTrue(finalVisualization.contains("R") && finalVisualization.contains("B"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewConstructorWithNullModel() {
    new ThreeTriosViewImpl(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewWithInvalidBoardConfig() throws IOException {
    createTestFile("invalid-board",
            "// Invalid 3x3 board with wrong dimensions\n"
                    + "3 3\n"
                    + "_X_\n"
                    + "X_\n"
                    + "_X_");

    game.startGameFromConfig(
            tempDir.resolve("invalid-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewWithInvalidCardConfig() throws IOException {
    createTestFile("invalid-cards",
            "// Cards with invalid value counts\n"
                    + "Knight A 5 6\n"
                    + "Dragon 9 8 7 6\n"
                    + "Wizard 5 A 7 8\n");

    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("invalid-cards").toString());
    view = new ThreeTriosViewImpl(game);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewWithInvalidCardValues() throws IOException {
    createTestFile("invalid-card-values",
            "// Cards with invalid values\n"
                    + "Knight X 5 6 7\n"
                    + "Dragon 9 8 7 6\n"
                    + "Wizard 5 A 7 8\n");

    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("invalid-card-values").toString());
    view = new ThreeTriosViewImpl(game);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewWithEmptyConfigFiles() throws IOException {
    createTestFile("empty-board", "");
    createTestFile("empty-cards", "");

    game.startGameFromConfig(
            tempDir.resolve("empty-board").toString(),
            tempDir.resolve("empty-cards").toString());
    view = new ThreeTriosViewImpl(game);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testViewWithMalformedBoardDimensions() throws IOException {
    createTestFile("malformed-board",
            "// Malformed board dimensions\n"
                    + "3x3\n"
                    + "_X_\n"
                    + "X_X\n"
                    + "_X_");

    game.startGameFromConfig(
            tempDir.resolve("malformed-board").toString(),
            tempDir.resolve("test-cards").toString());
    view = new ThreeTriosViewImpl(game);
  }

  @Test
  public void testSwingViewInitialization() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosSwingView swingView = new ThreeTriosSwingView(game);

    assertNotNull(swingView);
    assertEquals(JFrame.EXIT_ON_CLOSE, swingView.getDefaultCloseOperation());
    assertEquals(BorderLayout.class, swingView.getLayout().getClass());
  }

  @Test
  public void testSwingViewComponents() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosSwingView swingView = new ThreeTriosSwingView(game);

    Component[] components = swingView.getContentPane().getComponents();
    assertEquals(3, components.length);
    assertTrue(components[0] instanceof JPanel);
    assertTrue(components[1] instanceof JPanel);
    assertTrue(components[2] instanceof JPanel);
  }

  @Test
  public void testSwingViewRefresh() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosSwingView swingView = new ThreeTriosSwingView(game);

    String initialTitle = swingView.getTitle();
    game.placeCard(game.getCurrentPlayer(), 0, 0,
            game.getPlayerHand(game.getCurrentPlayer()).get(0));
    swingView.refresh();

    assertNotEquals(initialTitle, swingView.getTitle());
  }

  @Test
  public void testViewImplCardFormatting() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosViewImpl textView = new ThreeTriosViewImpl(game);

    String viewString = textView.toString();
    assertTrue(viewString.contains("Hand:"));

    String handSection = viewString.substring(viewString.indexOf("Hand:"));
    String[] cardLines = handSection.split("\n");

    for (int i = 1; i < cardLines.length; i++) {
      String cardLine = cardLines[i];
      assertTrue(cardLine.matches(".*\\s[1-9A]\\s[1-9A]\\s[1-9A]\\s[1-9A]\\s*"));
    }
  }

  @Test
  public void testViewImplGridRepresentation() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosViewImpl textView = new ThreeTriosViewImpl(game);

    String viewString = textView.toString();
    String[] lines = viewString.split("\n");
    assertTrue(lines.length >= 5);
    assertTrue(lines[1].contains("_") || lines[1].contains(" "));
  }

  @Test
  public void testViewImplHandRepresentation() throws IOException {
    game.startGameFromConfig(
            tempDir.resolve("test-board").toString(),
            tempDir.resolve("test-cards").toString());
    ThreeTriosViewImpl textView = new ThreeTriosViewImpl(game);

    String viewString = textView.toString();
    assertTrue(viewString.contains("Hand:"));
    String handSection = viewString.split("Hand:\n")[1];
    String[] cardLines = handSection.trim().split("\n");
    assertEquals(3, cardLines.length); // Assuming 3 cards in hand
  }

}