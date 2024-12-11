package cs3500.threetrios.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Set;

import cs3500.threetrios.model.Card;
import cs3500.threetrios.model.FallenAceDecorator;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.model.ReadOnlyThreeTriosModel;
import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.ReverseRuleDecorator;

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
    if (model instanceof ReverseRuleDecorator && model instanceof FallenAceDecorator) {
      return countFlipsReverseFallenAce(row, col, selectedCard,
              new HashSet<>(), selectedCard.getOwner());
    } else if (model instanceof ReverseRuleDecorator) {
      return countFlipsReverse(row, col, selectedCard, new HashSet<>(), selectedCard.getOwner());
    } else if (model instanceof FallenAceDecorator) {
      return countFlipsFallenAce(row, col, selectedCard, new HashSet<>(), selectedCard.getOwner());
    } else {
      return countFlipsNormal(row, col, selectedCard, new HashSet<>(), selectedCard.getOwner());
    }
  }

  private int countFlipsReverseFallenAce(int row, int col,
                                         Card attackingCard,
                                         Set<String> processed, Player originalAttacker) {
    int flips = 0;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int adjRow = row + dir[0];
      int adjCol = col + dir[1];
      String coord = adjRow + "," + adjCol;

      if (!processed.contains(coord) && isValidPosition(adjRow, adjCol)) {
        Card adjCard = model.getCardAt(adjRow, adjCol);
        if (adjCard != null && adjCard.getOwner() != originalAttacker) {
          Direction battleDir = getBattleDirection(row, col, adjRow, adjCol);
          int attackValue = attackingCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());
          int attackNum = attackValue == 10 ? 10 : attackValue;
          int defenseNum = defenseValue == 10 ? 10 : defenseValue;
          boolean shouldFlip;
          if ((attackNum == 10 && defenseNum == 1) || (attackNum == 1 && defenseNum == 10)) {
            shouldFlip = false;
          }
          else {
            shouldFlip = attackNum < defenseNum;
          }
          if (shouldFlip) {
            flips++;
            processed.add(coord);
            flips += countFlipsReverseFallenAce(adjRow,
                    adjCol, adjCard, processed, originalAttacker);
          }
        }
      }
    }
    return flips;
  }

  private int countFlipsNormal(int row, int col,
                               Card attackingCard, Set<String> processed,
                               Player originalAttacker) {
    int flips = 0;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int adjRow = row + dir[0];
      int adjCol = col + dir[1];
      String coord = adjRow + "," + adjCol;

      if (!processed.contains(coord) && isValidPosition(adjRow, adjCol)) {
        Card adjCard = model.getCardAt(adjRow, adjCol);
        if (adjCard != null && adjCard.getOwner() != originalAttacker) {
          Direction battleDir = getBattleDirection(row, col, adjRow, adjCol);
          int attackValue = attackingCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

          if (attackValue > defenseValue) {
            flips++;
            processed.add(coord);
            flips += countFlipsNormal(adjRow, adjCol, adjCard, processed, originalAttacker);
          }
        }
      }
    }
    return flips;
  }

  private int countFlipsReverse(int row, int col,
                                Card attackingCard, Set<String> processed,
                                Player originalAttacker) {
    int flips = 0;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int adjRow = row + dir[0];
      int adjCol = col + dir[1];
      String coord = adjRow + "," + adjCol;

      if (!processed.contains(coord) && isValidPosition(adjRow, adjCol)) {
        Card adjCard = model.getCardAt(adjRow, adjCol);
        if (adjCard != null && adjCard.getOwner() != originalAttacker) {
          Direction battleDir = getBattleDirection(row, col, adjRow, adjCol);
          int attackValue = attackingCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

          if (attackValue < defenseValue) {
            flips++;
            processed.add(coord);
            flips += countFlipsReverse(adjRow, adjCol, adjCard, processed, originalAttacker);
          }
        }
      }
    }
    return flips;
  }

  private int countFlipsFallenAce(int row, int col,
                                  Card attackingCard,
                                  Set<String> processed, Player originalAttacker) {
    int flips = 0;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    for (int[] dir : directions) {
      int adjRow = row + dir[0];
      int adjCol = col + dir[1];
      String coord = adjRow + "," + adjCol;

      if (!processed.contains(coord) && isValidPosition(adjRow, adjCol)) {
        Card adjCard = model.getCardAt(adjRow, adjCol);
        if (adjCard != null && adjCard.getOwner() != originalAttacker) {
          Direction battleDir = getBattleDirection(row, col, adjRow, adjCol);
          int attackValue = attackingCard.getAttackPower(battleDir);
          int defenseValue = adjCard.getAttackPower(battleDir.getOpposite());

          boolean shouldFlip = false;
          if (attackValue == 1 && defenseValue == 10) {
            shouldFlip = true;
          } else if (attackValue == 10 && defenseValue == 1) {
            shouldFlip = false;
          } else if (attackValue == 10 && defenseValue >= 2 && defenseValue <= 9) {
            shouldFlip = true;
          } else if (defenseValue == 1) {
            shouldFlip = attackValue > defenseValue;
          } else {
            shouldFlip = attackValue > defenseValue;
          }

          if (shouldFlip) {
            flips++;
            processed.add(coord);
            flips += countFlipsFallenAce(adjRow, adjCol, adjCard, processed, originalAttacker);
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
    if (fromRow < toRow) {
      return Direction.SOUTH;
    }
    if (fromRow > toRow) {
      return Direction.NORTH;
    }
    if (fromCol < toCol) {
      return Direction.EAST;
    }
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

  /**
   * helps refresh.
   * @param card selected card.
   * @param player team person who played the card.
   */
  public void setSelectedCard(Card card, Player player) {
    this.selectedCard = card;
    if (showHints) {
      refresh();
    }
  }
}