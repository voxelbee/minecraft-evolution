package com.evolution.entity;

public class Player extends Entity
{
  private float health;
  private int food;
  int count = 0;

  public Player()
  {
    super();
    this.setSize( 0.6F, 1.8F );
  }

  /**
   * Called to update player logic
   */
  public void update()
  {
    this.travel( 0.1f, 0.0f, 0.0f );
  }

  public float getHealth()
  {
    return this.health;
  }

  public int getFood()
  {
    return this.food;
  }

  public void setHealth( float inHealth )
  {
    this.health = inHealth;
  }

  public void setFood( int inFood )
  {
    this.food = inFood;
  }
}
