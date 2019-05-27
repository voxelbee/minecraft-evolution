package com.evolution.network.handler;

import com.evolution.main.EnumLoggerType;
import com.evolution.main.Main;
import com.evomine.decode.Packet;

public class PlayHandler implements IPlayHandler
{
  private final NettyManager netManager;

  public PlayHandler( NettyManager networkManagerIn )
  {
    this.netManager = networkManagerIn;
  }

  @Override
  public void onDisconnect( String reason )
  {
    Main.LOGGER.log( EnumLoggerType.WARN, "Disconnected from server: " + reason );
  }

  @Override
  public void handleSpawnObject( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnExperienceOrb( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnGlobalEntity( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnMob( Packet packetIn )
  {

  }

  @Override
  public void handleScoreboardObjective( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnPainting( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnPlayer( Packet packetIn )
  {

  }

  @Override
  public void handleAnimation( Packet packetIn )
  {

  }

  @Override
  public void handleStatistics( Packet packetIn )
  {

  }

  @Override
  public void handleRecipeBook( Packet packetIn )
  {

  }

  @Override
  public void handleBlockBreakAnim( Packet packetIn )
  {

  }

  @Override
  public void handleSignEditorOpen( Packet packetIn )
  {

  }

  @Override
  public void handleUpdateTileEntity( Packet packetIn )
  {

  }

  @Override
  public void handleBlockAction( Packet packetIn )
  {

  }

  @Override
  public void handleBlockChange( Packet packetIn )
  {

  }

  @Override
  public void handleChat( Packet packetIn )
  {

  }

  @Override
  public void handleTabComplete( Packet packetIn )
  {

  }

  @Override
  public void handleMultiBlockChange( Packet packetIn )
  {

  }

  @Override
  public void handleMaps( Packet packetIn )
  {

  }

  @Override
  public void handleConfirmTransaction( Packet packetIn )
  {

  }

  @Override
  public void handleCloseWindow( Packet packetIn )
  {

  }

  @Override
  public void handleWindowItems( Packet packetIn )
  {

  }

  @Override
  public void handleOpenWindow( Packet packetIn )
  {

  }

  @Override
  public void handleWindowProperty( Packet packetIn )
  {

  }

  @Override
  public void handleSetSlot( Packet packetIn )
  {

  }

  @Override
  public void handleCustomPayload( Packet packetIn )
  {

  }

  @Override
  public void handleDisconnect( Packet packetIn )
  {

  }

  @Override
  public void handleUseBed( Packet packetIn )
  {

  }

  @Override
  public void handleEntityStatus( Packet packetIn )
  {

  }

  @Override
  public void handleEntityAttach( Packet packetIn )
  {

  }

  @Override
  public void handleSetPassengers( Packet packetIn )
  {

  }

  @Override
  public void handleExplosion( Packet packetIn )
  {

  }

  @Override
  public void handleChangeGameState( Packet packetIn )
  {

  }

  @Override
  public void handleKeepAlive( Packet packetIn )
  {
    netManager.sendPacket( packetIn );
  }

  @Override
  public void handleChunkData( Packet packetIn )
  {

  }

  @Override
  public void processChunkUnload( Packet packetIn )
  {

  }

  @Override
  public void handleEffect( Packet packetIn )
  {

  }

  @Override
  public void handleJoinGame( Packet packetIn )
  {

  }

  @Override
  public void handleEntityMovement( Packet packetIn )
  {

  }

  @Override
  public void handlePlayerPosLook( Packet packetIn )
  {

  }

  @Override
  public void handleParticles( Packet packetIn )
  {

  }

  @Override
  public void handlePlayerAbilities( Packet packetIn )
  {

  }

  @Override
  public void handlePlayerListItem( Packet packetIn )
  {

  }

  @Override
  public void handleDestroyEntities( Packet packetIn )
  {

  }

  @Override
  public void handleRemoveEntityEffect( Packet packetIn )
  {

  }

  @Override
  public void handleRespawn( Packet packetIn )
  {

  }

  @Override
  public void handleEntityHeadLook( Packet packetIn )
  {

  }

  @Override
  public void handleHeldItemChange( Packet packetIn )
  {

  }

  @Override
  public void handleDisplayObjective( Packet packetIn )
  {

  }

  @Override
  public void handleEntityMetadata( Packet packetIn )
  {

  }

  @Override
  public void handleEntityVelocity( Packet packetIn )
  {

  }

  @Override
  public void handleEntityEquipment( Packet packetIn )
  {

  }

  @Override
  public void handleSetExperience( Packet packetIn )
  {

  }

  @Override
  public void handleUpdateHealth( Packet packetIn )
  {

  }

  @Override
  public void handleTeams( Packet packetIn )
  {

  }

  @Override
  public void handleUpdateScore( Packet packetIn )
  {

  }

  @Override
  public void handleSpawnPosition( Packet packetIn )
  {

  }

  @Override
  public void handleTimeUpdate( Packet packetIn )
  {

  }

  @Override
  public void handleSoundEffect( Packet packetIn )
  {

  }

  @Override
  public void handleCustomSound( Packet packetIn )
  {

  }

  @Override
  public void handleCollectItem( Packet packetIn )
  {

  }

  @Override
  public void handleEntityTeleport( Packet packetIn )
  {

  }

  @Override
  public void handleEntityProperties( Packet packetIn )
  {

  }

  @Override
  public void handleEntityEffect( Packet packetIn )
  {

  }

  @Override
  public void handleCombatEvent( Packet packetIn )
  {

  }

  @Override
  public void handleServerDifficulty( Packet packetIn )
  {

  }

  @Override
  public void handleCamera( Packet packetIn )
  {

  }

  @Override
  public void handleWorldBorder( Packet packetIn )
  {

  }

  @Override
  public void handleTitle( Packet packetIn )
  {

  }

  @Override
  public void handlePlayerListHeaderFooter( Packet packetIn )
  {

  }

  @Override
  public void handleResourcePack( Packet packetIn )
  {

  }

  @Override
  public void handleUpdateEntityNBT( Packet packetIn )
  {

  }

  @Override
  public void handleCooldown( Packet packetIn )
  {

  }

  @Override
  public void handleMoveVehicle( Packet packetIn )
  {

  }

  @Override
  public void handleAdvancementInfo( Packet packetIn )
  {

  }

  @Override
  public void handleAdvancementsTap( Packet packetIn )
  {

  }
}
