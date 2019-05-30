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
    if ( count % 10 == 0 && this.onGround )
    {
      this.jump();
    }

    this.setMotionRelative( 0.1f, 0.0f, 0.0f, 0.01f );
    this.moveEntity( MoverType.SELF, this.motionX, this.motionY, this.motionZ );

    this.motionY -= 0.089f;
    // this.posX += this.motionX;
    // this.posY += this.motionY;
    // this.posZ += this.motionZ;
    count++ ;
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
