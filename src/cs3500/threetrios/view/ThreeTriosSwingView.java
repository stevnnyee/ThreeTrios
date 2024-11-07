package cs3500.threetrios.view;

import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReadOnlyThreeTriosModel;
import cs3500.threetrios.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;

public class ThreeTriosSwingView extends JFrame implements ThreeTriosFrame {
  private final ReadOnlyThreeTriosModel model;
  private final GameBoardPanelImpl boardPanel;
  private final PlayerHandPanelImpl leftHandPanel;
  private final PlayerHandPanelImpl rightHandPanel;
  private final JLabel currentPlayerLabel;
  private static final Color HOLE_COLOR = Color.YELLOW;
  private static final Color EMPTY_CELL_COLOR = Color.LIGHT_GRAY;

  public ThreeTriosSwingView(ReadOnlyThreeTriosModel model) {
    this.model = model;
    this.setTitle("Three Trios Game");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());

    // Initialize the current player label
    this.currentPlayerLabel = new JLabel("Current Player: ", SwingConstants.CENTER);
    this.currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
    this.currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

    // Create panels
    this.boardPanel = new GameBoardPanelImpl(model);
    this.leftHandPanel = new PlayerHandPanelImpl(model, "RED");
    this.rightHandPanel = new PlayerHandPanelImpl(model, "BLUE");

    // Add panels to frame
    this.add(this.currentPlayerLabel, BorderLayout.NORTH);
    this.add(this.leftHandPanel, BorderLayout.WEST);
    this.add(this.boardPanel, BorderLayout.CENTER);
    this.add(this.rightHandPanel, BorderLayout.EAST);

