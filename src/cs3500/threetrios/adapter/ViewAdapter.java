package cs3500.threetrios.adapter;

import javax.swing.JFrame;

import java.awt.Dimension;

import cs3500.threetrios.provider.model.ReadOnlyTT;
import cs3500.threetrios.provider.controller.TriosController;
import cs3500.threetrios.provider.model.PlayerColor;
import cs3500.threetrios.provider.view.TriosView;
import cs3500.threetrios.provider.view.JTriosPanel;

/**
 * Adapter class that adapts JTriosPanel to work with our model implementation.
 */
public class ViewAdapter extends JFrame implements TriosView {
  private final JTriosPanel panel;
  private final ReadOnlyTT model;
  private static final int MIN_CARD_HEIGHT = 75;
  private static final int HAND_WIDTH = 150;

  /**
   * Constructs a new ViewAdapter with the specified model and title.
   *
   * @param model model to display
   * @param title the title
   */
  public ViewAdapter(ReadOnlyTT model, String title) {
    this.model = model;
    this.panel = new JTriosPanel(model);
    this.setTitle(title);
    updateWindowSize();

    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.add(this.panel);
    this.setResizable(true);
  }

  /**
   * Updates the window size based on the current game state.
   * Calculates appropriate dimensions based on hand sizes and grid dimensions.
   */
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