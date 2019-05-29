package com.evolution.player;

import com.evolution.main.Main;

public class Player
{
  private float health;
  private int food;

  public double x;
  public double y;
  public double z;

  public float yaw;
  public float pitch;

  public boolean onGround;

  /**
   * Called to update player logic
   */
  public void update()
  {
    System.out.println( Main.world.getBlock( x, y - 1, z ) );
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
