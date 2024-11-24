package cs3500.threetrios.view;

import cs3500.threetrios.features.ViewFeatures;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReadOnlyThreeTriosModel;
import cs3500.threetrios.model.Card;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A Swing-based graphical user interface for the Three Trios game.
 * This view implements a window with three main panels:
 * <ul>
 *  <li>Left panel: RED player's hand</li>
 *  <li>Center panel: Game board grid (5x7)</li>
 *  <li>Right panel: BLUE player's hand</li>
 * </ul>
 */
public class ThreeTriosSwingView extends JFrame implements ThreeTriosFrame {
  private final ReadOnlyThreeTriosModel model;
  private final GameBoardPanelImpl boardPanel;
  private final PlayerHandPanelImpl leftHandPanel;
  private final PlayerHandPanelImpl rightHandPanel;
  private static final Color HOLE_COLOR = Color.LIGHT_GRAY;
  private static final Color PLAYABLE_CELL_COLOR = new Color(238, 232, 170);
  private static final Color RED_PLAYER_COLOR = new Color(255, 182, 193);
  private static final Color BLUE_PLAYER_COLOR = new Color(173, 216, 230);
  private static final int GRID_ROWS = 5;
  private static final int GRID_COLS = 7;
  private static final int HAND_WIDTH = 150;
  private Card selectedCard = null;
  private Player selectedCardPlayer = null;
  private ViewFeatures features;
  private boolean gameOverMessageShown = false;

  /**
   * Constructs a new ThreeTrios game window.
   *
   * @param model the game model
   */
  public ThreeTriosSwingView(ReadOnlyThreeTriosModel model) {
    this.model = model;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(0, 0));

    Player currentPlayer = model.getCurrentPlayer();
    if (currentPlayer != null) {
      setTitle("Current player: " + currentPlayer.getColor());
    }

    leftHandPanel = new PlayerHandPanelImpl(model, "RED");
    rightHandPanel = new PlayerHandPanelImpl(model, "BLUE");
    boardPanel = new GameBoardPanelImpl(model);

    leftHandPanel.setPreferredSize(new Dimension(HAND_WIDTH, 400));
    rightHandPanel.setPreferredSize(new Dimension(HAND_WIDTH, 400));
    boardPanel.setPreferredSize(new Dimension(500, 400));

    add(leftHandPanel, BorderLayout.WEST);
    add(boardPanel, BorderLayout.CENTER);
    add(rightHandPanel, BorderLayout.EAST);

