package com.evolution.player;

public class Position
{
  public float x;
  public float y;
  public float z;

  public Position( float inX, float inY, float inZ )
  {
    this.x = inX;
    this.y = inY;
    this.z = inZ;
  }

  public Position()
  {

  }

  @Override
  public String toString()
  {
    return "x: " + x + ", y: " + y + ", z: " + z;
  }

  public boolean equals( Position pos )
  {
    return pos.x == this.x && pos.y == this.y && pos.z == this.z;
  }
}
