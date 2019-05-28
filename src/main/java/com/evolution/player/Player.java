package com.evolution.player;

import com.evolution.network.Connection;

public class Player
{
  private Connection connection;

  public float health;
  public int food;
  public float foodSaturation;

  public float forward;
  public float strafe;

  public Player( Connection connection )
  {
    this.connection = connection;
  }

  /**
   * Called to update player logic
   */
  public void update()
  {
    forward = 0.1f;
  }
}
