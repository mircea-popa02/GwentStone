package logic;

import java.util.ArrayList;

public class GenericCard {
  public int mana;
  public String description;
  public ArrayList<String> colors;
  public String name;

  public GenericCard() {
  }

  /**
   * Method used to subtract health from a card
   * @param damage amount of damage given to card
   */
  public void attackCard(final int damage) {
  }

  /**
   * Applies Weak Knees effect on card
   * @param atk amount of attack removed from card
   */
  public void weakness(final int atk) {
  }
}
