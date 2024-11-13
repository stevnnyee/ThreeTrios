package cs3500.threetrios.view;

import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReadOnlyThreeTriosModel;
import cs3500.threetrios.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

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



  private class PlayerHandPanelImpl extends JPanel implements PlayerHandPanel {
    private final ReadOnlyThreeTriosModel model;
    private final String playerColor;
    private int selectedCardIndex = -1;

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

    private Player getPlayer() {
      List<Player> players = model.getPlayers();
      if (players != null) {
        for (Player player : players) {
          if (player.getColor().equals(playerColor)) {
            return player;
          }
        }
      }
      Player currentPlayer = model.getCurrentPlayer();
      if (currentPlayer != null && currentPlayer.getColor().equals(playerColor)) {
        return currentPlayer;
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

      for (int i = 0; i < hand.size(); i++) {
        Card card = hand.get(i);
        int yPos = i * cardSpacing + 5;

        g2d.setColor(i == selectedCardIndex ? Color.YELLOW : Color.WHITE);
        g2d.fillRect(10, yPos, getWidth() - 20, cardSpacing - 10);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(10, yPos, getWidth() - 20, cardSpacing - 10);

        drawCardValues(g2d, card, 10, yPos, getWidth() - 20, cardSpacing - 10);
      }
    }

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

    private String formatValue(int value) {
      return value == 10 ? "A" : String.valueOf(value);
    }

    private void handleCardClick(Point p) {
      Player player = getPlayer();
      if (player == null || player != model.getCurrentPlayer()) return;

      List<Card> hand = model.getPlayerHand(player);
      if (hand == null || hand.isEmpty()) return;

      int cardSpacing = getHeight() / hand.size();

      for (int i = 0; i < hand.size(); i++) {
        Rectangle cardBounds = new Rectangle(10,
                i * cardSpacing + 5,
                getWidth() - 20,
                cardSpacing - 10);

        if (cardBounds.contains(p)) {
          selectedCardIndex = (selectedCardIndex == i) ? -1 : i;
          if (selectedCardIndex != -1) {
            selectedCard = hand.get(i);
            selectedCardPlayer = player;
          } else {
            selectedCard = null;
            selectedCardPlayer = null;
          }
          refresh();
          break;
        }
      }
    }

    @Override
    public Dimension getCardSize() {
      Player player = getPlayer();
      if (player == null) return new Dimension(getWidth() - 20, 50);

      List<Card> hand = model.getPlayerHand(player);
      if (hand == null || hand.isEmpty()) return new Dimension(getWidth() - 20, 50);

      return new Dimension(getWidth() - 20, getHeight() / hand.size() - 10);
    }

    @Override
    public void refresh() {
      repaint();
    }
  }

  private class GameBoardPanelImpl extends JPanel implements GameBoardPanel {
    private final ReadOnlyThreeTriosModel model;

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

    private void handleGridClick(Point p) {
      if (selectedCard == null) return;

      Dimension cellSize = getCellSize();
      int row = p.y / cellSize.height;
      int col = p.x / cellSize.width;

      if (row >= 0 && row < model.getGridDimensions()[0] &&
              col >= 0 && col < model.getGridDimensions()[1]) {

        try {
          if (!model.isHole(row, col) && model.canPlaceCard(row, col, selectedCard)) {
            model.placeCard(row, col, selectedCard);

            selectedCard = null;
            selectedCardPlayer = null;
            leftHandPanel.selectedCardIndex = -1;
            rightHandPanel.selectedCardIndex = -1;

            ThreeTriosSwingView.this.refresh();
          }
        } catch (IllegalArgumentException | IllegalStateException e) {
          throw new IllegalArgumentException("Illegal move");
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
    Player currentPlayer = model.getCurrentPlayer();
    if (currentPlayer != null) {
      if (model.isGameOver() && model.getWinner() != null) {
        setTitle("Game Over - " + model.getWinner().getColor() + " WINS!");
      } else {
        setTitle("Current player: " + currentPlayer.getColor());
      }
    }

    boardPanel.refresh();
    leftHandPanel.refresh();
    rightHandPanel.refresh();

    if (selectedCardPlayer != null && selectedCardPlayer != model.getCurrentPlayer()) {
      selectedCard = null;
      selectedCardPlayer = null;
    }

    revalidate();
    repaint();

    if (model.isGameOver()) {
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

      JOptionPane.showMessageDialog(this,
              message,
              "Game Over",
              JOptionPane.INFORMATION_MESSAGE);

      boardPanel.setEnabled(false);
      leftHandPanel.setEnabled(false);
      rightHandPanel.setEnabled(false);
    }
  }

  @Override
  public void display() {
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }
}