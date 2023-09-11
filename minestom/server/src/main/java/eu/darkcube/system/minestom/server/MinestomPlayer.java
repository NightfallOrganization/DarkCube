/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.AdvancementTab;
import net.minestom.server.adventure.AdventurePacketConvertor;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.effects.Effects;
import net.minestom.server.entity.*;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.entity.vehicle.PlayerVehicleInformation;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.ItemUpdateStateEvent;
import net.minestom.server.event.item.PickupExperienceEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.WrittenBookMeta;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.message.ChatPosition;
import net.minestom.server.message.Messenger;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.login.LoginDisconnectPacket;
import net.minestom.server.network.packet.server.play.*;
import net.minestom.server.network.packet.server.play.data.DeathLocation;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;
import net.minestom.server.recipe.Recipe;
import net.minestom.server.recipe.RecipeManager;
import net.minestom.server.resourcepack.ResourcePack;
import net.minestom.server.scoreboard.BelowNameTag;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.snapshot.EntitySnapshot;
import net.minestom.server.snapshot.PlayerSnapshot;
import net.minestom.server.snapshot.SnapshotImpl;
import net.minestom.server.snapshot.SnapshotUpdater;
import net.minestom.server.statistic.PlayerStatistic;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.async.AsyncUtils;
import net.minestom.server.utils.chunk.ChunkUpdateLimitChecker;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.utils.function.IntegerBiConsumer;
import net.minestom.server.utils.instance.InstanceUtils;
import net.minestom.server.utils.inventory.PlayerInventoryUtils;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.validate.Check;
import net.minestom.server.world.DimensionType;
import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpscUnboundedXaddArrayQueue;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * This class is used to modify the minestom default player for use with DarkCube
 */
@SuppressWarnings({"deprecation", "DeprecatedIsStillUsed"}) public class MinestomPlayer extends Player {

    private static final Component REMOVE_MESSAGE = Component.text("You have been removed from the server without reason.", NamedTextColor.RED);
    private static final int PACKET_PER_TICK = Integer.getInteger("minestom.packet-per-tick", 20);
    private static final int PACKET_QUEUE_SIZE = Integer.getInteger("minestom.packet-queue-size", 1000);
    protected final PlayerConnection playerConnection;
    final IntegerBiConsumer chunkAdder = (chunkX, chunkZ) -> {
        // Load new chunks
        this.instance.loadOptionalChunk(chunkX, chunkZ).thenAccept(chunk -> {
            try {
                if (chunk != null) {
                    chunk.sendChunk(this);
                    EventDispatcher.call(new PlayerChunkLoadEvent(this, chunkX, chunkZ));
                }
            } catch (Exception e) {
                MinecraftServer.getExceptionManager().handleException(e);
            }
        });
    };
    final IntegerBiConsumer chunkRemover = (chunkX, chunkZ) -> {
        // Unload old chunks
        sendPacket(new UnloadChunkPacket(chunkX, chunkZ));
        EventDispatcher.call(new PlayerChunkUnloadEvent(this, chunkX, chunkZ));
    };
    private final AtomicInteger teleportId = new AtomicInteger();
    private final MessagePassingQueue<ClientPacket> packets = new MpscUnboundedXaddArrayQueue<>(32);
    private final boolean levelFlat;
    private final Player.PlayerSettings settings;
    private final ChunkUpdateLimitChecker chunkUpdateLimitChecker = new ChunkUpdateLimitChecker(6);
    // Statistics
    private final Map<PlayerStatistic, Integer> statisticValueMap = new Hashtable<>();
    // Vehicle
    private final PlayerVehicleInformation vehicleInformation = new PlayerVehicleInformation();
    private final Pointers pointers;
    protected PlayerInventory inventory;
    // Experience orb pickup
    protected Cooldown experiencePickupCooldown = new Cooldown(Duration.of(10, TimeUnit.SERVER_TICK));
    private long lastKeepAlive;
    private boolean answerKeepAlive;
    private String username;
    private Component usernameComponent;
    private int latency;
    private Component displayName;
    private PlayerSkin skin;
    private DimensionType dimensionType;
    private GameMode gameMode;
    private DeathLocation deathLocation;
    /**
     * Keeps track of what chunks are sent to the client, this defines the center of the loaded area
     * in the range of {@link MinecraftServer#getChunkViewDistance()}
     */
    private Vec chunksLoadedByClient = Vec.ZERO;
    private int receivedTeleportId;
    private float exp;
    private int level;
    private Inventory openInventory;
    // Used internally to allow the closing of inventory within the inventory listener
    private boolean didCloseInventory;
    private byte heldSlot;
    private Pos respawnPoint;
    private int food;
    private float foodSaturation;
    private long startEatingTime;
    private long defaultEatingTime = 1000L;
    private long eatingTime;
    private Player.Hand eatingHand;
    // Game state (https://wiki.vg/Protocol#Change_Game_State)
    private boolean enableRespawnScreen;
    private BelowNameTag belowNameTag;
    private int permissionLevel;
    private boolean reducedDebugScreenInformation;
    // Abilities
    private boolean flying;
    private boolean allowFlying;
    private boolean instantBreak;
    private float flyingSpeed = 0.05f;
    private float fieldViewModifier = 0.1f;
    // Adventure
    private Identity identity;

    public MinestomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
        this.username = username;
        this.usernameComponent = Component.text(username);
        this.playerConnection = playerConnection;

        setRespawnPoint(Pos.ZERO);

        this.settings = new Player.PlayerSettings();
        this.inventory = new PlayerInventory(this);

        setCanPickupItem(true); // By default

        // Allow the server to send the next keep alive packet
        refreshAnswerKeepAlive(true);

