package cs3500.threetrios.model;

import java.util.Arrays;

public class ModelFactory {
  public static MainModelInterface createModel(String... args) {
    MainModelInterface model = new ThreeTriosGameModel();

    // Parse args and apply decorators
    boolean hasReverse = Arrays.asList(args).contains("reverse");
    boolean hasFallenAce = Arrays.asList(args).contains("fallenace");
    boolean hasSame = Arrays.asList(args).contains("same");
    boolean hasPlus = Arrays.asList(args).contains("plus");

    // Apply Set 1 rules
    if (hasReverse) {
      model = new ReverseRuleDecorator(model);
    }
    if (hasFallenAce) {
      model = new FallenAceDecorator(model);
    }

    // Apply Set 2 rules (mutually exclusive)
    if (hasSame && hasPlus) {
      throw new IllegalArgumentException("Cannot use Same and Plus rules together");
    }
    if (hasSame) {
      model = new SameRuleDecorator(model);
    }
    if (hasPlus) {
      model = new PlusRuleDecorator(model);
    }

    return model;
  }
}