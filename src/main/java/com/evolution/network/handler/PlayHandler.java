package com.evolution.network.handler;

import com.evolution.main.EnumLoggerType;
import com.evolution.main.Main;
import com.evolution.network.EnumConnectionState;
import com.evolution.player.Player;
import com.evomine.decode.Packet;

public class PlayHandler implements INetHandler
{
  private final NettyManager netManager;
  private Player player;

  // Server position
  private double lastPosX;
  private double lastPosY;
  private double lastPosZ;

  // Local pos of player
  public double localPosX;
  public double localPosY;
  public double localPosZ;

  // Walk speed of player
  public float walkSpeed;

  // Server rotations
  private float lastYaw;
  private float lastPitch;

  // Local rotations
  public float localYaw;
  public float localPitch;

  public PlayHandler( NettyManager networkManagerIn )
  {
    this.netManager = networkManagerIn;
    this.player = netManager.parent.getPlayer();
  }

  @Override
  public void onDisconnect( String reason )
  {
    Main.LOGGER.log( EnumLoggerType.WARN, "Disconnected from server: " + reason );
  }

  /**
   * Handles the packet [login] for the current player
   */
  public void handleJoinGame( Packet packetIn )
  {
    Packet settings = new Packet( "settings", EnumConnectionState.PLAY );
    settings.params.put( "locale", "en_GB" );
    settings.params.put( "viewDistance", (byte) 4 );
    settings.params.put( "chatFlags", 2 );
    settings.params.put( "chatColors", false );
    settings.params.put( "skinParts", (byte) 0 );
    settings.params.put( "mainHand", 1 );
    this.netManager.sendPacket( settings );
  }

  public void respawn()
  {
    Packet respawn = new Packet( "client_command", EnumConnectionState.PLAY );
    respawn.params.put( "actionId", 0 );
    this.netManager.sendPacket( respawn );
  }

  private void updatePosition( double x, double y, double z, boolean onGround )
  {
    Packet postionPacket = new Packet( "position", EnumConnectionState.PLAY );
    postionPacket.params.put( "x", x );
    postionPacket.params.put( "y", y );
    postionPacket.params.put( "z", z );
    postionPacket.params.put( "onGround", onGround );
    this.netManager.sendPacket( postionPacket );
  }

  public void handlePosition( Packet inPacket )
  {
    System.out.println( inPacket.params );
    setLocalAndLastPos( (double) inPacket.params.get( "x" ), (double) inPacket.params.get( "y" ), (double) inPacket.params.get( "z" ) );

    Packet confirm = new Packet( "teleport_confirm", EnumConnectionState.PLAY );
    confirm.params.put( "teleportId", (int) inPacket.params.get( "teleportId" ) );
    this.netManager.sendPacket( confirm );
  }

  public void handlePlayerAbilities( Packet packetIn )
  {
    this.walkSpeed = (float) packetIn.params.get( "walkingSpeed" );
  }

  public void handleUpdateHealth( Packet packetIn )
  {
    this.player.health = (float) packetIn.params.get( "health" );
    this.player.food = (int) packetIn.params.get( "food" );
    this.player.foodSaturation = (float) packetIn.params.get( "foodSaturation" );
  }

  public void handleKeepAlive( Packet packetIn )
  {
    this.netManager.sendPacket( packetIn );
  }

  public void updateMovement()
  {
    this.localPosX += this.player.forward;
    this.localPosZ += this.player.strafe;
  }

  public void setLocalAndLastPos( double x, double y, double z )
  {
    this.localPosX = x;
    this.localPosY = y;
    this.localPosZ = z;

    this.lastPosX = x;
    this.lastPosY = y;
    this.lastPosZ = z;
  }

  @Override
  public void update()
  {
    this.player.update();
    updateMovement();

    boolean flag = this.lastPosX != this.localPosX || this.lastPosY != this.localPosY || this.lastPosZ != this.localPosZ;

    if ( flag )
    {
      updatePosition( this.localPosX, this.localPosY, this.localPosZ, true );
    }
  }
}
