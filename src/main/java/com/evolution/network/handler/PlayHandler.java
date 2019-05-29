package com.evolution.network.handler;

import java.util.Map;

import com.evolution.EnumLoggerType;
import com.evolution.Main;
import com.evolution.network.EnumConnectionState;
import com.evolution.player.Player;
import com.evolution.world.Chunk;
import com.evolution.world.Entity;
import com.evomine.decode.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PlayHandler implements INetHandler
{
  private final NettyManager netManager;
  private int playerIndex;

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
    this.playerIndex = netManager.parent.playerId;
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
    settings.params.put( "viewDistance", (byte) 10 );
    settings.params.put( "chatFlags", 2 );
    settings.params.put( "chatColors", false );
    settings.params.put( "skinParts", (byte) 0 );
    settings.params.put( "mainHand", 1 );
    this.netManager.sendPacket( settings );
  }

  public void handleMapChunk( Packet packetIn )
  {
    int chunkX = (int) packetIn.params.get( "x" );
    int chunkY = (int) packetIn.params.get( "z" );
    int availableSections = (int) packetIn.params.get( "bitMap" );
    ByteBuf chunkData = Unpooled.wrappedBuffer( (byte[]) packetIn.params.get( "chunkData" ) );

    if ( !Main.WORLD.hasChunk( chunkX, chunkY ) )
    {
      Chunk chunk = new Chunk();
      Main.WORLD.setChunk( chunkX, chunkY, chunk );
      chunk.loadFromData( chunkData, availableSections );
      chunkData.release();
      chunk.loaded = true;
    }
  }

  public void handleBlockChange( Packet packetIn )
  {
    Map< String, Object > pos = (Map< String, Object >) packetIn.params.get( "location" );
    int posX = ( (Long) pos.get( "x" ) ).intValue();
    int posY = ( (Long) pos.get( "y" ) ).intValue();
    int posZ = ( (Long) pos.get( "z" ) ).intValue();
    int id = ( (int) packetIn.params.get( "type" ) ) >> 4;
    Main.WORLD.setBlock( posX, posY, posZ, id );
  }

  public void respawn()
  {
    Packet respawn = new Packet( "client_command", EnumConnectionState.PLAY );
    respawn.params.put( "actionId", 0 );
    this.netManager.sendPacket( respawn );
  }

  public void handleSpawnEntityLiving( Packet inPacket )
  {
    int entityId = (int) inPacket.params.get( "entityId" );
    int entityType = (int) inPacket.params.get( "type" );

    Entity entity = new Entity( entityType );
    entity.posX = (double) inPacket.params.get( "x" );
    entity.posY = (double) inPacket.params.get( "y" );
    entity.posZ = (double) inPacket.params.get( "z" );

    entity.motionX = ( (Integer) inPacket.params.get( "velocityX" ) ).floatValue() / 8000.0F;
    entity.motionY = ( (Integer) inPacket.params.get( "velocityY" ) ).floatValue() / 8000.0F;
    entity.motionZ = ( (Integer) inPacket.params.get( "velocityZ" ) ).floatValue() / 8000.0F;

    entity.pitch = ( (Byte) inPacket.params.get( "pitch" ) ).floatValue() * 360 / 256.0F;
    entity.yaw = ( (Byte) inPacket.params.get( "yaw" ) ).floatValue() * 360 / 256.0F;
    entity.pitchHead = ( (Byte) inPacket.params.get( "headPitch" ) ).floatValue() * 360 / 256.0F;
    Main.WORLD.addEntity( entityId, entity );
  }

  public void handlePosition( Packet inPacket )
  {
    Player player = Main.WORLD.getPlayer( playerIndex );
    player.posX = (double) inPacket.params.get( "x" );
    player.posY = (double) inPacket.params.get( "y" );
    player.posZ = (double) inPacket.params.get( "z" );
    player.pitch = (float) inPacket.params.get( "pitch" );
    player.yaw = (float) inPacket.params.get( "yaw" );

    this.lastPosX = player.posX;
    this.lastPosY = player.posY;
    this.lastPosZ = player.posZ;

    this.lastPitch = player.pitch;
    this.lastYaw = player.yaw;

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
    Main.WORLD.getPlayer( playerIndex ).setHealth( (float) packetIn.params.get( "health" ) );
    Main.WORLD.getPlayer( playerIndex ).setFood( (int) packetIn.params.get( "food" ) );
  }

  public void handleKeepAlive( Packet packetIn )
  {
    this.netManager.sendPacket( packetIn );
  }

  @Override
  public void update()
  {
    Player player = Main.WORLD.getPlayer( playerIndex );
    boolean shouldSendPos = player.posX != this.lastPosX || player.posY != this.lastPosY || player.posZ != this.lastPosZ;
    boolean shouldSendLook = player.pitch != this.lastPitch || player.yaw != this.lastYaw;

    if ( shouldSendPos && shouldSendLook )
    {
      Packet postionPacket = new Packet( "position_look", EnumConnectionState.PLAY );
      postionPacket.params.put( "x", player.posX );
      postionPacket.params.put( "y", player.posY );
      postionPacket.params.put( "z", player.posZ );
      postionPacket.params.put( "yaw", player.yaw );
      postionPacket.params.put( "pitch", player.pitch );
      postionPacket.params.put( "onGround", player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPosX = player.posX;
      this.lastPosY = player.posY;
      this.lastPosZ = player.posZ;
      this.lastPitch = player.pitch;
      this.lastYaw = player.yaw;
    }
    else if ( shouldSendPos )
    {
      Packet postionPacket = new Packet( "position", EnumConnectionState.PLAY );
      postionPacket.params.put( "x", player.posX );
      postionPacket.params.put( "y", player.posY );
      postionPacket.params.put( "z", player.posZ );
      postionPacket.params.put( "onGround", player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPosX = player.posX;
      this.lastPosY = player.posY;
      this.lastPosZ = player.posZ;
    }
    else if ( shouldSendLook )
    {
      Packet postionPacket = new Packet( "look", EnumConnectionState.PLAY );
      postionPacket.params.put( "yaw", player.yaw );
      postionPacket.params.put( "pitch", player.pitch );
      postionPacket.params.put( "onGround", player.onGround );
      this.netManager.sendPacket( postionPacket );

      this.lastPitch = player.pitch;
      this.lastYaw = player.yaw;
    }
  }
}
