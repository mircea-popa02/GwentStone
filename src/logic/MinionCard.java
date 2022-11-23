package logic;


import java.util.ArrayList;

final class MinionCard extends GenericCard {
  public int mana;
  public int health;
  public int attackDamage;
  public String description;
  public ArrayList<String> colors;
  public String name;

  MinionCard(final int mana, final int health, final int attackDamage,
                    final String description, final ArrayList<String> colors, final String name) {
    this.mana = mana;
    this.health = health;
    this.attackDamage = attackDamage;
    this.description = description;
    this.colors = colors;
    this.name = name;
  }

  /**
   * Subtracts health from card
   * @param damage amount of damage given to card
   */
  @Override
  public void attackCard(final int damage) {
    this.health -= damage;
  }

  /**
   * Applies Weak Knees effect to a card
   * @param atk amount of attack removed from card
   */
  @Override
  public void weakness(final int atk) {
    this.attackDamage -= atk;
  }
}
