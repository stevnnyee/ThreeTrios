package cs3500.threetrios.adapter;

import cs3500.threetrios.model.Direction;
import cs3500.threetrios.model.Card;
import cs3500.threetrios.provider.model.PlayerColor;

/**
 * Adapts our Card implementation to work with the provider's Card class.
 */
public class CardAdapter extends cs3500.threetrios.provider.model.Card {
  private final Card modelCard;

  /**
   * Constructs a CardAdapter that wraps our model's Card.
   *
   * @param modelCard the card from our model to adapt
   */
  public CardAdapter(Card modelCard) {
    super(modelCard.getName(),
            modelCard.getAttackPower(Direction.NORTH),
            modelCard.getAttackPower(Direction.SOUTH),
            modelCard.getAttackPower(Direction.EAST),
            modelCard.getAttackPower(Direction.WEST),
            modelCard.getOwner() != null ?
                    (modelCard.getOwner().getColor().equals("RED") ?
                            PlayerColor.RED : PlayerColor.BLUE) : null);
    this.modelCard = modelCard;
  }

  @Override
  public PlayerColor getColor() {
    if (modelCard.getOwner() == null) {
      throw new IllegalStateException("This card is currently in the deck.");
    }
    return modelCard.getOwner().getColor().equals("RED") ? PlayerColor.RED : PlayerColor.BLUE;
  }
}