package cs3500.threetrios.provider.view;

import javax.swing.JFrame;
import java.awt.Dimension;
import cs3500.threetrios.provider.model.ReadOnlyTT;
import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.PlayerColor;

/**
 * Represents a view for the ThreeTrios Game.
 */
public class ThreeTriosView extends JFrame implements TriosView {
  private final JTriosPanel panel;
  private final ReadOnlyTT model;
  private static final int MIN_CARD_HEIGHT = 75;
  private static final int HAND_WIDTH = 150;

  public ThreeTriosView(ReadOnlyTT model, String title) {
    this.model = model;
    this.panel = new JTriosPanel(model);
    this.setTitle(title);
    updateWindowSize();

    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.add(this.panel);
    this.setResizable(true);
  }

  private void updateWindowSize() {
    int redHandSize = model.getHand(PlayerColor.RED).size();
    int blueHandSize = model.getHand(PlayerColor.BLUE).size();
    int maxHandSize = Math.max(redHandSize, blueHandSize);
    int minHeight = Math.max(600, maxHandSize * MIN_CARD_HEIGHT);
    int gridWidth = model.numCols() * 80;
    int totalWidth = gridWidth + (2 * HAND_WIDTH);
    int minWidth = Math.max(800, totalWidth);

    this.setSize(minWidth, minHeight);
    this.setMinimumSize(new Dimension(minWidth, Math.min(600, minHeight)));
  }

  @Override
  public void refresh() {
    updateWindowSize();
    this.repaint();
  }

  @Override
  public void makeVisible() {
    this.pack();
    this.setVisible(true);
  }

  @Override
  public int getCellWidth() {
    return (this.panel.getWidth() - (2 * HAND_WIDTH)) / model.numCols();
  }

  @Override
  public int getCellHeight() {
    return this.panel.getHeight() / model.numRows();
  }

  @Override
  public void addClickListener(TriosController listener) {
    this.panel.addClickListener(listener);
  }

  @Override
  public boolean selectCard(int row, int col) {
    return this.panel.selectCard(row, col);
  }
}