package com.evolution.entity;

public class Player extends Entity
{
  private float health;
  private int food;
  int count = 0;

  public Player()
  {
    super();
    this.setSize( 0.7F, 1.8F );
  }

  /**
   * Called to update player logic
   */
  public void update()
  {
    this.setMotionRelative( 0.1f, 0.0f, 0.0f, 0.05f );
    this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );
    this.motionY -= 0.08;
    this.motionY = Math.max( -1.0, motionY );
    this.motionX *= 0.9900000095367432D;
    this.motionY *= 0.9800000190734863D;
    this.motionZ *= 0.9900000095367432D;
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
