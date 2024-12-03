package cs3500.threetrios.provider.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;

import javax.swing.JPanel;

import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.Card;
import cs3500.threetrios.provider.model.CardCell;
import cs3500.threetrios.provider.model.Cell;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.model.ReadOnlyTT;

/**
 * Represents a panel for ThreeTriosModel game.
 * Again, just for preparation and testing for the next part!
 */
public class JTriosPanel extends JPanel {

  private final ReadOnlyTT model;
  private cs3500.threetrios.provider.controller.TriosController features;
  private int selectedCardIndex = -1;
  private PlayerColor selectedCardColor = PlayerColor.RED;

  /**
   * Constructs a JTriosPanel.
   *
   * @param model the model to use
   * @throws IllegalArgumentException if the model is null
   */
  public JTriosPanel(ReadOnlyTT model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }

    this.model = model;
    this.addMouseListener(new TriosClickListener());
  }

  /**
   * This method tells Swing what the "natural" size should be
   * for this panel.  Here, we set it to 600x600 pixels.
   *
   * @return Our preferred *physical* size.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(600, 600);
  }

  /**
   * Adds a click listener to the panel.
   *
   * @param features the features
   */
  public void addClickListener(TriosController features) {
    this.features = features;
  }
  /**
   * Selects a card.
   *
   * @param row the row of the card to select (0-indexed)
   * @param col the column of the card to select (0-indexed)
   * @return true if the card was selected, false otherwise
   */
  public boolean selectCard(int row, int col) {

    PlayerColor colorChosen = (col == 0) ? PlayerColor.RED : PlayerColor.BLUE;

    if (this.selectedCardIndex == row && this.selectedCardColor == colorChosen) {
      this.selectedCardIndex = -1;
      this.repaint();
      return false;
    }
    this.selectedCardIndex = row;
    this.selectedCardColor = colorChosen;
    this.repaint();
    return true;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    int rows = model.numRows();
    int cols = model.numCols() + 2;
    int cellWidth = getWidth() / cols;
    int cellHeight = getHeight() / rows;
    List<Card> playerRedHand = model.getHand(PlayerColor.RED);
    List<Card> playerBlueHand = model.getHand(PlayerColor.BLUE);

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        Cell currentCell;
        boolean isHighlighted = false;

        List<Card> currentHand = (col == 0) ? playerRedHand : playerBlueHand;
        PlayerColor currentColor = (col == 0) ? PlayerColor.RED : PlayerColor.BLUE;
        if (col == 0 || col == cols - 1) {
          if (row > currentHand.size() - 1) {
            currentCell = null;
          } else {
            Card currentCard = currentHand.get(row);
            CardCell temp = new CardCell();
            temp.updateCard(currentCard, true);
            currentCell = temp;

            if (row == selectedCardIndex && currentColor == selectedCardColor) {
              isHighlighted = true;
            }
          }
        } else {
          currentCell = model.getCell(row, col - 1);
          if (selectedCardIndex != -1 && !currentCell.isHole() && currentCell.isEmpty()) {
            isHighlighted = true;
          }
        }
        Color cellColor = handleHighlight(currentCell, isHighlighted, col, cols);
        int x = col * cellWidth;
        int y = row * cellHeight;
        g2d.setColor(cellColor);
        g2d.fillRect(x, y, cellWidth, cellHeight);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, cellWidth, cellHeight);
        if (currentCell == null || currentCell.isHole() || currentCell.isEmpty()) {
          continue;
        }
        paintCard((CardCell) currentCell, g2d, x, cellWidth, y, cellHeight);
      }
    }
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(5));
    g2d.drawLine(cellWidth, 0, cellWidth, getHeight());
    g2d.drawLine(cellWidth * (cols - 1), 0, cellWidth * (cols - 1), getHeight());
    g2d.dispose();
  }

  private Color handleHighlight(Cell currentCell, boolean isHighlighted, int col, int cols) {
    Color cellColor;
    if (currentCell == null) {
      cellColor = Color.DARK_GRAY;
    } else {
      cellColor = currentCell.getColor();
      if (isHighlighted) {
        if (col == 0 || col == cols - 1 || selectedCardIndex != -1) {
          cellColor = cellColor.brighter().brighter();
        }
      }
    }
    return cellColor;
  }

  private static void paintCard(CardCell currentCell, Graphics2D g2d, int x, int cellWidth,
                                int y, int cellHeight) {
    Card currentCard = currentCell.getCard();
    g2d.setColor(Color.BLACK);
    String cardText = currentCard.getAttackValues();

    String northValue = cardText.substring(0, 1);
    String southValue = cardText.substring(1, 2);
    String eastValue = cardText.substring(2, 3);
    String westValue = cardText.substring(3, 4);
    int centerX = x + cellWidth / 2;
    int centerY = y + cellHeight / 2;

    int textYOffset = cellHeight / 4;
    int textXOffset = cellWidth / 4;

    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.setColor(Color.BLACK);

    // Draw attack values
    g2d.drawString(northValue, centerX, centerY - textYOffset);
    g2d.drawString(southValue, centerX, centerY + textYOffset);
    g2d.drawString(eastValue, centerX + textXOffset, centerY);
    g2d.drawString(westValue, centerX - textXOffset, centerY);
  }

  private class TriosClickListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      features.handleCellClick(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
  }
}
