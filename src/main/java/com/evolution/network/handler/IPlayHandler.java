package com.evolution.network.handler;

import com.evomine.decode.PacketLayout;

public interface IPlayHandler extends INetHandler
{
    /**
     * Spawns an instance of the objecttype indicated by the PacketLayout and sets its position and momentum
     */
    void handleSpawnObject(PacketLayout packetIn);

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    void handleSpawnExperienceOrb(PacketLayout packetIn);

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    void handleSpawnGlobalEntity(PacketLayout packetIn);

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the PacketLayout
     */
    void handleSpawnMob(PacketLayout packetIn);

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    void handleScoreboardObjective(PacketLayout packetIn);

    /**
     * Handles the spawning of a painting object
     */
    void handleSpawnPainting(PacketLayout packetIn);

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    void handleSpawnPlayer(PacketLayout packetIn);

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    void handleAnimation(PacketLayout packetIn);

    /**
     * Updates the players statistics or achievements
     */
    void handleStatistics(PacketLayout packetIn);

    void handleRecipeBook(PacketLayout packetIn);

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    void handleBlockBreakAnim(PacketLayout packetIn);

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    void handleSignEditorOpen(PacketLayout packetIn);

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    void handleUpdateTileEntity(PacketLayout packetIn);

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    void handleBlockAction(PacketLayout packetIn);

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    void handleBlockChange(PacketLayout packetIn);

    /**
     * Prints a chatmessage in the chat GUI
     */
    void handleChat(PacketLayout packetIn);

    /**
     * Displays the available command-completion options the server knows of
     */
    void handleTabComplete(PacketLayout packetIn);

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketLayoutBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketLayoutChunkData
     */
    void handleMultiBlockChange(PacketLayout packetIn);

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    void handleMaps(PacketLayout packetIn);

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    void handleConfirmTransaction(PacketLayout packetIn);

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    void handleCloseWindow(PacketLayout packetIn);

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    void handleWindowItems(PacketLayout packetIn);

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    void handleOpenWindow(PacketLayout packetIn);

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    void handleWindowProperty(PacketLayout packetIn);

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    void handleSetSlot(PacketLayout packetIn);

    /**
     * Handles PacketLayouts that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    void handleCustomPayload(PacketLayout packetIn);

    /**
     * Closes the network channel
     */
    void handleDisconnect(PacketLayout packetIn);

    /**
     * Retrieves the player identified by the PacketLayout, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    void handleUseBed(PacketLayout packetIn);

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    void handleEntityStatus(PacketLayout packetIn);

    void handleEntityAttach(PacketLayout packetIn);

    void handleSetPassengers(PacketLayout packetIn);

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the PacketLayout.
     */
    void handleExplosion(PacketLayout packetIn);

    void handleChangeGameState(PacketLayout packetIn);

    void handleKeepAlive(PacketLayout packetIn);

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    void handleChunkData(PacketLayout packetIn);

    void processChunkUnload(PacketLayout packetIn);

    void handleEffect(PacketLayout packetIn);

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    void handleJoinGame(PacketLayout packetIn);

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the PacketLayout allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    void handleEntityMovement(PacketLayout packetIn);

    void handlePlayerPosLook(PacketLayout packetIn);

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    void handleParticles(PacketLayout packetIn);

    void handlePlayerAbilities(PacketLayout packetIn);

    void handlePlayerListItem(PacketLayout packetIn);

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    void handleDestroyEntities(PacketLayout packetIn);

    void handleRemoveEntityEffect(PacketLayout packetIn);

    void handleRespawn(PacketLayout packetIn);

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    void handleEntityHeadLook(PacketLayout packetIn);

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    void handleHeldItemChange(PacketLayout packetIn);

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    void handleDisplayObjective(PacketLayout packetIn);

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    void handleEntityMetadata(PacketLayout packetIn);

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    void handleEntityVelocity(PacketLayout packetIn);

    void handleEntityEquipment(PacketLayout packetIn);

    void handleSetExperience(PacketLayout packetIn);

    void handleUpdateHealth(PacketLayout packetIn);

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    void handleTeams(PacketLayout packetIn);

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    void handleUpdateScore(PacketLayout packetIn);

    void handleSpawnPosition(PacketLayout packetIn);

    void handleTimeUpdate(PacketLayout packetIn);

    void handleSoundEffect(PacketLayout packetIn);

    void handleCustomSound(PacketLayout packetIn);

    void handleCollectItem(PacketLayout packetIn);

    /**
     * Updates an entity's position and rotation as specified by the PacketLayout
     */
    void handleEntityTeleport(PacketLayout packetIn);

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    void handleEntityProperties(PacketLayout packetIn);

    void handleEntityEffect(PacketLayout packetIn);

    void handleCombatEvent(PacketLayout packetIn);

    void handleServerDifficulty(PacketLayout packetIn);

    void handleCamera(PacketLayout packetIn);

    void handleWorldBorder(PacketLayout packetIn);

    void handleTitle(PacketLayout packetIn);

    void handlePlayerListHeaderFooter(PacketLayout packetIn);

    void handleResourcePack(PacketLayout packetIn);

    void handleUpdateEntityNBT(PacketLayout packetIn);

    void handleCooldown(PacketLayout packetIn);

    void handleMoveVehicle(PacketLayout packetIn);

    void handleAdvancementInfo(PacketLayout packetIn);

    void handleAdvancementsTap(PacketLayout packetIn);
}
