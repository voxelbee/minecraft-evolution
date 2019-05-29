package com.evolution.player;

import com.evolution.Main;

public class Player
{
  private float health;
  private int food;

  public double posX;
  public double posY;
  public double posZ;

  public float yaw;
  public float pitch;

  public boolean onGround;

  private float forwardForce;
  private float strafeForce;
  private float upwardsForce;

  private double velocityX;
  private double velocityY;
  private double velocityZ;

  /**
   * Called to update player logic
   */
  public void update()
  {
    int blockId = Main.WORLD.getBlock( posX, posY, posZ );
    if ( blockId == 0 )
    {
      velocityY -= 0.08D;
      if ( velocityY >= 1.0D )
      {
        velocityY -= 1.0D;
      }
    }
    else
    {
      velocityY = 0;
    }
    velocityX = 0.2f;

    posX += velocityX;
    posZ += velocityZ;
    posY += velocityY;
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