        this.gameMode = GameMode.SURVIVAL;
        this.dimensionType = DimensionType.OVERWORLD; // Default dimension
        this.levelFlat = true;
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);

        // FakePlayer init its connection there
        playerConnectionInit();

        this.identity = Identity.identity(uuid);
        this.pointers = Pointers
                .builder()
                .withDynamic(Identity.UUID, this::getUuid)
                .withDynamic(Identity.NAME, this::getUsername)
                .withDynamic(Identity.DISPLAY_NAME, this::getDisplayName)
                .build();
    }

    /**
     * Used when the player is created.
     * Init the player and spawn him.
     * <p>
     * WARNING: executed in the main update thread
     * UNSAFE: Only meant to be used when a socket player connects through the server.
     *
     * @param spawnInstance the player spawn instance (defined in {@link PlayerLoginEvent})
     */
    @Override public CompletableFuture<Void> UNSAFE_init(@NotNull Instance spawnInstance) {
        this.dimensionType = spawnInstance.getDimensionType();

        NBTCompound nbt = NBT.Compound(Map.of("minecraft:chat_type", Messenger.chatRegistry(), "minecraft:dimension_type", MinecraftServer
                .getDimensionTypeManager()
                .toNBT(), "minecraft:worldgen/biome", MinecraftServer.getBiomeManager().toNBT()));

        final JoinGamePacket joinGamePacket = new JoinGamePacket(getEntityId(), false, gameMode, null, List.of(dimensionType
                .getName()
                .asString()), nbt, dimensionType.toString(), dimensionType
                .getName()
                .asString(), 0, 0, MinecraftServer.getChunkViewDistance(), MinecraftServer.getChunkViewDistance(), false, true, false, levelFlat, deathLocation);
        sendPacket(joinGamePacket);

        // Server brand name
        sendPacket(PluginMessagePacket.getBrandPacket());
        // Difficulty
        sendPacket(new ServerDifficultyPacket(MinecraftServer.getDifficulty(), true));

        sendPacket(new SpawnPositionPacket(respawnPoint, 0));

        // Add player to list with spawning skin
        PlayerSkin profileSkin = null;
        if (playerConnection instanceof PlayerSocketConnection socketConnection) {
            final GameProfile gameProfile = socketConnection.gameProfile();
            if (gameProfile != null) {
                for (GameProfile.Property property : gameProfile.properties()) {
                    if (property.name().equals("textures")) {
                        profileSkin = new PlayerSkin(property.value(), property.signature());
                        break;
                    }
                }
            }
        }
        PlayerSkinInitEvent skinInitEvent = new PlayerSkinInitEvent(this, profileSkin);
        EventDispatcher.call(skinInitEvent);
        this.skin = skinInitEvent.getSkin();
        // FIXME: when using Geyser, this line remove the skin of the client
        PacketUtils.broadcastPacket(getAddPlayerToList());
        for (var player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if (player != this) sendPacket(((MinestomPlayer) player).getAddPlayerToList());
        }

        //Teams
        for (Team team : MinecraftServer.getTeamManager().getTeams()) {
            sendPacket(team.createTeamsCreationPacket());
        }

        // Commands
        refreshCommands();

        // Recipes start
        {
            RecipeManager recipeManager = MinecraftServer.getRecipeManager();
            sendPacket(recipeManager.getDeclareRecipesPacket());

            List<String> recipesIdentifier = new ArrayList<>();
            for (Recipe recipe : recipeManager.getRecipes()) {
                if (!recipe.shouldShow(this)) continue;
                recipesIdentifier.add(recipe.getRecipeId());
            }
            if (!recipesIdentifier.isEmpty()) {
                UnlockRecipesPacket unlockRecipesPacket = new UnlockRecipesPacket(0, false, false, false, false, false, false, false, false, recipesIdentifier, recipesIdentifier);
                sendPacket(unlockRecipesPacket);
            }
        }
        // Recipes end

        // Tags
        sendPacket(TagsPacket.DEFAULT_TAGS);

        // Some client updates
        sendPacket(getPropertiesPacket()); // Send default properties
        triggerStatus((byte) (24 + permissionLevel)); // Set permission level
        refreshHealth(); // Heal and send health packet
        refreshAbilities(); // Send abilities packet

        return setInstance(spawnInstance);
    }

    /**
     * Used to initialize the player connection
     */
    @Override protected void playerConnectionInit() {
        PlayerConnection connection = playerConnection;
        if (connection != null) connection.setPlayer(this);
    }

    @SuppressWarnings("unused") @Override public void update(long time) {
        // Process received packets
        interpretPacketQueue();

        super.update(time); // Super update (item pickup/fire management)

        // Experience orb pickup
        if (experiencePickupCooldown.isReady(time)) {
            experiencePickupCooldown.refreshLastUpdate(time);
            final Point loweredPosition = position.sub(0, .5, 0);
            this.instance
                    .getEntityTracker()
                    .nearbyEntities(position, expandedBoundingBox.width(), EntityTracker.Target.EXPERIENCE_ORBS, experienceOrb -> {
                        if (expandedBoundingBox.intersectEntity(loweredPosition, experienceOrb)) {
                            PickupExperienceEvent pickupExperienceEvent = new PickupExperienceEvent(this, experienceOrb);
                            EventDispatcher.callCancellable(pickupExperienceEvent, () -> {
                                short experienceCount = pickupExperienceEvent.getExperienceCount(); // TODO give to player
                                experienceOrb.remove();
                            });
                        }
                    });
        }

        // Eating animation
        if (isEating()) {
            if (time - startEatingTime >= eatingTime) {
                triggerStatus((byte) 9); // Mark item use as finished
                ItemUpdateStateEvent itemUpdateStateEvent = callItemUpdateStateEvent(eatingHand);

                Check.notNull(itemUpdateStateEvent, "#callItemUpdateStateEvent returned null.");

                // Refresh hand
                final boolean isOffHand = itemUpdateStateEvent.getHand() == Player.Hand.OFF;
                refreshActiveHand(false, isOffHand, false);

                final ItemStack foodItem = itemUpdateStateEvent.getItemStack();
                final boolean isFood = foodItem.material().isFood();

                if (isFood) {
                    PlayerEatEvent playerEatEvent = new PlayerEatEvent(this, foodItem, eatingHand);
                    EventDispatcher.call(playerEatEvent);
                }

                refreshEating(null);
            }
        }

        // Tick event
        EventDispatcher.call(new PlayerTickEvent(this));
    }

    @Override public void kill() {
        if (!isDead()) {

            Component deathText;
            Component chatMessage;

            // get death screen text to the killed player
            {
                if (lastDamageSource != null) {
                    deathText = lastDamageSource.buildDeathScreenText(this);
                } else { // may happen if killed by the server without applying damage
                    deathText = Component.text("Killed by poor programming.");
                }
            }

            // get death message to chat
            {
                if (lastDamageSource != null) {
                    chatMessage = lastDamageSource.buildDeathMessage(this);
                } else { // may happen if killed by the server without applying damage
                    chatMessage = Component.text(getUsername() + " was killed by poor programming.");
                }
            }

            // Call player death event
            PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(this, deathText, chatMessage);
            EventDispatcher.call(playerDeathEvent);

            deathText = playerDeathEvent.getDeathText();
            chatMessage = playerDeathEvent.getChatMessage();

            // #buildDeathScreenText can return null, check here
            if (deathText != null) {
                sendPacket(new DeathCombatEventPacket(getEntityId(), -1, deathText));
            }

            // #buildDeathMessage can return null, check here
            if (chatMessage != null) {
                Audiences.players().sendMessage(chatMessage);
            }

            // Set death location
            if (getInstance() != null) setDeathLocation(getInstance().getDimensionType(), getPosition());
        }
        super.kill();
    }

    /**
     * Respawns the player by sending a {@link RespawnPacket} to the player and teleporting him
     * to {@link #getRespawnPoint()}. It also resets fire and health.
     */
    @Override public void respawn() {
        if (!isDead()) return;

        setFireForDuration(0);
        setOnFire(false);
        refreshHealth();

        sendPacket(new RespawnPacket(getDimensionType().toString(), getDimensionType()
                .getName()
                .asString(), 0, gameMode, gameMode, false, levelFlat, true, deathLocation));

        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(this);
        EventDispatcher.call(respawnEvent);
        triggerStatus((byte) (24 + permissionLevel)); // Set permission level
        refreshIsDead(false);
        updatePose();

        Pos respawnPosition = respawnEvent.getRespawnPosition();

        // The client unloads chunks when respawning, so resend all chunks next to spawn
        ChunkUtils.forChunksInRange(respawnPosition, Math.min(MinecraftServer.getChunkViewDistance(), settings.getViewDistance()), (chunkX, chunkZ) -> this.instance
                .loadOptionalChunk(chunkX, chunkZ)
                .thenAccept(chunk -> {
                    try {
                        if (chunk != null) {
                            chunk.sendChunk(this);
                        }
                    } catch (Exception e) {
                        MinecraftServer.getExceptionManager().handleException(e);
                    }
                }));
        chunksLoadedByClient = new Vec(respawnPosition.chunkX(), respawnPosition.chunkZ());
        // Client also needs all entities resent to them, since those are unloaded as well
        this.instance
                .getEntityTracker()
                .nearbyEntitiesByChunkRange(respawnPosition, Math.min(MinecraftServer.getChunkViewDistance(), settings.getViewDistance()), EntityTracker.Target.ENTITIES, entity -> {
                    // Skip refreshing self with a new viewer
                    if (!entity.getUuid().equals(uuid)) {
                        entity.updateNewViewer(this);
                    }
                });
        teleport(respawnPosition).thenRun(this::refreshAfterTeleport);
    }

    /**
     * Sends necessary packets to synchronize player data after a {@link RespawnPacket}
     */
    private void refreshClientStateAfterRespawn() {
        sendPacket(new ServerDifficultyPacket(MinecraftServer.getDifficulty(), false));
        sendPacket(new UpdateHealthPacket(this.getHealth(), food, foodSaturation));
        sendPacket(new SetExperiencePacket(exp, level, 0));
        triggerStatus((byte) (24 + permissionLevel)); // Set permission level
        refreshAbilities();
    }

    /**
     * Refreshes the command list for this player. This checks the
     * {@link net.minestom.server.command.builder.condition.CommandCondition}s
     * again, and any changes will be visible to the player.
     */
    @Override public void refreshCommands() {
        sendPacket(MinecraftServer.getCommandManager().createDeclareCommandsPacket(this));
    }

    @Override public void remove() {
        if (isRemoved()) return;
        EventDispatcher.call(new PlayerDisconnectEvent(this));
        super.remove();
        this.packets.clear();
        final Inventory currentInventory = getOpenInventory();
        if (currentInventory != null) currentInventory.removeViewer(this);
        MinecraftServer.getBossBarManager().removeAllBossBars(this);
        // Advancement tabs cache
        {
            Set<AdvancementTab> advancementTabs = AdvancementTab.getTabs(this);
            if (advancementTabs != null) {
                for (AdvancementTab advancementTab : advancementTabs) {
                    advancementTab.removeViewer(this);
                }
            }
        }
        final Pos position = this.position;
        final int chunkX = position.chunkX();
        final int chunkZ = position.chunkZ();
        // Clear all viewable chunks
        ChunkUtils.forChunksInRange(chunkX, chunkZ, MinecraftServer.getChunkViewDistance(), chunkRemover);
        // Remove from the tab-list
        PacketUtils.broadcastPacket(getRemovePlayerToList());

        // Prevent the player from being stuck in loading screen, or just unable to interact with the server
        // This should be considered as a bug, since the player will ultimately time out anyway.
        if (playerConnection.isOnline()) kick(REMOVE_MESSAGE);
    }

    @Override public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        // Team
        if (this.getTeam() != null && this.getTeam().getMembers().size() == 1) {// If team only contains "this" player
            player.sendPacket(this.getTeam().createTeamDestructionPacket());
        }
    }

    @Override public void sendPacketToViewersAndSelf(@NotNull SendablePacket packet) {
        sendPacket(packet);
        super.sendPacketToViewersAndSelf(packet);
    }

    /**
     * Changes the player instance and load surrounding chunks if needed.
     * <p>
     * Be aware that because chunk operations are expensive,
     * it is possible for this method to be non-blocking when retrieving chunks is required.
     *
     * @param instance      the new player instance
     * @param spawnPosition the new position of the player
     * @return a future called once the player instance changed
     */
    @Override public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        final Instance currentInstance = this.instance;
        Check.argCondition(currentInstance == instance, "Instance should be different than the current one");
        if (InstanceUtils.areLinked(currentInstance, instance) && spawnPosition.sameChunk(this.position)) {
            // The player already has the good version of all the chunks.
            // We just need to refresh his entity viewing list and add him to the instance
            spawnPlayer(instance, spawnPosition, false, false, false);
            return AsyncUtils.VOID_FUTURE;
        }
        // Must update the player chunks
        chunkUpdateLimitChecker.clearHistory();
        final boolean dimensionChange = !Objects.equals(dimensionType, instance.getDimensionType());
        final Consumer<Instance> runnable = (i) -> spawnPlayer(i, spawnPosition, currentInstance == null, dimensionChange, true);

        // Ensure that surrounding chunks are loaded
        List<CompletableFuture<Chunk>> futures = new ArrayList<>();
        ChunkUtils.forChunksInRange(spawnPosition, MinecraftServer.getChunkViewDistance(), (chunkX, chunkZ) -> {
            final CompletableFuture<Chunk> future = instance.loadOptionalChunk(chunkX, chunkZ);
            if (!future.isDone()) futures.add(future);
        });
        if (futures.isEmpty()) {
            // All chunks are already loaded
            runnable.accept(instance);
            return AsyncUtils.VOID_FUTURE;
        }

        // One or more chunks need to be loaded
        final Thread runThread = Thread.currentThread();
        CountDownLatch latch = new CountDownLatch(1);
        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        CompletableFuture<Void> future = new CompletableFuture<>() {
            @Override public Void join() {
                // Prevent deadlock
                if (runThread == Thread.currentThread()) {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    scheduler.process();
                    assert isDone();
                }
                return super.join();
            }
        };

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> {
            scheduler.scheduleNextProcess(() -> {
                runnable.accept(instance);
                future.complete(null);
            });
            latch.countDown();
        });
        return future;
    }

    /**
     * Changes the player instance without changing its position (defaulted to {@link #getRespawnPoint()}
     * if the player is not in any instance).
     *
     * @param instance the new player instance
     * @return a {@link CompletableFuture} called once the entity's instance has been set,
     * this is due to chunks needing to load for players
     * @see #setInstance(Instance, Pos)
     */
    @Override public CompletableFuture<Void> setInstance(@NotNull Instance instance) {
        return setInstance(instance, this.instance != null ? getPosition() : getRespawnPoint());
    }

    /**
     * Used to spawn the player once the client has all the required chunks.
     * <p>
     * Does add the player to {@code instance}, remove all viewable entities and call {@link PlayerSpawnEvent}.
     * <p>
     * UNSAFE: only called with {@link #setInstance(Instance, Pos)}.
     *
     * @param spawnPosition the position to teleport the player
     * @param firstSpawn    true if this is the player first spawn
     * @param updateChunks  true if chunks should be refreshed, false if the new instance shares the same
     *                      chunks
     */
    private void spawnPlayer(@NotNull Instance instance, @NotNull Pos spawnPosition, boolean firstSpawn, boolean dimensionChange, boolean updateChunks) {
        if (!firstSpawn && !dimensionChange) {
            // Player instance changed, clear current viewable collections
            if (updateChunks) ChunkUtils.forChunksInRange(spawnPosition, MinecraftServer.getChunkViewDistance(), chunkRemover);
        }

        if (dimensionChange) sendDimension(instance.getDimensionType());

        super.setInstance(instance, spawnPosition);

        if (updateChunks) {
            final int chunkX = spawnPosition.chunkX();
            final int chunkZ = spawnPosition.chunkZ();
            chunksLoadedByClient = new Vec(chunkX, chunkZ);
            chunkUpdateLimitChecker.addToHistory(getChunk());
            sendPacket(new UpdateViewPositionPacket(chunkX, chunkZ));
            ChunkUtils.forChunksInRange(spawnPosition, MinecraftServer.getChunkViewDistance(), chunkAdder);
        }

        synchronizePosition(true); // So the player doesn't get stuck

        if (dimensionChange) {
            sendPacket(new SpawnPositionPacket(spawnPosition, 0)); // Without this the client gets stuck on loading terrain for a while
            instance.getWorldBorder().init(this);
            sendPacket(new TimeUpdatePacket(instance.getWorldAge(), instance.getTime()));
        }

        if (dimensionChange || firstSpawn) {
            this.inventory.update();
            sendPacket(new HeldItemChangePacket(heldSlot));
        }

        EventDispatcher.call(new PlayerSpawnEvent(this, instance, firstSpawn));
    }

    /**
     * Sends a plugin message to the player.
     *
     * @param channel the message channel
     * @param data    the message data
     */
    @Override public void sendPluginMessage(@NotNull String channel, byte @NotNull [] data) {
        sendPacket(new PluginMessagePacket(channel, data));
    }

    /**
     * Sends a plugin message to the player.
     * <p>
     * Message encoded to UTF-8.
     *
     * @param channel the message channel
     * @param message the message
     */
    @Override public void sendPluginMessage(@NotNull String channel, @NotNull String message) {
        sendPluginMessage(channel, message.getBytes(StandardCharsets.UTF_8));
    }

    @Override public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        Messenger.sendMessage(this, message, ChatPosition.fromMessageType(type), source.uuid());
    }

    @Override public void playSound(@NotNull Sound sound) {
        this.playSound(sound, this.position.x(), this.position.y(), this.position.z());
    }

    @Override public void playSound(@NotNull Sound sound, @NotNull Point point) {
        sendPacket(AdventurePacketConvertor.createSoundPacket(sound, point.x(), point.y(), point.z()));
    }

    @Override public void playSound(@NotNull Sound sound, double x, double y, double z) {
        sendPacket(AdventurePacketConvertor.createSoundPacket(sound, x, y, z));
    }

    @Override public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        final ServerPacket packet;
        if (emitter == Sound.Emitter.self()) {
            packet = AdventurePacketConvertor.createSoundPacket(sound, this);
        } else {
            packet = AdventurePacketConvertor.createSoundPacket(sound, emitter);
        }
        sendPacket(packet);
    }

    @Override public void stopSound(@NotNull SoundStop stop) {
        sendPacket(AdventurePacketConvertor.createSoundStopPacket(stop));
    }

    /**
     * Plays a given effect at the given position for this player.
     *
     * @param effect                the effect to play
     * @param x                     x position of the effect
     * @param y                     y position of the effect
     * @param z                     z position of the effect
     * @param data                  data for the effect
     * @param disableRelativeVolume disable volume scaling based on distance
     */
    @Override public void playEffect(@NotNull Effects effect, int x, int y, int z, int data, boolean disableRelativeVolume) {
        sendPacket(new EffectPacket(effect.getId(), new Vec(x, y, z), data, disableRelativeVolume));
    }

    @Override public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        sendPacket(new PlayerListHeaderAndFooterPacket(header, footer));
    }

    @Override public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        sendPacket(AdventurePacketConvertor.createTitlePartPacket(part, value));
    }

    @Override public void sendActionBar(@NotNull Component message) {
        sendPacket(new ActionBarPacket(message));
    }

    @Override public void resetTitle() {
        sendPacket(new ClearTitlesPacket(true));
    }

    @Override public void clearTitle() {
        sendPacket(new ClearTitlesPacket(false));
    }

    @Override public void showBossBar(@NotNull BossBar bar) {
        MinecraftServer.getBossBarManager().addBossBar(this, bar);
    }

    @Override public void hideBossBar(@NotNull BossBar bar) {
        MinecraftServer.getBossBarManager().removeBossBar(this, bar);
    }

    @Override public void openBook(@NotNull Book book) {
        final ItemStack writtenBook = ItemStack
                .builder(Material.WRITTEN_BOOK)
                .meta(WrittenBookMeta.class, builder -> builder
                        .resolved(false)
                        .generation(WrittenBookMeta.WrittenBookGeneration.ORIGINAL)
                        .author(book.author())
                        .title(book.title())
                        .pages(book.pages()))
                .build();
        // Set book in offhand
        sendPacket(new SetSlotPacket((byte) 0, 0, (short) PlayerInventoryUtils.OFFHAND_SLOT, writtenBook));
        // Open the book
        sendPacket(new OpenBookPacket(Player.Hand.OFF));
        // Restore the item in offhand
        sendPacket(new SetSlotPacket((byte) 0, 0, (short) PlayerInventoryUtils.OFFHAND_SLOT, getItemInOffHand()));
    }

    @Override public boolean isImmune(@NotNull DamageType type) {
        if (!getGameMode().canTakeDamage()) {
            return type != DamageType.VOID;
        }
        return super.isImmune(type);
    }

    @Override public void setHealth(float health) {
        super.setHealth(health);
        sendPacket(new UpdateHealthPacket(health, food, foodSaturation));
    }

    @Override public @NotNull PlayerMeta getEntityMeta() {
        return super.getEntityMeta();
    }

    /**
     * Gets the player additional hearts.
     *
     * @return the player additional hearts
     */
    @Override public float getAdditionalHearts() {
        return getEntityMeta().getAdditionalHearts();
    }

    /**
     * Changes the amount of additional hearts shown.
     *
     * @param additionalHearts the count of additional hearts
     */
    @Override public void setAdditionalHearts(float additionalHearts) {
        getEntityMeta().setAdditionalHearts(additionalHearts);
    }

    /**
     * Gets the player food.
     *
     * @return the player food
     */
    @Override public int getFood() {
        return food;
    }

    /**
     * Sets and refresh client food bar.
     *
     * @param food the new food value
     * @throws IllegalArgumentException if {@code food} is not between 0 and 20
     */
    @Override public void setFood(int food) {
        Check.argCondition(!MathUtils.isBetween(food, 0, 20), "Food has to be between 0 and 20");
        this.food = food;
        sendPacket(new UpdateHealthPacket(getHealth(), food, foodSaturation));
    }

    @Override public float getFoodSaturation() {
        return foodSaturation;
    }

    /**
     * Sets and refresh client food saturation.
     *
     * @param foodSaturation the food saturation
     * @throws IllegalArgumentException if {@code foodSaturation} is not between 0 and 20
     */
    @Override public void setFoodSaturation(float foodSaturation) {
        Check.argCondition(!MathUtils.isBetween(foodSaturation, 0, 20), "Food saturation has to be between 0 and 20");
        this.foodSaturation = foodSaturation;
        sendPacket(new UpdateHealthPacket(getHealth(), food, foodSaturation));
    }

    /**
     * Gets if the player is eating.
     *
     * @return true if the player is eating, false otherwise
     */
    @Override public boolean isEating() {
        return eatingHand != null;
    }

    /**
     * Gets the hand which the player is eating from.
     *
     * @return the eating hand, null if none
     */
    @Override public @Nullable Player.Hand getEatingHand() {
        return eatingHand;
    }

    /**
     * Gets the player default eating time.
     *
     * @return the player default eating time
     */
    @Override public long getDefaultEatingTime() {
        return defaultEatingTime;
    }

    /**
     * Used to change the default eating time animation.
     *
     * @param defaultEatingTime the default eating time in milliseconds
     */
    @Override public void setDefaultEatingTime(long defaultEatingTime) {
        this.defaultEatingTime = defaultEatingTime;
    }

    /**
     * Gets the player display name in the tab-list.
     *
     * @return the player display name, null means that {@link #getUsername()} is displayed
     */
    @Override public @Nullable Component getDisplayName() {
        return displayName;
    }

    /**
     * Changes the player display name in the tab-list.
     * <p>
     * Sets to null to show the player username.
     *
     * @param displayName the display name, null to display the username
     */
    @Override public void setDisplayName(@Nullable Component displayName) {
        this.displayName = displayName;
        PacketUtils.broadcastPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, infoEntry()));
    }

    /**
     * Gets the player skin.
     *
     * @return the player skin object,
     * null means that the player has his {@link #getUuid()} default skin
     */
    @Override public @Nullable PlayerSkin getSkin() {
        return skin;
    }

    /**
     * Changes the player skin.
     * <p>
     * This does remove the player for all viewers to spawn it again with the correct new skin.
     *
     * @param skin the player skin, null to reset it to his {@link #getUuid()} default skin
     * @see PlayerSkinInitEvent if you want to apply the skin at connection
     */
    @Override public synchronized void setSkin(@Nullable PlayerSkin skin) {
        this.skin = skin;
        if (instance == null) return;

        DestroyEntitiesPacket destroyEntitiesPacket = new DestroyEntitiesPacket(getEntityId());

        final PlayerInfoRemovePacket removePlayerPacket = getRemovePlayerToList();
        final PlayerInfoUpdatePacket addPlayerPacket = getAddPlayerToList();

        RespawnPacket respawnPacket = new RespawnPacket(getDimensionType().toString(), getDimensionType()
                .getName()
                .asString(), 0, gameMode, gameMode, false, levelFlat, true, deathLocation);

        sendPacket(removePlayerPacket);
        sendPacket(destroyEntitiesPacket);
        sendPacket(addPlayerPacket);
        sendPacket(respawnPacket);
        refreshClientStateAfterRespawn();

        {
            // Remove player
            PacketUtils.broadcastPacket(removePlayerPacket);
            sendPacketToViewers(destroyEntitiesPacket);

            // Show player again
            PacketUtils.broadcastPacket(addPlayerPacket);
            getViewers().forEach(player -> showPlayer(player.getPlayerConnection()));
        }

        getInventory().update();
        teleport(getPosition());
    }

    @Override public void setDeathLocation(@NotNull DimensionType type, @NotNull Pos position) {
        this.deathLocation = new DeathLocation(type.getName().asString(), position);
    }

    @Override public @Nullable DeathLocation getDeathLocation() {
        return this.deathLocation;
    }

    /**
     * Gets if the player has the respawn screen enabled or disabled.
     *
     * @return true if the player has the respawn screen, false if he didn't
     */
    @Override public boolean isEnableRespawnScreen() {
        return enableRespawnScreen;
    }

    /**
     * Enables or disable the respawn screen.
     *
     * @param enableRespawnScreen true to enable the respawn screen, false to disable it
     */
    @Override public void setEnableRespawnScreen(boolean enableRespawnScreen) {
        this.enableRespawnScreen = enableRespawnScreen;
        sendPacket(new ChangeGameStatePacket(ChangeGameStatePacket.Reason.ENABLE_RESPAWN_SCREEN, enableRespawnScreen ? 0 : 1));
    }

    /**
     * Gets the player's name as a component. This will either return the display name
     * (if set) or a component holding the username.
     *
     * @return the name
     */
    @Override public @NotNull Component getName() {
        return Objects.requireNonNullElse(displayName, usernameComponent);
    }

    /**
     * Gets the player's username.
     *
     * @return the player's username
     */
    @Override public @NotNull String getUsername() {
        return username;
    }

    /**
     * Changes the internal player name, used for the {@link AsyncPlayerPreLoginEvent}
     * mostly unsafe outside of it.
     *
     * @param username the new player name
     */
    @Override public void setUsernameField(@NotNull String username) {
        this.username = username;
        this.usernameComponent = Component.text(username);
    }

    /**
     * Calls an {@link ItemDropEvent} with a specified item.
     * <p>
     * Returns false if {@code item} is air.
     *
     * @param item the item to drop
     * @return true if player can drop the item (event not cancelled), false otherwise
     */
    @Override public boolean dropItem(@NotNull ItemStack item) {
        if (item.isAir()) return false;
        ItemDropEvent itemDropEvent = new ItemDropEvent(this, item);
        EventDispatcher.call(itemDropEvent);
        return !itemDropEvent.isCancelled();
    }

    /**
     * Sets the player resource pack.
     *
     * @param resourcePack the resource pack
     */
    @Override public void setResourcePack(@NotNull ResourcePack resourcePack) {
        sendPacket(new ResourcePackSendPacket(resourcePack));
    }

    /**
     * Rotates the player to face {@code targetPosition}.
     *
     * @param facePoint      the point from where the player should aim
     * @param targetPosition the target position to face
     */
    @Override public void facePosition(@NotNull Player.FacePoint facePoint, @NotNull Point targetPosition) {
        facePosition(facePoint, targetPosition, null, null);
    }

    /**
     * Rotates the player to face {@code entity}.
     *
     * @param facePoint   the point from where the player should aim
     * @param entity      the entity to face
     * @param targetPoint the point to aim at {@code entity} position
     */
    @Override public void facePosition(@NotNull Player.FacePoint facePoint, Entity entity, Player.FacePoint targetPoint) {
        facePosition(facePoint, entity.getPosition(), entity, targetPoint);
    }

    private void facePosition(@NotNull Player.FacePoint facePoint, @NotNull Point targetPosition, @Nullable Entity entity, @Nullable Player.FacePoint targetPoint) {
        final int entityId = entity != null ? entity.getEntityId() : 0;
        sendPacket(new FacePlayerPacket(facePoint == Player.FacePoint.EYE ? FacePlayerPacket.FacePosition.EYES : FacePlayerPacket.FacePosition.FEET, targetPosition, entityId, targetPoint == Player.FacePoint.EYE ? FacePlayerPacket.FacePosition.EYES : FacePlayerPacket.FacePosition.FEET));
    }

    /**
     * Sets the camera at {@code entity} eyes.
     *
     * @param entity the entity to spectate
     */
    @Override public void spectate(@NotNull Entity entity) {
        sendPacket(new CameraPacket(entity));
    }

    /**
     * Resets the camera at the player.
     */
    @Override public void stopSpectating() {
        spectate(this);
    }

    /**
     * Used to retrieve the default spawn point.
     * <p>
     * Can be altered by the {@link PlayerRespawnEvent#setRespawnPosition(Pos)}.
     *
     * @return a copy of the default respawn point
     */
    @Override public @NotNull Pos getRespawnPoint() {
        return respawnPoint;
    }

    /**
     * Changes the default spawn point.
     *
     * @param respawnPoint the player respawn point
     */
    @Override public void setRespawnPoint(@NotNull Pos respawnPoint) {
        this.respawnPoint = respawnPoint;
    }

    /**
     * Called after the player teleportation to refresh his position
     * and send data to his new viewers.
     */
    @Override protected void refreshAfterTeleport() {
        sendPacketsToViewers(getEntityType().registry().spawnType().getSpawnPacket(this));

        // Update for viewers
        sendPacketToViewersAndSelf(getVelocityPacket());
        sendPacketToViewersAndSelf(getMetadataPacket());
        sendPacketToViewersAndSelf(getPropertiesPacket());
        sendPacketToViewersAndSelf(getEquipmentsPacket());

        getInventory().update();
    }

    /**
     * Sets the player food and health values to their maximum.
     */
    @Override protected void refreshHealth() {
        this.food = 20;
        this.foodSaturation = 5;
        // refresh health and send health packet
        heal();
    }

    /**
     * Gets the percentage displayed in the experience bar.
     *
     * @return the exp percentage 0-1
     */
    @Override public float getExp() {
        return exp;
    }

    /**
     * Used to change the percentage experience bar.
     * This cannot change the displayed level, see {@link #setLevel(int)}.
     *
     * @param exp a percentage between 0 and 1
     * @throws IllegalArgumentException if {@code exp} is not between 0 and 1
     */
    @Override public void setExp(float exp) {
        Check.argCondition(!MathUtils.isBetween(exp, 0, 1), "Exp should be between 0 and 1");
        this.exp = exp;
        sendPacket(new SetExperiencePacket(exp, level, 0));
    }

    /**
     * Gets the level of the player displayed in the experience bar.
     *
     * @return the player level
     */
    @Override public int getLevel() {
        return level;
    }

    /**
     * Used to change the level of the player
     * This cannot change the displayed percentage bar see {@link #setExp(float)}
     *
     * @param level the new level of the player
     */
    @Override public void setLevel(int level) {
        this.level = level;
        sendPacket(new SetExperiencePacket(exp, level, 0));
    }

    /**
     * Gets the player connection.
     * <p>
     * Used to send packets and get stuff related to the connection.
     *
     * @return the player connection
     */
    @Override public @NotNull PlayerConnection getPlayerConnection() {
        return playerConnection;
    }

    /**
     * Shortcut for {@link PlayerConnection#sendPacket(SendablePacket)}.
     *
     * @param packet the packet to send
     */
    @Override @ApiStatus.Experimental public void sendPacket(@NotNull SendablePacket packet) {
        this.playerConnection.sendPacket(packet);
    }

    @Override @ApiStatus.Experimental public void sendPackets(@NotNull SendablePacket... packets) {
        this.playerConnection.sendPackets(packets);
    }

    @Override @ApiStatus.Experimental public void sendPackets(@NotNull Collection<SendablePacket> packets) {
        this.playerConnection.sendPackets(packets);
    }

    /**
     * Gets if the player is online or not.
     *
     * @return true if the player is online, false otherwise
     */
    @Override public boolean isOnline() {
        return playerConnection.isOnline();
    }

    /**
     * Gets the player settings.
     *
     * @return the player settings
     */
    @Override public @NotNull Player.PlayerSettings getSettings() {
        return settings;
    }

    /**
     * Gets the player dimension.
     *
     * @return the player current dimension
     */
    @Override public DimensionType getDimensionType() {
        return dimensionType;
    }

    @Override public @NotNull PlayerInventory getInventory() {
        return inventory;
    }

    /**
     * Used to get the player latency,
     * computed by seeing how long it takes the client to answer the {@link KeepAlivePacket} packet.
     *
     * @return the player latency
     */
    @Override public int getLatency() {
        return latency;
    }

    /**
     * Gets the player {@link GameMode}.
     *
     * @return the player current gamemode
     */
    @Override public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Changes the player {@link GameMode}
     *
     * @param gameMode the new player GameMode
     */
    @Override public void setGameMode(@NotNull GameMode gameMode) {
        this.gameMode = gameMode;
        // Condition to prevent sending the packets before spawning the player
        if (isActive()) {
            sendPacket(new ChangeGameStatePacket(ChangeGameStatePacket.Reason.CHANGE_GAMEMODE, gameMode.id()));
            PacketUtils.broadcastPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, infoEntry()));
        }

        // The client updates their abilities based on the GameMode as follows
        switch (gameMode) {
            case CREATIVE -> {
                this.allowFlying = true;
                this.instantBreak = true;
                this.invulnerable = true;
            }
            case SPECTATOR -> {
                this.allowFlying = true;
                this.instantBreak = false;
                this.invulnerable = true;
                this.flying = true;
            }
            default -> {
                this.allowFlying = false;
                this.instantBreak = false;
                this.invulnerable = false;
                this.flying = false;
            }
        }
        // Make sure that the player is in the PLAY state and synchronize their flight speed.
        if (isActive()) {
            refreshAbilities();
        }
    }

    /**
     * Gets if this player is in creative. Used for code readability.
     *
     * @return true if the player is in creative mode
     */
    @Override public boolean isCreative() {
        return gameMode == GameMode.CREATIVE;
    }

    /**
     * Changes the dimension of the player.
     * Mostly unsafe since it requires sending chunks after.
     *
     * @param dimensionType the new player dimension
     */
    @Override protected void sendDimension(@NotNull DimensionType dimensionType) {
        Check.argCondition(dimensionType.equals(getDimensionType()), "The dimension needs to be different than the current one!");
        this.dimensionType = dimensionType;
        sendPacket(new RespawnPacket(dimensionType.toString(), getDimensionType()
                .getName()
                .asString(), 0, gameMode, gameMode, false, levelFlat, true, deathLocation));
        refreshClientStateAfterRespawn();
    }

    /**
     * Kicks the player with a reason.
     *
     * @param component the reason
     */
    @Override public void kick(@NotNull Component component) {
        final ConnectionState connectionState = playerConnection.getConnectionState();
        // Packet type depends on the current player connection state
        final ServerPacket disconnectPacket;
        if (connectionState == ConnectionState.LOGIN) {
            disconnectPacket = new LoginDisconnectPacket(component);
        } else {
            disconnectPacket = new DisconnectPacket(component);
        }
        sendPacket(disconnectPacket);
        playerConnection.disconnect();
    }

    /**
     * Kicks the player with a reason.
     *
     * @param message the kick reason
     */
    @Override public void kick(@NotNull String message) {
        this.kick(Component.text(message));
    }

    /**
     * Changes the current held slot for the player.
     *
     * @param slot the slot that the player has to held
     * @throws IllegalArgumentException if {@code slot} is not between 0 and 8
     */
    @Override public void setHeldItemSlot(byte slot) {
        Check.argCondition(!MathUtils.isBetween(slot, 0, 8), "Slot has to be between 0 and 8");
        refreshHeldSlot(slot);
        sendPacket(new HeldItemChangePacket(slot));
    }

    /**
     * Gets the player held slot (0-8).
     *
     * @return the current held slot for the player
     */
    @Override public byte getHeldSlot() {
        return heldSlot;
    }

    @Override public void setTeam(Team team) {
        super.setTeam(team);
        if (team != null) {
            PacketUtils.broadcastPacket(team.createTeamsCreationPacket());
        }
    }

    /**
     * Changes the tag below the name.
     *
     * @param belowNameTag The new below name tag
     */
    @Override public void setBelowNameTag(BelowNameTag belowNameTag) {
        if (this.belowNameTag == belowNameTag) return;

        if (this.belowNameTag != null) {
            this.belowNameTag.removeViewer(this);
        }

        this.belowNameTag = belowNameTag;
    }

    /**
     * Gets the player open inventory.
     *
     * @return the currently open inventory, null if there is not (player inventory is not detected)
     */
    @Override public @Nullable Inventory getOpenInventory() {
        return openInventory;
    }

    /**
     * Opens the specified Inventory, close the previous inventory if existing.
     *
     * @param inventory the inventory to open
     * @return true if the inventory has been opened/sent to the player, false otherwise (cancelled by event)
     */
    @Override public boolean openInventory(@NotNull Inventory inventory) {
        InventoryOpenEvent inventoryOpenEvent = new InventoryOpenEvent(inventory, this);

        EventDispatcher.callCancellable(inventoryOpenEvent, () -> {
            Inventory openInventory = getOpenInventory();
            if (openInventory != null) {
                openInventory.removeViewer(this);
            }

            Inventory newInventory = inventoryOpenEvent.getInventory();
            if (newInventory == null) {
                // just close the inventory
                return;
            }

            sendPacket(new OpenWindowPacket(newInventory.getWindowId(), newInventory
                    .getInventoryType()
                    .getWindowType(), newInventory.getTitle()));
            newInventory.addViewer(this);
            this.openInventory = newInventory;
        });
        return !inventoryOpenEvent.isCancelled();
    }

    /**
     * Closes the current inventory if there is any.
     * It closes the player inventory (when opened) if {@link #getOpenInventory()} returns null.
     */
    @Override public void closeInventory() {
        Inventory openInventory = getOpenInventory();

        // Drop cursor item when closing inventory
        ItemStack cursorItem;
        if (openInventory == null) {
            cursorItem = getInventory().getCursorItem();
            getInventory().setCursorItem(ItemStack.AIR);
        } else {
            cursorItem = openInventory.getCursorItem(this);
            openInventory.setCursorItem(this, ItemStack.AIR);
        }
        if (!cursorItem.isAir()) {
            // Add item to inventory if he hasn't been able to drop it
            if (!dropItem(cursorItem)) {
                getInventory().addItemStack(cursorItem);
            }
        }

        if (openInventory == getOpenInventory()) {
            CloseWindowPacket closeWindowPacket;
            if (openInventory == null) {
                closeWindowPacket = new CloseWindowPacket((byte) 0);
            } else {
                closeWindowPacket = new CloseWindowPacket(openInventory.getWindowId());
                openInventory.removeViewer(this); // Clear cache
                this.openInventory = null;
            }
            sendPacket(closeWindowPacket);
            inventory.update();
            this.didCloseInventory = true;
        }
    }

    /**
     * Used internally to prevent an inventory click to be processed
     * when the inventory listeners closed the inventory.
     * <p>
     * Should only be used within an inventory listener (event or condition).
     *
     * @return true if the inventory has been closed, false otherwise
     */
    @Override public boolean didCloseInventory() {
        return didCloseInventory;
    }

    /**
     * Used internally to reset the didCloseInventory field.
     * <p>
     * Shouldn't be used externally without proper understanding of its consequence.
     *
     * @param didCloseInventory the new didCloseInventory field
     */
    @Override public void UNSAFE_changeDidCloseInventory(boolean didCloseInventory) {
        this.didCloseInventory = didCloseInventory;
    }

    @Override public int getNextTeleportId() {
        return teleportId.incrementAndGet();
    }

    @Override public int getLastSentTeleportId() {
        return teleportId.get();
    }

    @Override public int getLastReceivedTeleportId() {
        return receivedTeleportId;
    }

    @Override public void refreshReceivedTeleportId(int receivedTeleportId) {
        this.receivedTeleportId = receivedTeleportId;
    }

    /**
     * @see Entity#synchronizePosition(boolean)
     */
    @Override @ApiStatus.Internal protected void synchronizePosition(boolean includeSelf) {
        if (includeSelf) {
            sendPacket(new PlayerPositionAndLookPacket(position, (byte) 0x00, getNextTeleportId(), false));
        }
        super.synchronizePosition(includeSelf);
    }

    /**
     * Gets the player permission level.
     *
     * @return the player permission level
     */
    @Override public int getPermissionLevel() {
        return permissionLevel;
    }

    /**
     * Changes the player permission level.
     *
     * @param permissionLevel the new player permission level
     * @throws IllegalArgumentException if {@code permissionLevel} is not between 0 and 4
     */
    @Override public void setPermissionLevel(int permissionLevel) {
        Check.argCondition(!MathUtils.isBetween(permissionLevel, 0, 4), "permissionLevel has to be between 0 and 4");

        this.permissionLevel = permissionLevel;

        // Condition to prevent sending the packets before spawning the player
        if (isActive()) {
            // Magic values: https://wiki.vg/Entity_statuses#Player
            // TODO remove magic values
            final byte permissionLevelStatus = (byte) (24 + permissionLevel);
            triggerStatus(permissionLevelStatus);
        }
    }

    /**
     * Sets or remove the reduced debug screen.
     *
     * @param reduced should the player has the reduced debug screen
     */
    @Override public void setReducedDebugScreenInformation(boolean reduced) {
        this.reducedDebugScreenInformation = reduced;

        // Magic values: https://wiki.vg/Entity_statuses#Player
        // TODO remove magic values
        final byte debugScreenStatus = (byte) (reduced ? 22 : 23);
        triggerStatus(debugScreenStatus);
    }

    /**
     * Gets if the player has the reduced debug screen.
     *
     * @return true if the player has the reduced debug screen, false otherwise
     */
    @Override public boolean hasReducedDebugScreenInformation() {
        return reducedDebugScreenInformation;
    }

    /**
     * The invulnerable field appear in the {@link PlayerAbilitiesPacket} packet.
     *
     * @return true if the player is invulnerable, false otherwise
     */
    @Override public boolean isInvulnerable() {
        return super.isInvulnerable();
    }

    /**
     * This do update the {@code invulnerable} field in the packet {@link PlayerAbilitiesPacket}
     * and prevent the player from receiving damage.
     *
     * @param invulnerable should the player be invulnerable
     */
    @Override public void setInvulnerable(boolean invulnerable) {
        super.setInvulnerable(invulnerable);
        refreshAbilities();
    }

    @Override public void setSneaking(boolean sneaking) {
        if (isFlying()) { //If we are flying, don't set the players pose to sneaking as this can clip them through blocks
            this.entityMeta.setSneaking(sneaking);
        } else {
            super.setSneaking(sneaking);
        }
    }

    /**
     * Gets if the player is currently flying.
     *
     * @return true if the player if flying, false otherwise
     */
    @Override public boolean isFlying() {
        return flying;
    }

    /**
     * Sets the player flying.
     *
     * @param flying should the player fly
     */
    @Override public void setFlying(boolean flying) {
        refreshFlying(flying);
        refreshAbilities();
    }

    /**
     * Updates the internal flying field.
     * <p>
     * Mostly unsafe since there is nothing to backup the value, used internally for creative players.
     *
     * @param flying the new flying field
     * @see #setFlying(boolean) instead
     */
    @Override public void refreshFlying(boolean flying) {
        //When the player starts or stops flying, their pose needs to change
        if (this.flying != flying) {
            Pose pose = getPose();

            if (this.isSneaking() && pose == Pose.STANDING) {
                setPose(Pose.SNEAKING);
            } else if (pose == Pose.SNEAKING) {
                setPose(Pose.STANDING);
            }
        }

        this.flying = flying;
    }

    /**
     * Gets if the player is allowed to fly.
     *
     * @return true if the player if allowed to fly, false otherwise
     */
    @Override public boolean isAllowFlying() {
        return allowFlying;
    }

    /**
     * Allows or forbid the player to fly.
     *
     * @param allowFlying should the player be allowed to fly
     */
    @Override public void setAllowFlying(boolean allowFlying) {
        this.allowFlying = allowFlying;
        refreshAbilities();
    }

    @Override public boolean isInstantBreak() {
        return instantBreak;
    }

    /**
     * Changes the player ability "Creative Mode".
     *
     * @param instantBreak true to allow instant break
     * @see <a href="https://wiki.vg/Protocol#Player_Abilities_.28clientbound.29">player abilities</a>
     */
    @Override public void setInstantBreak(boolean instantBreak) {
        this.instantBreak = instantBreak;
        refreshAbilities();
    }

    /**
     * Gets the player flying speed.
     *
     * @return the flying speed of the player
     */
    @Override public float getFlyingSpeed() {
        return flyingSpeed;
    }

    /**
     * Updates the internal field and send a {@link PlayerAbilitiesPacket} with the new flying speed.
     *
     * @param flyingSpeed the new flying speed of the player
     */
    @Override public void setFlyingSpeed(float flyingSpeed) {
        this.flyingSpeed = flyingSpeed;
        refreshAbilities();
    }

    @Override public float getFieldViewModifier() {
        return fieldViewModifier;
    }

    @Override public void setFieldViewModifier(float fieldViewModifier) {
        this.fieldViewModifier = fieldViewModifier;
        refreshAbilities();
    }

    /**
     * This is the map used to send the statistic packet.
     * It is possible to add/remove/change statistic value directly into it.
     *
     * @return the modifiable statistic map
     */
    @Override public @NotNull Map<PlayerStatistic, Integer> getStatisticValueMap() {
        return statisticValueMap;
    }

    /**
     * Gets the player vehicle information.
     *
     * @return the player vehicle information
     */
    @Override public @NotNull PlayerVehicleInformation getVehicleInformation() {
        return vehicleInformation;
    }

    /**
     * Sends to the player a {@link PlayerAbilitiesPacket} with all the updated fields.
     */
    @Override protected void refreshAbilities() {
        byte flags = 0;
        if (invulnerable) flags |= PlayerAbilitiesPacket.FLAG_INVULNERABLE;
        if (flying) flags |= PlayerAbilitiesPacket.FLAG_FLYING;
        if (allowFlying) flags |= PlayerAbilitiesPacket.FLAG_ALLOW_FLYING;
        if (instantBreak) flags |= PlayerAbilitiesPacket.FLAG_INSTANT_BREAK;
        sendPacket(new PlayerAbilitiesPacket(flags, flyingSpeed, fieldViewModifier));
    }

    /**
     * All packets in the queue are executed in the {@link #update(long)} method
     * It is used internally to add all received packet from the client.
     * Could be used to "simulate" a received packet, but to use at your own risk.
     *
     * @param packet the packet to add in the queue
     */
    @Override public void addPacketToQueue(@NotNull ClientPacket packet) {
        this.packets.offer(packet);
    }

    @Override @ApiStatus.Internal @ApiStatus.Experimental public void interpretPacketQueue() {
        if (this.packets.size() >= PACKET_QUEUE_SIZE) {
            kick(Component.text("Too Many Packets", NamedTextColor.RED));
            return;
        }
        final PacketListenerManager manager = MinecraftServer.getPacketListenerManager();
        // This method is NOT thread-safe
        this.packets.drain(packet -> manager.processClientPacket(packet, this), PACKET_PER_TICK);
    }

    /**
     * Changes the storage player latency and update its tab value.
     *
     * @param latency the new player latency
     */
    @Override public void refreshLatency(int latency) {
        this.latency = latency;
        PacketUtils.broadcastPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.UPDATE_LATENCY, infoEntry()));
    }

    @Override public void refreshOnGround(boolean onGround) {
        this.onGround = onGround;
        if (this.onGround && this.isFlyingWithElytra()) {
            this.setFlyingWithElytra(false);
            EventDispatcher.call(new PlayerStopFlyingWithElytraEvent(this));
        }
    }

    /**
     * Used to change internally the last sent last keep alive id.
     * <p>
     * Warning: could lead to have the player kicked because of a wrong keep alive packet.
     *
     * @param lastKeepAlive the new lastKeepAlive id
     */
    @Override public void refreshKeepAlive(long lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
        this.answerKeepAlive = false;
    }

    @Override public boolean didAnswerKeepAlive() {
        return answerKeepAlive;
    }

    @Override public void refreshAnswerKeepAlive(boolean answerKeepAlive) {
        this.answerKeepAlive = answerKeepAlive;
    }

    /**
     * Changes the held item for the player viewers
     * Also cancel eating if {@link #isEating()} was true.
     * <p>
     * Warning: the player will not be noticed by this chance, only his viewers,
     * see instead: {@link #setHeldItemSlot(byte)}.
     *
     * @param slot the new held slot
     */
    @Override public void refreshHeldSlot(byte slot) {
        this.heldSlot = slot;
        syncEquipment(EquipmentSlot.MAIN_HAND);
        refreshEating(null);
    }

    @Override public void refreshEating(@Nullable Player.Hand eatingHand, long eatingTime) {
        this.eatingHand = eatingHand;
        if (eatingHand != null) {
            this.startEatingTime = System.currentTimeMillis();
            this.eatingTime = eatingTime;
        } else {
            this.startEatingTime = 0;
        }
    }

    @Override public void refreshEating(@Nullable Player.Hand eatingHand) {
        refreshEating(eatingHand, defaultEatingTime);
    }

    /**
     * Used to call {@link ItemUpdateStateEvent} with the proper item
     * It does check which hand to get the item to update.
     *
     * @param allowFood true if food should be updated, false otherwise
     * @return the called {@link ItemUpdateStateEvent},
     * null if there is no item to update the state
     * @deprecated Use {@link #callItemUpdateStateEvent(Player.Hand)} instead
     */
    @Override @Deprecated public @Nullable ItemUpdateStateEvent callItemUpdateStateEvent(boolean allowFood, @Nullable Player.Hand hand) {
        if (hand == null) return null;

        final ItemStack updatedItem = getItemInHand(hand);
        final boolean isFood = updatedItem.material().isFood();

        if (isFood && !allowFood) return null;

        ItemUpdateStateEvent itemUpdateStateEvent = new ItemUpdateStateEvent(this, hand, updatedItem);
        EventDispatcher.call(itemUpdateStateEvent);

        return itemUpdateStateEvent;
    }

    /**
     * Used to call {@link ItemUpdateStateEvent} with the proper item
     * It does check which hand to get the item to update. Allows food.
     *
     * @return the called {@link ItemUpdateStateEvent},
     * null if there is no item to update the state
     */
    @Override public @Nullable ItemUpdateStateEvent callItemUpdateStateEvent(@Nullable Player.Hand hand) {
        return callItemUpdateStateEvent(true, hand);
    }

    @Override public void refreshVehicleSteer(float sideways, float forward, boolean jump, boolean unmount) {
        this.vehicleInformation.refresh(sideways, forward, jump, unmount);
    }

    /**
     * Gets the last sent keep alive id.
     *
     * @return the last keep alive id sent to the player
     */
    @Override public long getLastKeepAlive() {
        return lastKeepAlive;
    }

    @Override public @NotNull HoverEvent<HoverEvent.ShowEntity> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowEntity> op) {
        return HoverEvent.showEntity(HoverEvent.ShowEntity.of(EntityType.PLAYER, this.uuid, this.displayName));
    }

    /**
     * Gets the packet to add the player from the tab-list.
     *
     * @return a {@link PlayerInfoUpdatePacket} to add the player
     */
    @Override protected @NotNull PlayerInfoUpdatePacket getAddPlayerToList() {
        return new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(infoEntry()));
    }

    /**
     * Gets the packet to remove the player from the tab-list.
     *
     * @return a {@link PlayerInfoRemovePacket} to remove the player
     */
    @Override protected @NotNull PlayerInfoRemovePacket getRemovePlayerToList() {
        return new PlayerInfoRemovePacket(getUuid());
    }

    private PlayerInfoUpdatePacket.Entry infoEntry() {
        final PlayerSkin skin = this.skin;
        List<PlayerInfoUpdatePacket.Property> prop = skin != null ? List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())) : List.of();
        return new PlayerInfoUpdatePacket.Entry(getUuid(), getUsername(), prop, true, getLatency(), getGameMode(), displayName, null);
    }

    /**
     * Sends all the related packet to have the player sent to another with related data
     * (create player, spawn position, velocity, metadata, equipments, passengers, team).
     * <p>
     * WARNING: this alone does not sync the player, please use {@link #addViewer(Player)}.
     *
     * @param connection the connection to show the player to
     */
    @Override protected void showPlayer(@NotNull PlayerConnection connection) {
        connection.sendPacket(getEntityType().registry().spawnType().getSpawnPacket(this));
        connection.sendPacket(getVelocityPacket());
        connection.sendPacket(getMetadataPacket());
        connection.sendPacket(getEquipmentsPacket());
        if (hasPassenger()) {
            connection.sendPacket(getPassengersPacket());
        }
        // Team
        if (this.getTeam() != null) {
            connection.sendPacket(this.getTeam().createTeamsCreationPacket());
        }
        connection.sendPacket(new EntityHeadLookPacket(getEntityId(), position.yaw()));
    }

    @Override public @NotNull ItemStack getItemInMainHand() {
        return inventory.getItemInMainHand();
    }

    @Override public void setItemInMainHand(@NotNull ItemStack itemStack) {
        inventory.setItemInMainHand(itemStack);
    }

    @Override public @NotNull ItemStack getItemInOffHand() {
        return inventory.getItemInOffHand();
    }

    @Override public void setItemInOffHand(@NotNull ItemStack itemStack) {
        inventory.setItemInOffHand(itemStack);
    }

    @Override public @NotNull ItemStack getHelmet() {
        return inventory.getHelmet();
    }

    @Override public void setHelmet(@NotNull ItemStack itemStack) {
        inventory.setHelmet(itemStack);
    }

    @Override public @NotNull ItemStack getChestplate() {
        return inventory.getChestplate();
    }

    @Override public void setChestplate(@NotNull ItemStack itemStack) {
        inventory.setChestplate(itemStack);
    }

    @Override public @NotNull ItemStack getLeggings() {
        return inventory.getLeggings();
    }

    @Override public void setLeggings(@NotNull ItemStack itemStack) {
        inventory.setLeggings(itemStack);
    }

    @Override public @NotNull ItemStack getBoots() {
        return inventory.getBoots();
    }

    @Override public void setBoots(@NotNull ItemStack itemStack) {
        inventory.setBoots(itemStack);
    }

    @Override public @NotNull PlayerSnapshot updateSnapshot(@NotNull SnapshotUpdater updater) {
        final EntitySnapshot snapshot = super.updateSnapshot(updater);
        return new SnapshotImpl.Player(snapshot, username, gameMode);
    }

    @Override public @NotNull Identity identity() {
        return this.identity;
    }

    @Override public @NotNull Pointers pointers() {
        return this.pointers;
    }

    @Override public void setUuid(@NotNull UUID uuid) {
        super.setUuid(uuid);
        // update identity
        this.identity = Identity.identity(uuid);
    }

    @Override protected void sendChunkUpdates(Chunk newChunk) {
        if (chunkUpdateLimitChecker.addToHistory(newChunk)) {
            final int newX = newChunk.getChunkX();
            final int newZ = newChunk.getChunkZ();
            final Vec old = chunksLoadedByClient;
            sendPacket(new UpdateViewPositionPacket(newX, newZ));
            ChunkUtils.forDifferingChunksInRange(newX, newZ, (int) old.x(), (int) old.z(), MinecraftServer.getChunkViewDistance(), chunkAdder, chunkRemover);
            this.chunksLoadedByClient = new Vec(newX, newZ);
        }
    }

    @Override public @NotNull CompletableFuture<Void> teleport(@NotNull Pos position, long @Nullable [] chunks) {
        chunkUpdateLimitChecker.clearHistory();
        return super.teleport(position, chunks);
    }

}