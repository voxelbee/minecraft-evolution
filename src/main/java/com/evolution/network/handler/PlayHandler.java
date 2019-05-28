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

  // Walk speed of player
  public float walkSpeed = 0.2f;

  private double lastPosX;
  private double lastPosY;
  private double lastPosZ;

  private float lastYaw;
  private float lastPitch;

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

  public void handlePosition( Packet inPacket )
  {
    this.player.x = (double) inPacket.params.get( "x" );
    this.player.y = (double) inPacket.params.get( "y" );
    this.player.z = (double) inPacket.params.get( "z" );
    this.player.pitch = (float) inPacket.params.get( "pitch" );
    this.player.yaw = (float) inPacket.params.get( "yaw" );

    this.lastPosX = this.player.x;
    this.lastPosY = this.player.y;
    this.lastPosZ = this.player.z;

    this.lastPitch = this.player.pitch;
    this.lastYaw = this.player.yaw;

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
    this.player.setHealth( (float) packetIn.params.get( "health" ) );
    this.player.setFood( (int) packetIn.params.get( "food" ) );
  }

  public void handleKeepAlive( Packet packetIn )
  {
    this.netManager.sendPacket( packetIn );
  }

  @Override
  public void update()
  {
    this.player.update();

    boolean shouldSendPos = this.player.x != this.lastPosX || this.player.y != this.lastPosY || this.player.z != this.lastPosZ;
    boolean shouldSendLook = this.player.pitch != this.lastPitch || this.player.yaw != this.lastYaw;

    if ( shouldSendPos && shouldSendLook )
    {
      Packet postionPacket = new Packet( "position_look", EnumConnectionState.PLAY );
      postionPacket.params.put( "x", this.player.x );
      postionPacket.params.put( "y", this.player.y );
      postionPacket.params.put( "z", this.player.z );
      postionPacket.params.put( "yaw", this.player.yaw );
      postionPacket.params.put( "pitch", this.player.pitch );
      postionPacket.params.put( "onGround", this.player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPosX = this.player.x;
      this.lastPosY = this.player.y;
      this.lastPosZ = this.player.z;
      this.lastPitch = this.player.pitch;
      this.lastYaw = this.player.yaw;
    }
    else if ( shouldSendPos )
    {
      Packet postionPacket = new Packet( "position", EnumConnectionState.PLAY );
      postionPacket.params.put( "x", this.player.x );
      postionPacket.params.put( "y", this.player.y );
      postionPacket.params.put( "z", this.player.z );
      postionPacket.params.put( "onGround", this.player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPosX = this.player.x;
      this.lastPosY = this.player.y;
      this.lastPosZ = this.player.z;
    }
    else if ( shouldSendLook )
    {
      Packet postionPacket = new Packet( "look", EnumConnectionState.PLAY );
      postionPacket.params.put( "yaw", this.player.yaw );
      postionPacket.params.put( "pitch", this.player.pitch );
      postionPacket.params.put( "onGround", this.player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPitch = this.player.pitch;
      this.lastYaw = this.player.yaw;
    }
  }
}