    pack();
  }

  /**
   * Implementation of PlayerHandPanel that displays and manages a player's cards in ThreeTrios.
   */
  private class PlayerHandPanelImpl extends JPanel implements PlayerHandPanel {
    private final ReadOnlyThreeTriosModel model;
    private final String playerColor;
    private int selectedCardIndex = -1;

    /**
     * Creates a new player hand panel for the specified player.
     *
     * @param model       the model
     * @param playerColor the player's color
     */
    public PlayerHandPanelImpl(ReadOnlyThreeTriosModel model, String playerColor) {
      this.model = model;
      this.playerColor = playerColor;
      setBackground(playerColor.equals("RED") ? RED_PLAYER_COLOR : BLUE_PLAYER_COLOR);

      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleCardClick(e.getPoint());
        }
      });
    }

    /**
     * Returns the current player.
     *
     * @return the Player we want to get
     */
    private Player getPlayer() {
      for (Player player : model.getPlayers()) {
        if (player.getColor().equals(playerColor)) {
          return player;
        }
      }
      return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      Player player = getPlayer();
      if (player == null) {
        return;
      }

      List<Card> hand = model.getPlayerHand(player);
      if (hand == null || hand.isEmpty()) {
        return;
      }

      int cardSpacing = getHeight() / hand.size();
      boolean isCurrentPlayer = model.getCurrentPlayer() != null &&
              player.getColor().equals(model.getCurrentPlayer().getColor());

      for (int i = 0; i < hand.size(); i++) {
        Card card = hand.get(i);
        int yPos = i * cardSpacing + 5;

        // Only highlight cards if it's this player's turn
        g2d.setColor(i == selectedCardIndex && isCurrentPlayer ? Color.YELLOW : Color.WHITE);
        g2d.fillRect(10, yPos, getWidth() - 20, cardSpacing - 10);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(10, yPos, getWidth() - 20, cardSpacing - 10);

        drawCardValues(g2d, card, 10, yPos, getWidth() - 20, cardSpacing - 10);
      }
    }


    /**
     * Renders the attack values for a card at the specified position.
     * The values are drawn in each cardinal direction (NORTH, SOUTH, EAST, WEST)
     *
     * @param g2d    the graphics to draw with
     * @param card   the card
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width of the card
     * @param height the height of the card
     */
    private void drawCardValues(Graphics2D g2d, Card card, int x, int y, int width, int height) {
      g2d.setFont(new Font("Arial", Font.PLAIN, height / 3));
      FontMetrics fm = g2d.getFontMetrics();

      String north = formatValue(card.getAttackPower(Direction.NORTH));
      String south = formatValue(card.getAttackPower(Direction.SOUTH));
      String east = formatValue(card.getAttackPower(Direction.EAST));
      String west = formatValue(card.getAttackPower(Direction.WEST));

      int centerX = x + width / 2;
      int centerY = y + height / 2;

      g2d.drawString(north, centerX - fm.stringWidth(north) / 2, y + fm.getHeight());
      g2d.drawString(south, centerX - fm.stringWidth(south) / 2, y + height - fm.getDescent());
      g2d.drawString(east, x + width - fm.stringWidth(east) - 5, centerY + fm.getAscent() / 2);
      g2d.drawString(west, x + 5, centerY + fm.getAscent() / 2);
    }

    /**
     * Formats a card's attack value for display.
     * Values of 10 are converted to 'A', while all other values are
     * converted to their string representation.
     *
     * @param value value
     * @return "A" if the value is 10, the number value otherwise as a string
     */
    private String formatValue(int value) {
      return value == 10 ? "A" : String.valueOf(value);
    }

    /**
     * Handles mouse clicks on the panel, detecting which card was clicked.
     *
     * @param p the point of where the mouse was clicked
     */
    private void handleCardClick(Point p) {
      Player player = getPlayer();
      if (player == null) {
        return;
      }

      List<Card> hand = model.getPlayerHand(player);
      if (hand == null || hand.isEmpty()) {
        return;
      }

      int cardSpacing = getHeight() / hand.size();
      int cardIndex = Math.min((p.y / cardSpacing), hand.size() - 1);

      System.out.println("Hand click at index: " + cardIndex);

      if (cardIndex >= 0 && cardIndex < hand.size()) {
        Card clickedCard = hand.get(cardIndex);
        if (features != null) {
          features.onCardSelected(player, clickedCard);
        }
      }
    }

    @Override
    public Dimension getCardSize() {
      Player player = getPlayer();
      if (player == null) {
        return new Dimension(getWidth() - 20, 50);
      }

      List<Card> hand = model.getPlayerHand(player);
      if (hand == null || hand.isEmpty()) {
        return new Dimension(getWidth() - 20, 50);
      }

      return new Dimension(getWidth() - 20, getHeight() / hand.size() - 10);
    }

    private int findCardIndex(Card card) {
      try {
        Player player = getPlayer();
        if (player == null || card == null) {
          return -1;
        }

        List<Card> hand = model.getPlayerHand(player);
        if (hand == null) {
          return -1;
        }

        for (int i = 0; i < hand.size(); i++) {
          Card handCard = hand.get(i);
          if (handCard != null && handCard.getName().equals(card.getName())) {
            return i;
          }
        }
      } catch (Exception e) {
        System.err.println("Error in findCardIndex: " + e.getMessage());
      }
      return -1;
    }

    @Override
    public void refresh() {
      repaint();
    }
  }

  /**
   * Implementation of the game board panel that displays the Three Trios playing grid.
   * The panel manages:
   * <ul>
   *  <li>A 5x7 grid of cells for card placement</li>
   *  <li>Visual representation of holes and playable cells</li>
   *  li>Display of placed cards with their attack values</li>
   *  <li>Mouse interaction for card placement</li>
   * </ul>
   */
  private class GameBoardPanelImpl extends JPanel implements GameBoardPanel {
    private final ReadOnlyThreeTriosModel model;

    /**
     * Creates a new game board panel linked to the specified model, initializing the model
     * with a white background and a click listener.
     *
     * @param model game model
     */
    public GameBoardPanelImpl(ReadOnlyThreeTriosModel model) {
      this.model = model;
      setBackground(Color.WHITE);

      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleGridClick(e.getPoint());
        }
      });
    }

    /**
     * Handles mouse clicks on the game board grid, attempting to place the currently selected card.
     *
     * @param p the point where the mouse is clicked
     */
    private void handleGridClick(Point p) {
      Dimension cellSize = getCellSize();
      int row = p.y / cellSize.height;
      int col = p.x / cellSize.width;

      // Print coordinates for debugging
      System.out.println("Grid click at: " + row + "," + col);

      if (row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLS) {
        if (features != null) {
          features.onCellSelected(row, col);
        }
      }
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      Dimension cellSize = getCellSize();

      for (int row = 0; row < GRID_ROWS; row++) {
        for (int col = 0; col < GRID_COLS; col++) {
          int x = col * cellSize.width;
          int y = row * cellSize.height;

          g2d.setColor(model.isHole(row, col) ? HOLE_COLOR : PLAYABLE_CELL_COLOR);
          g2d.fillRect(x, y, cellSize.width, cellSize.height);

          Card card = model.getCardAt(row, col);
          if (card != null) {
            drawGridCard(g2d, card, x, y, cellSize.width, cellSize.height);
          }

          g2d.setColor(Color.BLACK);
          g2d.drawRect(x, y, cellSize.width, cellSize.height);
        }
      }
    }

    /**
     * Renders a card on the game board grid with its attack values and player color.
     *
     * @param g2d    the graphics to draw with
     * @param card   the card
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width of the card
     * @param height the height of the card
     */
    private void drawGridCard(Graphics2D g2d, Card card, int x, int y, int width, int height) {
      g2d.setColor(card.getOwner().getColor().equals("RED") ? RED_PLAYER_COLOR : BLUE_PLAYER_COLOR);
      g2d.fillRect(x + 1, y + 1, width - 2, height - 2);

      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.PLAIN, height / 4));
      FontMetrics fm = g2d.getFontMetrics();

      String north = formatValue(card.getAttackPower(Direction.NORTH));
      String south = formatValue(card.getAttackPower(Direction.SOUTH));
      String east = formatValue(card.getAttackPower(Direction.EAST));
      String west = formatValue(card.getAttackPower(Direction.WEST));

      int centerX = x + width / 2;
      int centerY = y + height / 2;

      g2d.drawString(north, centerX - fm.stringWidth(north) / 2, y + fm.getHeight());
      g2d.drawString(south, centerX - fm.stringWidth(south) / 2, y + height - fm.getDescent());
      g2d.drawString(east, x + width - fm.stringWidth(east) - 5, centerY + fm.getAscent() / 2);
      g2d.drawString(west, x + 5, centerY + fm.getAscent() / 2);
    }

    /**
     * Formats a card's attack value for display.
     * Values of 10 are converted to 'A', while all other values are
     * converted to their string representation.
     *
     * @param value value
     * @return "A" if the value is 10, the number value otherwise as a string
     */
    private String formatValue(int value) {
      return value == 10 ? "A" : String.valueOf(value);
    }

    @Override
    public Dimension getCellSize() {
      return new Dimension(getWidth() / GRID_COLS, getHeight() / GRID_ROWS);
    }

    @Override
    public void refresh() {
      repaint();
    }
  }

  @Override
  public void refresh() {
    // Update window title
    Player currentPlayer = model.getCurrentPlayer();
    if (currentPlayer != null) {
      if (model.isGameOver() && model.getWinner() != null) {
        setTitle("Game Over - " + model.getWinner().getColor() + " WINS!");
      } else {
        setTitle("Current player: " + currentPlayer.getColor());
      }
    }

    // Only repaint the panels, don't call their refresh methods
    boardPanel.repaint();
    leftHandPanel.repaint();
    rightHandPanel.repaint();

    if (selectedCardPlayer != null && currentPlayer != null &&
            !selectedCardPlayer.getColor().equals(currentPlayer.getColor())) {
      selectedCard = null;
      selectedCardPlayer = null;
      leftHandPanel.selectedCardIndex = -1;
      rightHandPanel.selectedCardIndex = -1;
    }

    revalidate();
    repaint();

    // Handle game over state
    if (model.isGameOver() && !gameOverMessageShown) {  // Add a flag to prevent multiple messages
      gameOverMessageShown = true;  // Add this field to the class
      Player winner = model.getWinner();
      int redScore = model.getPlayerScore(model.getPlayers().get(0));
      int blueScore = model.getPlayerScore(model.getPlayers().get(1));

      String message;
      if (winner != null) {
        message = String.format("%s wins!\nFinal Score:\nRED: %d\nBLUE: %d",
                winner.getColor(), redScore, blueScore);
      } else {
        message = String.format("It's a tie!\nFinal Score:\nRED: %d\nBLUE: %d",
                redScore, blueScore);
      }

      JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  @Override
  public void display() {
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }


  public void addViewFeatures(ViewFeatures features) {
    this.features = features;
  }

  @Override
  public void setSelectedCard(Card card, Player player) {
    this.selectedCard = card;
    this.selectedCardPlayer = player;

    try {
      // Clear all selections first
      leftHandPanel.selectedCardIndex = -1;
      rightHandPanel.selectedCardIndex = -1;

      // Then set the appropriate selection
      if (player != null && card != null) {
        if (player.getColor().equals("RED")) {
          leftHandPanel.selectedCardIndex = leftHandPanel.findCardIndex(card);
        } else if (player.getColor().equals("BLUE")) {
          rightHandPanel.selectedCardIndex = rightHandPanel.findCardIndex(card);
        }
      }

      // Refresh the UI
      leftHandPanel.refresh();
      rightHandPanel.refresh();
      repaint();
    } catch (Exception e) {
      System.err.println("Error in setSelectedCard: " + e.getMessage());
    }
  }
}