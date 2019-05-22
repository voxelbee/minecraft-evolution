package com.evolution.network.handler;

import com.evomine.decode.PacketLayout;

public class PlayHandler implements IPlayHandler
{
    private final NettyManager netManager;

    private boolean doneLoadingTerrain;
    public int currentServerMaxPlayers = 20;

    public PlayHandler(NettyManager networkManagerIn)
    {
        this.netManager = networkManagerIn;
    }

	@Override
	public void onDisconnect(String reason)
	{
		
	}

	@Override
	public void handleSpawnObject(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnExperienceOrb(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnGlobalEntity(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnMob(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleScoreboardObjective(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnPainting(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnPlayer(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleAnimation(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleStatistics(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleRecipeBook(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleBlockBreakAnim(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSignEditorOpen(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleUpdateTileEntity(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleBlockAction(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleBlockChange(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleChat(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleTabComplete(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleMultiBlockChange(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleMaps(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleConfirmTransaction(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCloseWindow(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleWindowItems(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleOpenWindow(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleWindowProperty(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSetSlot(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCustomPayload(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleDisconnect(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleUseBed(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityStatus(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityAttach(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSetPassengers(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleExplosion(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleChangeGameState(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleKeepAlive(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleChunkData(PacketLayout packetIn)
	{
		
	}

	@Override
	public void processChunkUnload(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEffect(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleJoinGame(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityMovement(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handlePlayerPosLook(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleParticles(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handlePlayerAbilities(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handlePlayerListItem(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleDestroyEntities(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleRemoveEntityEffect(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleRespawn(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityHeadLook(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleHeldItemChange(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleDisplayObjective(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityMetadata(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityVelocity(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityEquipment(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSetExperience(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleUpdateHealth(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleTeams(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleUpdateScore(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSpawnPosition(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleTimeUpdate(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleSoundEffect(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCustomSound(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCollectItem(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityTeleport(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityProperties(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleEntityEffect(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCombatEvent(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleServerDifficulty(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCamera(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleWorldBorder(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleTitle(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handlePlayerListHeaderFooter(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleResourcePack(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleUpdateEntityNBT(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleCooldown(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleMoveVehicle(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleAdvancementInfo(PacketLayout packetIn)
	{
		
	}

	@Override
	public void handleAdvancementsTap(PacketLayout packetIn)
	{
		
	}
}