    // Set minimum sizes
    this.setMinimumSize(new Dimension(800, 600));
  }

  @Override
  public void refresh() {
    Player currentPlayer = model.getCurrentPlayer();
    if (currentPlayer != null) {
      this.currentPlayerLabel.setText("Current Player: " + currentPlayer.getColor());
      this.currentPlayerLabel.setForeground(
              currentPlayer.getColor().equals("RED") ? Color.RED : Color.BLUE
      );
    }

    this.boardPanel.refresh();
    this.leftHandPanel.refresh();
    this.rightHandPanel.refresh();
    this.revalidate();
    this.repaint();
  }

  @Override
  public void display() {
    this.pack();
    this.setVisible(true);
  }

  private static class CardShape extends Path2D.Double {
    public CardShape(int x, int y, int width, int height) {
      moveTo(x, y);
      lineTo(x + width, y);
      lineTo(x + width, y + height);
      lineTo(x, y + height);
      closePath();
    }
  }

  private static class PlayerHandPanelImpl extends JPanel implements PlayerHandPanel {
    private final ReadOnlyThreeTriosModel model;
    private final String playerColor;
    private static final int CARD_SPACING = 10;
    private static final Color RED_BACKGROUND = new Color(255, 220, 220);
    private static final Color BLUE_BACKGROUND = new Color(220, 220, 255);
    private int selectedCardIndex = -1;  // Add tracking for selected card

    public PlayerHandPanelImpl(ReadOnlyThreeTriosModel model, String playerColor) {
      this.model = model;
      this.playerColor = playerColor;
      this.setBackground(playerColor.equals("RED") ? RED_BACKGROUND : BLUE_BACKGROUND);
      this.setPreferredSize(new Dimension(150, 500));

      // Add mouse listener for card selection
      this.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleCardClick(e.getPoint());
        }
      });
    }

    private void handleCardClick(Point p) {
      Player targetPlayer = getPlayerByColor(playerColor);
      if (targetPlayer == null) return;

      List<Card> hand = model.getPlayerHand(targetPlayer);
      if (hand == null) return;

      Dimension cardSize = getCardSize();
      int x = CARD_SPACING;
      int startY = CARD_SPACING;
      int totalHeight = (cardSize.height + CARD_SPACING) * hand.size();
      startY = Math.max(CARD_SPACING, (getHeight() - totalHeight) / 2);

      // Check which card was clicked
      for (int i = 0; i < hand.size(); i++) {
        Rectangle cardBounds = new Rectangle(x, startY, cardSize.width, cardSize.height);
        if (cardBounds.contains(p)) {
          if (selectedCardIndex == i) {
            selectedCardIndex = -1; // Deselect if clicking the same card
          } else {
            selectedCardIndex = i; // Select new card
          }
          System.out.println("Clicked card index: " + i + " in " + playerColor + " player's hand");
          repaint();
          break;
        }
        startY += cardSize.height + CARD_SPACING;
      }
    }

    @Override
    public Dimension getCardSize() {
      int width = getWidth() - (2 * CARD_SPACING);
      return new Dimension(width, width * 3 / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);

      Player targetPlayer = getPlayerByColor(playerColor);
      if (targetPlayer == null) return;

      List<Card> hand = model.getPlayerHand(targetPlayer);
      if (hand == null) return;

      Dimension cardSize = getCardSize();
      int x = CARD_SPACING;
      int startY = CARD_SPACING;
      int totalHeight = (cardSize.height + CARD_SPACING) * hand.size();
      startY = Math.max(CARD_SPACING, (getHeight() - totalHeight) / 2);

      // Draw all cards
      for (int i = 0; i < hand.size(); i++) {
        Card card = hand.get(i);
        drawCard(g2d, card, x, startY, cardSize.width, cardSize.height);

        // Draw selection highlight if this is the selected card
        if (i == selectedCardIndex) {
          g2d.setColor(Color.GRAY);
          g2d.setStroke(new BasicStroke(4));
          g2d.draw(new Rectangle(x - 2, startY - 2,
                  cardSize.width + 4, cardSize.height + 4));
        }
        startY += cardSize.height + CARD_SPACING;
      }
    }

    private Player getPlayerByColor(String color) {
      Player current = model.getCurrentPlayer();
      if (current != null && current.getColor().equals(color)) {
        return current;
      }
      int[] dims = model.getGridDimensions();
      for (int i = 0; i < dims[0]; i++) {
        for (int j = 0; j < dims[1]; j++) {
          Card card = model.getCardAt(i, j);
          if (card != null && card.getOwner().getColor().equals(color)) {
            return card.getOwner();
          }
        }
      }
      return null;
    }

    private void drawCard(Graphics2D g2d, Card card, int x, int y, int width, int height) {
      // Draw card background
      g2d.setColor(Color.WHITE);
      CardShape cardShape = new CardShape(x, y, width, height);
      g2d.fill(cardShape);

      // Draw card border
      g2d.setColor(Color.BLACK);
      g2d.setStroke(new BasicStroke(2));
      g2d.draw(cardShape);

      // Draw card name and values
      drawCardContents(g2d, card, x, y, width, height);
    }

    private void drawCardContents(Graphics2D g2d, Card card, int x, int y, int width, int height) {
      // Draw card name
      int fontSize = Math.min(width, height) / 6;
      Font font = new Font("Arial", Font.BOLD, fontSize);
      g2d.setFont(font);
      FontMetrics metrics = g2d.getFontMetrics(font);

      String name = card.getName();
      int nameX = x + (width - metrics.stringWidth(name)) / 2;
      int nameY = y + (height / 2);
      g2d.drawString(name, nameX, nameY);

      // Draw attack values
      fontSize = Math.min(width, height) / 8;
      font = new Font("Arial", Font.BOLD, fontSize);
      g2d.setFont(font);
      metrics = g2d.getFontMetrics(font);

      String north = formatValue(card.getAttackPower(Direction.NORTH));
      String south = formatValue(card.getAttackPower(Direction.SOUTH));
      String east = formatValue(card.getAttackPower(Direction.EAST));
      String west = formatValue(card.getAttackPower(Direction.WEST));

      int centerX = x + width / 2;
      int centerY = y + height / 2;
      int margin = fontSize;

      g2d.drawString(north, centerX - metrics.stringWidth(north) / 2, y + margin);
      g2d.drawString(south, centerX - metrics.stringWidth(south) / 2, y + height - margin);
      g2d.drawString(east, x + width - margin, centerY + metrics.getAscent() / 2);
      g2d.drawString(west, x + margin, centerY + metrics.getAscent() / 2);
    }

    private String formatValue(int value) {
      return value == 10 ? "A" : String.valueOf(value);
    }

    @Override
    public void refresh() {
      revalidate();
      repaint();
    }
  }

  private static class GameBoardPanelImpl extends JPanel implements GameBoardPanel {
    private final ReadOnlyThreeTriosModel model;

    public GameBoardPanelImpl(ReadOnlyThreeTriosModel model) {
      this.model = model;
      this.setBackground(Color.WHITE);

      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleGridClick(e.getPoint());
        }
      });
    }

    private void handleGridClick(Point p) {
      int[] dims = model.getGridDimensions();
      Dimension cellSize = getCellSize();

      int col = p.x / cellSize.width;
      int row = p.y / cellSize.height;

      if (row >= 0 && row < dims[0] && col >= 0 && col < dims[1]) {
        System.out.println("Clicked grid cell: row=" + row + ", col=" + col);
      }
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(500, 500);
    }

    @Override
    public Dimension getCellSize() {
      int[] dims = model.getGridDimensions();
      int width = getWidth() / dims[1];
      int height = getHeight() / dims[0];
      return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);

      int[] dims = model.getGridDimensions();
      Dimension cellSize = getCellSize();

      for (int row = 0; row < dims[0]; row++) {
        for (int col = 0; col < dims[1]; col++) {
          int x = col * cellSize.width;
          int y = row * cellSize.height;

          if (model.getCardAt(row, col) == null) {
            g2d.setColor(model.isHole(row, col) ? HOLE_COLOR : EMPTY_CELL_COLOR);
            g2d.fill(new CardShape(x, y, cellSize.width, cellSize.height));
          } else {
            Card card = model.getCardAt(row, col);
            drawCard(g2d, card, x, y, cellSize.width, cellSize.height);
          }

          // Draw grid lines
          g2d.setColor(Color.BLACK);
          g2d.setStroke(new BasicStroke(1));
          g2d.drawRect(x, y, cellSize.width, cellSize.height);
        }
      }
    }

    private void drawCard(Graphics2D g2d, Card card, int x, int y, int width, int height) {
      g2d.setColor(card.getOwner().getColor().equals("RED") ?
              new Color(255, 200, 200) : new Color(200, 200, 255));
      g2d.fill(new CardShape(x, y, width, height));

      g2d.setColor(Color.BLACK);
      Font font = new Font("Arial", Font.BOLD, height / 4);
      g2d.setFont(font);

      String north = formatValue(card.getAttackPower(Direction.NORTH));
      String south = formatValue(card.getAttackPower(Direction.SOUTH));
      String east = formatValue(card.getAttackPower(Direction.EAST));
      String west = formatValue(card.getAttackPower(Direction.WEST));

      FontMetrics metrics = g2d.getFontMetrics(font);
      int centerX = x + width / 2;
      int centerY = y + height / 2;

      g2d.drawString(north, centerX - metrics.stringWidth(north) / 2, y + metrics.getHeight());
      g2d.drawString(south, centerX - metrics.stringWidth(south) / 2, y + height - metrics.getDescent());
      g2d.drawString(east, x + width - metrics.stringWidth(east) - 5, centerY);
      g2d.drawString(west, x + 5, centerY);
    }

    private String formatValue(int value) {
      return value == 10 ? "A" : String.valueOf(value);
    }

    @Override
    public void refresh() {
      repaint();
    }
  }
}