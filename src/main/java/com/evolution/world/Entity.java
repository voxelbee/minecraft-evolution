package com.evolution.world;

public class Entity
{
  public double posX;
  public double posY;
  public double posZ;

  public double motionX;
  public double motionY;
  public double motionZ;

  public float yaw;
  public float pitch;
  public float pitchHead;

  private int type;

  public Entity( int type )
  {
    this.type = type;
  }

  public int getType()
  {
    return this.type;
  }
}
