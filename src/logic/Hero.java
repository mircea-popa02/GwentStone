package logic;

import fileio.CardInput;
import java.util.ArrayList;

final class Hero extends GenericCard {
  private int mana;
  private String description;
  private ArrayList<String> colors;
  public String name;
  public int health;
  int playerId;
  static final int MAX_HERO_HEALTH = 30;

  public int getMana() {
    return mana;
  }

  public void setMana(final int mana) {
    this.mana = mana;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public ArrayList<String> getColors() {
    return colors;
  }

  public void setColors(final ArrayList<String> colors) {
    this.colors = colors;
  }

  Hero(final CardInput card, final int playerId) {
    this.mana = card.getMana();
    this.description = card.getDescription();
    this.colors = card.getColors();
    this.name = card.getName();
    this.playerId = playerId;
    this.health = MAX_HERO_HEALTH;
  }

  void attackHero(final int damage) {
    this.health -= damage;
  }
}
