/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.world.EventDamageBlock;
import eu.darkcube.minigame.woolbattle.event.world.EventDestroyBlock;
import eu.darkcube.minigame.woolbattle.game.ingame.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.*;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Ingame extends GamePhase {

    public final Map<WBUser, Team> lastTeam = new HashMap<>();
    private final WoolBattleBukkit woolbattle;
    private final PlayerUtil playerUtil;
    private final TeamSplitter teamSplitter;
    public Set<Block> placedBlocks = new HashSet<>();
    public Map<Block, DyeColor> breakedWool = new HashMap<>();
    public Map<WBUser, Integer> killstreak = new HashMap<>();
    public int killsFor1Life = 5;
    public Team winner;
    private boolean globalSpawnProtection = false;
    private int maxBlockArrowHits;
    private int spawnprotectionTicks;
    private int spawnprotectionTicksGlobal;
    private boolean startingIngame = false;

    public Ingame(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.teamSplitter = new TeamSplitter(this);
        this.playerUtil = new PlayerUtil(this);
        maxBlockArrowHits = woolbattle.getConfig("config").getInt("maxblockarrowhits");
        spawnprotectionTicks = woolbattle.getConfig("config").getInt("spawnprotectionticks");
        spawnprotectionTicksGlobal = woolbattle.getConfig("config").getInt("spawnprotectionticksglobal");
        addListener(new ListenerItemPickup(), new ListenerItemDrop(), new ListenerBlockBreak(woolbattle), new ListenerBlockPlace(woolbattle), new ListenerBlockCanBuild(woolbattle), new ListenerPlayerJoin(this), new ListenerPlayerQuit(woolbattle), new ListenerPlayerLogin(this), new ListenerEntityDamageByEntity(woolbattle), new ListenerEntityDamage(woolbattle), new ListenerChangeBlock(woolbattle), new ListenerInventoryClick(woolbattle), new ListenerInventoryDrag(), new ListenerInteract(woolbattle), new ListenerPlayerMove(woolbattle), new ListenerEntitySpawn(), new ListenerExplode(woolbattle), new ListenerDeathMove(woolbattle));

        addScheduler(new SchedulerParticles(woolbattle), new SchedulerSpawnProtection(this, woolbattle), new SchedulerResetWool(breakedWool, placedBlocks, woolbattle), new SchedulerHeightDisplay(woolbattle), new SchedulerTick(woolbattle), new SchedulerPerkCooldown(woolbattle));

    }

    public <T> T getMetaData(Block block, String key, T defaultValue) {
        if (block.hasMetadata("WoolBattleMetaStorage")) {
            BasicMetaDataStorage storage = (BasicMetaDataStorage) block.getMetadata("WoolBattleMetaStorage").get(0).value();
            return storage.getOr(new Key(woolbattle, key), defaultValue);
        }
        return defaultValue;
    }

    public void setMetaData(Block block, String key, Object value) {
        BasicMetaDataStorage storage;
        boolean has = block.hasMetadata("WoolBattleMetaStorage");
        if (has) {
            storage = (BasicMetaDataStorage) block.getMetadata("WoolBattleMetaStorage").get(0).value();
        } else {
            storage = new BasicMetaDataStorage();
        }
        Key k = new Key(woolbattle, key);
        if (value == null) {
            if (storage.has(k)) {
                storage.remove(k);
            }
        } else {
            storage.set(k, value);
        }
        if (storage.data.isEmpty()) {
            if (has) {
                block.removeMetadata("WoolBattleMetaStorage", woolbattle);
            }
        } else {
            if (!has) {
                block.setMetadata("WoolBattleMetaStorage", new FixedMetadataValue(woolbattle, storage));
            }
        }
    }

    public int getBlockDamage(Block block) {
        return getMetaData(block, "arrow_damage", 0);
    }

    public void resetBlockDamage(Block block) {
        setMetaData(block, "arrow_damage", null);
    }

    public void playSoundNotEnoughWool(WBUser user) {
        user.getBukkitEntity().playSound(user.getBukkitEntity().getLocation(), Sound.VILLAGER_NO, 1, 1);
    }

    public void setBlockDamage(Block block, int damage) {
        EventDamageBlock damageBlock = new EventDamageBlock(block, getBlockDamage(block), damage);
        if (damageBlock.isCancelled()) {
            return;
        }
        if (damage >= maxBlockArrowHits) {
            if (destroy(block)) {
                return;
            }
        }
        setMetaData(block, "arrow_damage", damage);
    }

    public boolean destroy(Block block) {
        return destroy(block, false);
    }

    public boolean destroy(Block block, boolean force) {
        if (!force && !placedBlocks.contains(block)) return false;
        EventDestroyBlock destroyBlock = new EventDestroyBlock(block);
        if (force || !destroyBlock.isCancelled()) {
            Ingame ingame = woolbattle.ingame();
            if (ingame.placedBlocks.contains(block)) {
                ingame.placedBlocks.remove(block);
            } else if (block.getType() == Material.WOOL) {
                ingame.breakedWool.put(block, ((Wool) block.getState().getData()).getColor());
            }

            removeMetaStorage(block);
            block.setType(Material.AIR);
            return true;
        }
        return false;
    }

    public void removeMetaStorage(Block block) {
        if (block.hasMetadata("WoolBattleMetaStorage")) {
            block.removeMetadata("WoolBattleMetaStorage", woolbattle);
        }
    }

    public Team getLastTeam(WBUser user) {
        return this.lastTeam.containsKey(user) ? this.lastTeam.get(user) : user.getTeam();
    }

    @Override public void onEnable() {
        killstreak.clear();
        this.startingIngame = true;

        woolbattle.lobbySystemLink().update();

        var map = woolbattle.gameData().map(); // TODO this should never be null
        if (map == null) map = woolbattle.mapManager().defaultRandomPersistentMap(woolbattle.gameData().mapSize());
        CompletableFuture<Void> future = woolbattle.mapLoader().loadMap(map);
        woolbattle.sendMessage(Message.STARTING_GAME);
        for (WBUser user : WBUser.onlineUsers()) {
            user.getBukkitEntity().setGameMode(GameMode.SPECTATOR);
            user.removeWool(user.woolCount());
            user.setKills(0);
            user.setDeaths(0);
            user.setLastHit(null);
            if (user.isTrollMode()) user.setTrollMode(false);
        }
        future.thenRun(() -> {
            try {
                postMapLoaded();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        future.exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }

    private void postMapLoaded() {
        teamSplitter.splitPlayers();

        int lifes = calculateLifes();
        for (Team team : woolbattle.teamManager().getTeams()) team.setLifes(lifes);

        if (this.placedBlocks != null) for (Block b : new ArrayList<>(this.placedBlocks)) destroy(b);

        this.globalSpawnProtection = true;

        playerUtil.setupPlayers();
        woolbattle.perkRegistry().perks().values().forEach(Perk::startLogic);
        this.startingIngame = false;
    }

    private int calculateLifes() {
        int lifes = -1;
        if (!woolbattle.lobby().VOTES_LIFES.isEmpty()) {
            Collection<Integer> list = woolbattle.lobby().VOTES_LIFES.values();
            double d = 0;
            for (int i : list) {
                d += i;
            }
            d /= list.size();
            lifes = (int) Math.round(d);
        }
        if (lifes == -1) {
            int xtra = 0;
            int playersize = Bukkit.getOnlinePlayers().size();
            if (playersize >= 3) {
                playersize = 3;
                xtra = new Random().nextInt(4);
            }
            xtra += playersize;
            lifes = 10 + xtra;
        }
        if (woolbattle.gameData().forceLifes() != -1) lifes = woolbattle.gameData().forceLifes();
        return lifes;
    }

    @Override public void onDisable() {

        woolbattle.perkRegistry().perks().values().forEach(Perk::stopLogic);

        if (this.placedBlocks != null) {
            for (Block b : new ArrayList<>(this.placedBlocks)) {
                destroy(b);
            }
        }

        woolbattle.mapLoader().unloadMap(woolbattle.gameData().map());
    }

    public boolean place(WBUser user, Block block) {
        return this.place(user, block, 0);
    }

    public boolean place(WBUser user, Block block, int damage) {
        return place(user, block, damage, true);
    }

    public boolean place(WBUser user, Block block, int damage, boolean useColor) {
        return place(block, b -> {
            block.setType(Material.WOOL);
            if (useColor) {
                BlockState state = block.getState();
                state.setType(Material.WOOL);
                Wool wool = (Wool) state.getData();
                wool.setColor(user.getTeam().getType().getWoolColor());
                state.setData(wool);
                state.update(true);
            }
            setBlockDamage(block, damage);
        });
    }

    public boolean place(Block block, Consumer<Block> placer) {
        return place(block, type -> type == Material.AIR, placer);
    }

    public boolean place(Block block, Predicate<Material> predicate, Consumer<Block> placer) {
        if (!predicate.test(block.getType())) {
            return false;
        }
        placedBlocks.add(block);
        placer.accept(block);
        return true;
    }

    public void checkGameEnd() {
        if (this.startingIngame) return;
        List<Team> teams = woolbattle.teamManager().getTeams().stream().filter(t -> !t.getUsers().isEmpty()).collect(Collectors.toList());
        if (teams.size() == 1) {
            this.winner = teams.get(0);
            for (WBUser i : this.winner.getUsers()) {
                StatsLink.addWin(i);
            }
            this.disable();
            woolbattle.endgame().enable();
        }
    }

    public int spawnprotectionTicks() {
        return spawnprotectionTicks;
    }

    public int spawnprotectionTicksGlobal() {
        return spawnprotectionTicksGlobal;
    }

    public int maxBlockArrowHits() {
        return maxBlockArrowHits;
    }

    public boolean startingIngame() {
        return startingIngame;
    }

    public PlayerUtil playerUtil() {
        return playerUtil;
    }

    public boolean globalSpawnProtection() {
        return globalSpawnProtection;
    }

    public void globalSpawnProtection(boolean globalSpawnProtection) {
        this.globalSpawnProtection = globalSpawnProtection;
    }

    public WoolBattleBukkit woolbattle() {
        return woolbattle;
    }
}
