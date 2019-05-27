package com.evolution.network.handler;

import com.evomine.decode.Packet;

public interface IPlayHandler extends INetHandler
{
  /**
   * Spawns an instance of the objecttype indicated by the Packet and sets its position and momentum
   */
  void handleSpawnObject( Packet packetIn );

  /**
   * Spawns an experience orb and sets its value (amount of XP)
   */
  void handleSpawnExperienceOrb( Packet packetIn );

  /**
   * Handles globally visible entities. Used in vanilla for lightning bolts
   */
  void handleSpawnGlobalEntity( Packet packetIn );

  /**
   * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
   * entities Datawatchers with the entity metadata specified in the Packet
   */
  void handleSpawnMob( Packet packetIn );

  /**
   * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
   */
  void handleScoreboardObjective( Packet packetIn );

  /**
   * Handles the spawning of a painting object
   */
  void handleSpawnPainting( Packet packetIn );

  /**
   * Handles the creation of a nearby player entity, sets the position and held item
   */
  void handleSpawnPlayer( Packet packetIn );

  /**
   * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
   * or receiving a critical hit by normal or magical means
   */
  void handleAnimation( Packet packetIn );

  /**
   * Updates the players statistics or achievements
   */
  void handleStatistics( Packet packetIn );

  void handleRecipeBook( Packet packetIn );

  /**
   * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
   */
  void handleBlockBreakAnim( Packet packetIn );

  /**
   * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
   */
  void handleSignEditorOpen( Packet packetIn );

  /**
   * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
   * beacons, skulls, flowerpot
   */
  void handleUpdateTileEntity( Packet packetIn );

  /**
   * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
   * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
   * accessing a (Ender)Chest
   */
  void handleBlockAction( Packet packetIn );

  /**
   * Updates the block and metadata and generates a blockupdate (and notify the clients)
   */
  void handleBlockChange( Packet packetIn );

  /**
   * Prints a chatmessage in the chat GUI
   */
  void handleChat( Packet packetIn );

  /**
   * Displays the available command-completion options the server knows of
   */
  void handleTabComplete( Packet packetIn );

  /**
   * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
   * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
   * S21PacketChunkData
   */
  void handleMultiBlockChange( Packet packetIn );

  /**
   * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
   * MapItemRenderer for it
   */
  void handleMaps( Packet packetIn );

  /**
   * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
   * and confirms if it is the case.
   */
  void handleConfirmTransaction( Packet packetIn );

  /**
   * Resets the ItemStack held in hand and closes the window that is opened
   */
  void handleCloseWindow( Packet packetIn );

  /**
   * Handles the placement of a specified ItemStack in a specified container/inventory slot
   */
  void handleWindowItems( Packet packetIn );

  /**
   * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
   * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
   */
  void handleOpenWindow( Packet packetIn );

  /**
   * Sets the progressbar of the opened window to the specified value
   */
  void handleWindowProperty( Packet packetIn );

  /**
   * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
   */
  void handleSetSlot( Packet packetIn );

  /**
   * Handles Packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
   * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
   * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
   * resourcepack for the client to load.
   */
  void handleCustomPayload( Packet packetIn );

  /**
   * Closes the network channel
   */
  void handleDisconnect( Packet packetIn );

  /**
   * Retrieves the player identified by the Packet, puts him to sleep if possible (and flags whether all players are
   * asleep)
   */
  void handleUseBed( Packet packetIn );

  /**
   * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
   * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
   * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
   * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
   */
  void handleEntityStatus( Packet packetIn );

  void handleEntityAttach( Packet packetIn );

  void handleSetPassengers( Packet packetIn );

  /**
   * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the Packet.
   */
  void handleExplosion( Packet packetIn );

  void handleChangeGameState( Packet packetIn );

  void handleKeepAlive( Packet packetIn );

  /**
   * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
   */
  void handleChunkData( Packet packetIn );

  void processChunkUnload( Packet packetIn );

  void handleEffect( Packet packetIn );

  /**
   * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
   * WorldClient and sets the player initial dimension
   */
  void handleJoinGame( Packet packetIn );

  /**
   * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
   * subclassing of the Packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
   * rotation or both).
   */
  void handleEntityMovement( Packet packetIn );

  void handlePlayerPosLook( Packet packetIn );

  /**
   * Spawns a specified number of particles at the specified location with a randomized displacement according to
   * specified bounds
   */
  void handleParticles( Packet packetIn );

  void handlePlayerAbilities( Packet packetIn );

  void handlePlayerListItem( Packet packetIn );

  /**
   * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
   * longer registered as required to monitor them. The latter happens when distance between the player and item
   * increases beyond a certain treshold (typically the viewing distance)
   */
  void handleDestroyEntities( Packet packetIn );

  void handleRemoveEntityEffect( Packet packetIn );

  void handleRespawn( Packet packetIn );

  /**
   * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
   * rotation of the entity itself
   */
  void handleEntityHeadLook( Packet packetIn );

  /**
   * Updates which hotbar slot of the player is currently selected
   */
  void handleHeldItemChange( Packet packetIn );

  /**
   * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
   * name)
   */
  void handleDisplayObjective( Packet packetIn );

  /**
   * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
   * changed -> Registers any changes locally
   */
  void handleEntityMetadata( Packet packetIn );

  /**
   * Sets the velocity of the specified entity to the specified value
   */
  void handleEntityVelocity( Packet packetIn );

  void handleEntityEquipment( Packet packetIn );

  void handleSetExperience( Packet packetIn );

  void handleUpdateHealth( Packet packetIn );

  /**
   * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
   * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
   */
  void handleTeams( Packet packetIn );

  /**
   * Either updates the score with a specified value or removes the score for an objective
   */
  void handleUpdateScore( Packet packetIn );

  void handleSpawnPosition( Packet packetIn );

  void handleTimeUpdate( Packet packetIn );

  void handleSoundEffect( Packet packetIn );

  void handleCustomSound( Packet packetIn );

  void handleCollectItem( Packet packetIn );

  /**
   * Updates an entity's position and rotation as specified by the Packet
   */
  void handleEntityTeleport( Packet packetIn );

  /**
   * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
   * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
   * maxHealth and knockback resistance as well as reinforcement spawning chance.
   */
  void handleEntityProperties( Packet packetIn );

  void handleEntityEffect( Packet packetIn );

  void handleCombatEvent( Packet packetIn );

  void handleServerDifficulty( Packet packetIn );

  void handleCamera( Packet packetIn );

  void handleWorldBorder( Packet packetIn );

  void handleTitle( Packet packetIn );

  void handlePlayerListHeaderFooter( Packet packetIn );

  void handleResourcePack( Packet packetIn );

  void handleUpdateEntityNBT( Packet packetIn );

  void handleCooldown( Packet packetIn );

  void handleMoveVehicle( Packet packetIn );

  void handleAdvancementInfo( Packet packetIn );

  void handleAdvancementsTap( Packet packetIn );
}
