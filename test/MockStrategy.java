import cs3500.threetrios.model.MainModelInterface;
import cs3500.threetrios.model.Player;
import cs3500.threetrios.strategy.AIMove;
import cs3500.threetrios.strategy.AIStrategy;
import cs3500.threetrios.strategy.MockCard;
import cs3500.threetrios.strategy.Position;

/**
 * A mock class for mock strategies that will be used for testing.
 */
class MockStrategy implements AIStrategy {
  private final StringBuilder log;

  /**
   * Constructs a MockStrategy.
   *
   * @param log the log to print
   */
  public MockStrategy(StringBuilder log) {
    this.log = log;
  }

  @Override
  public AIMove findBestMove(MainModelInterface model, Player player) {
    log.append("Finding best move\n");
    return new AIMove(new MockCard("test", 5, 5, 5, 5),
            new Position(0,0), 0);
  }
}
