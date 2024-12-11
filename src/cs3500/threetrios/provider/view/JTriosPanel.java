package cs3500.threetrios.provider.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
  ReadOnlyTT model;
  private TriosController features;
  private int selectedCardIndex = -1;
  private PlayerColor selectedCardColor = PlayerColor.RED;
  boolean showHints;
  private static final Font HINT_FONT = new Font("Arial", Font.BOLD, 14);


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
    this.showHints = false;
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


  @Override
  public Dimension getMinimumSize() {
    return new Dimension(400, 400);
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

  private int parseValue(char value) {
    return value == 'A' ? 10 : Character.getNumericValue(value);
  }

  private Color handleHighlight(Cell currentCell, boolean isHighlighted, int col, int cols) {
    Color cellColor;
    if (currentCell == null) {
      cellColor = Color.DARK_GRAY;
    } else {
      cellColor = currentCell.getColor();
      // deal with highlighted cards
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
    // if not empty then has a card
    Card currentCard = currentCell.getCard();
    // Draw text
    g2d.setColor(Color.BLACK);
    String cardText = currentCard.getAttackValues();

    String northValue = cardText.substring(0, 1);
    String southValue = cardText.substring(1, 2);
    String eastValue = cardText.substring(2, 3);
    String westValue = cardText.substring(3, 4);

    // Calculate positions for attack values
    int centerX = x + cellWidth / 2;
    int centerY = y + cellHeight / 2;

    int textYOffset = cellHeight / 4; // Offset for north/south
    int textXOffset = cellWidth / 4;  // Offset for east/west

    g2d.setFont(new Font("Arial", Font.BOLD, 16));
    g2d.setColor(Color.BLACK);

    // Draw attack values
    g2d.drawString(northValue, centerX, centerY - textYOffset); // North
    g2d.drawString(southValue, centerX, centerY + textYOffset); // South
    g2d.drawString(eastValue, centerX + textXOffset, centerY);  // East
    g2d.drawString(westValue, centerX - textXOffset, centerY);  // West
  }

  private class TriosClickListener implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
      if (features != null) {
        features.handleCellClick(e.getX(), e.getY());
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // empty
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // empty
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // empty
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // empty
    }
  }
}