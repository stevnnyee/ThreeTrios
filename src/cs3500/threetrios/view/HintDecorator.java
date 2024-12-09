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
import cs3500.threetrios.model.Direction;

import javax.swing.JPanel;
import javax.swing.JComponent;

/**
 * Public class for hint decorators.
 * Displays and calculates the correct number of cards that would be flipped by the selected card.
 */
public class HintDecorator extends JPanel implements GameBoardPanel {
  private final GameBoardPanel decoratedPanel;
  private final ReadOnlyThreeTriosModel model;
  private boolean showHints;
  private Card selectedCard;
  private static final Font HINT_FONT = new Font("Arial", Font.BOLD, 14);

  /**
   * Constructor for hintdirector.
   * @param panel game panel.
   * @param model game model.
   * @param playerColor team color.
   */
  public HintDecorator(GameBoardPanel panel, ReadOnlyThreeTriosModel model, String playerColor) {
    this.decoratedPanel = panel;
    this.model = model;
    this.showHints = false;
    setLayout(new BorderLayout());
    if (panel instanceof JComponent) {
      add((JComponent) panel, BorderLayout.CENTER);
    }
    setOpaque(false);

    setUI(new javax.swing.plaf.PanelUI() {
      @Override
      public void paint(Graphics g, JComponent c) {
        paintHints(g);
      }
    });
  }

  private void paintHints(Graphics g) {
    if (!showHints || selectedCard == null) {
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
          int flips = calculateTotalFlips(row, col);
          int x = col * cellSize.width;
          int y = row * cellSize.height;
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

  private int calculateTotalFlips(int row, int col) {
    int flips = model.getFlippableCards(row, col, selectedCard);
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int adjRow = row + dir[0];
      int adjCol = col + dir[1];

      if (isValidPosition(adjRow, adjCol)) {
        Card adjCard = model.getCardAt(adjRow, adjCol);
        if (adjCard != null && adjCard.getOwner() != selectedCard.getOwner()) {
          Direction battleDir = getBattleDirection(row, col, adjRow, adjCol);
          int attackValue = selectedCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());
          if (attackValue > defenseValue) {
            for (int[] secDir : directions) {
              int secRow = adjRow + secDir[0];
              int secCol = adjCol + secDir[1];

              if (isValidPosition(secRow, secCol)) {
                Card secCard = model.getCardAt(secRow, secCol);
                if (secCard != null && secCard.getOwner() != selectedCard.getOwner()) {
                  Direction secBattleDir = getBattleDirection(adjRow, adjCol, secRow, secCol);
                  int secAttackValue = adjCard.getAttackPower(secBattleDir);
                  int secDefenseValue = secCard.getAttackPower(secBattleDir.getOpposite());
                  if (secAttackValue > secDefenseValue) {
                    flips++;
                  }
                }
              }
            }
          }
        }
      }
    }
    return flips;
  }

  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < model.getGridDimensions()[0]
            && col >= 0 && col < model.getGridDimensions()[1]
            && !model.isHole(row, col);
  }

  private Direction getBattleDirection(int fromRow, int fromCol, int toRow, int toCol) {
    if (fromRow < toRow) return Direction.SOUTH;
    if (fromRow > toRow) return Direction.NORTH;
    if (fromCol < toCol) return Direction.EAST;
    return Direction.WEST;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (decoratedPanel instanceof JComponent) {
      ((JComponent) decoratedPanel).paint(g);
    }
    paintHints(g);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    paintHints(g);
  }

  @Override
  public Dimension getCellSize() {
    return decoratedPanel.getCellSize();
  }

  @Override
  public void refresh() {
    decoratedPanel.refresh();
    revalidate();
    repaint(50L);
  }

  public void setShowHints(boolean show) {
    this.showHints = show;
    refresh();
  }

  public void setSelectedCard(Card card, Player player) {
    this.selectedCard = card;
    if (showHints) {
      refresh();
    }
  }
}