package cs3500.threetrios.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.RenderingHints;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReadOnlyThreeTriosModel;

import javax.swing.JPanel;
import javax.swing.JComponent;

public class HintDecorator extends JPanel implements GameBoardPanel {
  private final GameBoardPanel decoratedPanel;
  private final ReadOnlyThreeTriosModel model;
  private boolean showHints;
  private Card selectedCard;
  private static final Color HINT_OVERLAY = new Color(255, 255, 0, 128);
  private static final Font HINT_FONT = new Font("Arial", Font.BOLD, 24);

  public HintDecorator(GameBoardPanel panel, ReadOnlyThreeTriosModel model, String playerColor) {
    this.decoratedPanel = panel;
    this.model = model;
    this.showHints = false;
    setLayout(new BorderLayout());
    if (panel instanceof JComponent) {
      add((JComponent) panel, BorderLayout.CENTER);
    }
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (decoratedPanel instanceof JComponent) {
      ((JComponent) decoratedPanel).paint(g);
    }

    if (showHints && selectedCard != null) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      Dimension cellSize = getCellSize();
      int[] dims = model.getGridDimensions();

      // Set font size based on cell size
      int fontSize = Math.min(cellSize.height / 3, cellSize.width / 3);
      g2d.setFont(HINT_FONT.deriveFont((float)fontSize));

      // Draw hints for each cell
      for (int row = 0; row < dims[0]; row++) {
        for (int col = 0; col < dims[1]; col++) {
          // Check if cell is empty and not a hole
          if (!model.isHole(row, col) && model.getCardAt(row, col) == null) {
            int flips = model.getFlippableCards(row, col, selectedCard);
            int x = col * cellSize.width;
            int y = row * cellSize.height;

            // Only show hint if card can be placed here
            if (model.canPlaceCard(row, col, selectedCard)) {
              // Draw yellow highlight
              g2d.setColor(HINT_OVERLAY);
              g2d.fillRect(x, y, cellSize.width, cellSize.height);

              // Draw number of flips
              g2d.setColor(Color.BLACK);
              String hint = String.valueOf(flips);
              FontMetrics fm = g2d.getFontMetrics();
              int textX = x + (cellSize.width - fm.stringWidth(hint)) / 2;
              int textY = y + (cellSize.height + fm.getAscent()) / 2;
              g2d.drawString(hint, textX, textY);
            }
          }
        }
      }
    }
  }

  @Override
  public Dimension getCellSize() {
    return decoratedPanel.getCellSize();
  }

  @Override
  public void refresh() {
    decoratedPanel.refresh();
    repaint();
  }

  public void setShowHints(boolean show) {
    this.showHints = show;
    repaint();
  }

  public void setSelectedCard(Card card, Player player) {
    this.selectedCard = card;
    repaint();
  }
}