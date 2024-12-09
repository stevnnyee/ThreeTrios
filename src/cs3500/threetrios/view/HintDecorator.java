package cs3500.threetrios.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Font;
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
  private static final Font HINT_FONT = new Font("Arial", Font.BOLD, 14);

  public HintDecorator(GameBoardPanel panel, ReadOnlyThreeTriosModel model, String playerColor) {
    this.decoratedPanel = panel;
    this.model = model;
    this.showHints = false;
    setLayout(new BorderLayout());
    if (panel instanceof JComponent) {
      add((JComponent) panel, BorderLayout.CENTER);
    }
    setOpaque(false);

    // Set custom UI to ensure painting
    setUI(new javax.swing.plaf.PanelUI() {
      @Override
      public void paint(Graphics g, JComponent c) {
        paintHints(g);
      }
    });
  }

  private void paintHints(Graphics g) {
    System.out.println("Attempting to paint hints"); // Debug

    if (!showHints || selectedCard == null) {
      System.out.println("Hints disabled or no card selected"); // Debug
      return;
    }

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Dimension cellSize = getCellSize();
    int[] dims = model.getGridDimensions();

    g2d.setFont(HINT_FONT);

    for (int row = 0; row < dims[0]; row++) {
      for (int col = 0; col < dims[1]; col++) {
        if (!model.isHole(row, col) && model.getCardAt(row, col) == null) {
          int flips = model.getFlippableCards(row, col, selectedCard);
          System.out.println("Cell [" + row + "," + col + "] flips: " + flips); // Debug

          int x = col * cellSize.width;
          int y = row * cellSize.height;

          // Draw hint number
          g2d.setColor(Color.BLACK);
          String hint = String.valueOf(flips);
          int padding = 5;
          int textX = x + padding;
          int textY = y + cellSize.height - padding;
          g2d.drawString(hint, textX, textY);
        }
      }
    }
    g2d.dispose();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    System.out.println("PaintComponent called"); // Debug

    // Paint the base panel
    if (decoratedPanel instanceof JComponent) {
      ((JComponent) decoratedPanel).paint(g);
    }

    // Paint the hints
    paintHints(g);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    System.out.println("Paint called"); // Debug
    paintHints(g);
  }

  @Override
  public Dimension getCellSize() {
    return decoratedPanel.getCellSize();
  }

  @Override
  public void refresh() {
    System.out.println("Refresh called"); // Debug
    decoratedPanel.refresh();
    revalidate();
    repaint(50L); // Force repaint with delay
  }

  public void setShowHints(boolean show) {
    System.out.println("Setting showHints to: " + show); // Debug
    this.showHints = show;
    refresh();
  }

  public void setSelectedCard(Card card, Player player) {
    System.out.println("Setting selectedCard to: " + (card != null ? card.getName() : "null")); // Debug
    this.selectedCard = card;
    if (showHints) {
      refresh();
    }
  }
}