package cs3500.threetrios.provider.view;

import javax.swing.JFrame;

import cs3500.threetrios.provider.model.ReadOnlyTT;

/**
 * Represents a view for the ThreeTrios Game.
 */
public class ThreeTriosView extends JFrame implements TriosView {
  private final JTriosPanel panel;
  private final ReadOnlyTT model;

  public ThreeTriosView(ReadOnlyTT model, String title) {
    this.model = model;
    this.panel = new JTriosPanel(model);
    this.setTitle(title);
    this.setSize(600, 600);
    this.setLocationRelativeTo(null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.add(this.panel);
  }

  @Override
  public void addClickListener(TriosController listener) {
    this.panel.addClickListener(listener);
  }

  /**
   * Refresh the view to reflect any changes in the game state.
   */
  @Override
  public void refresh() {
    this.repaint();
  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  @Override
  public int getCellWidth() {
    return this.panel.getWidth() / (this.model.numCols() + 2);
  }

  @Override
  public int getCellHeight() {
    return this.panel.getHeight() / this.model.numRows();
  }


  @Override
  public boolean selectCard(int row, int col) {
    return this.panel.selectCard(row, col);
  }

}
