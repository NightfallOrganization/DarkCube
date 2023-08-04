/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.user.EventUserAttackUser;
import eu.darkcube.minigame.woolbattle.event.user.EventUserKill;
import eu.darkcube.minigame.woolbattle.event.user.EventUserMayAttack;
import eu.darkcube.minigame.woolbattle.event.user.UserArmorSetEvent;
import eu.darkcube.minigame.woolbattle.event.world.EventDamageBlock;
import eu.darkcube.minigame.woolbattle.event.world.EventDestroyBlock;
import eu.darkcube.minigame.woolbattle.game.ingame.*;
import eu.darkcube.minigame.woolbattle.listener.ingame.*;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Ingame extends GamePhase {

    public final Map<WBUser, Team> lastTeam = new HashMap<>();
    private final WoolBattle woolbattle;
    public Set<Block> placedBlocks = new HashSet<>();
    public Map<Block, DyeColor> breakedWool = new HashMap<>();
    public Map<WBUser, Integer> killstreak = new HashMap<>();
    public int killsFor1Life = 5;
    public boolean isGlobalSpawnProtection = false;
    public Team winner;
    private int maxBlockArrowHits;
    private int spawnprotectionTicks;
    private int spawnprotectionTicksGlobal;
    private boolean startingIngame = false;

    public Ingame(WoolBattle woolbattle) {
        this.woolbattle = woolbattle;
        maxBlockArrowHits = woolbattle.getConfig("config").getInt("maxblockarrowhits");
        spawnprotectionTicks = woolbattle.getConfig("config").getInt("spawnprotectionticks");
        spawnprotectionTicksGlobal = woolbattle.getConfig("config").getInt("spawnprotectionticksglobal");
        addListener(new ListenerItemPickup(), new ListenerItemDrop(), new ListenerBlockBreak(), new ListenerBlockPlace(), new ListenerBlockCanBuild(), new ListenerPlayerJoin(), new ListenerPlayerQuit(), new ListenerPlayerLogin(), new ListenerEntityDamageByEntity(), new ListenerEntityDamage(), new ListenerChangeBlock(), new ListenerInventoryClick(), new ListenerInventoryDrag(), new ListenerInteract(woolbattle), new ListenerPlayerMove(), new ListenerEntitySpawn(), new ListenerExplode(), new ListenerDeathMove(woolbattle));

        addScheduler(new SchedulerParticles(), new SchedulerSpawnProtection(this), new SchedulerResetWool(breakedWool, placedBlocks), new SchedulerHeightDisplay(woolbattle), new SchedulerTick(), new SchedulerPerkCooldown());

    }

    public <T> T getMetaData(Block block, String key, T defaultValue) {
        if (block.hasMetadata("WoolBattleMetaStorage")) {
            BasicMetaDataStorage storage =
                    (BasicMetaDataStorage) block.getMetadata("WoolBattleMetaStorage").get(0)
                            .value();
            return storage.getOr(new Key(woolbattle, key), defaultValue);
        }
        return defaultValue;
    }

    public void setMetaData(Block block, String key, Object value) {
        BasicMetaDataStorage storage;
        boolean has = block.hasMetadata("WoolBattleMetaStorage");
        if (has) {
            storage = (BasicMetaDataStorage) block.getMetadata("WoolBattleMetaStorage").get(0)
                    .value();
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
                block.setMetadata("WoolBattleMetaStorage",
                        new FixedMetadataValue(woolbattle, storage));
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
        user.getBukkitEntity()
                .playSound(user.getBukkitEntity().getLocation(), Sound.VILLAGER_NO, 1, 1);
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
        if (!force && !placedBlocks.contains(block))
            return false;
        EventDestroyBlock destroyBlock = new EventDestroyBlock(block);
        if (force || !destroyBlock.isCancelled()) {
            Ingame ingame = woolbattle.ingame();
            if (ingame.placedBlocks.contains(block)) {
                ingame.placedBlocks.remove(block);
            } else if (block.getType() == Material.WOOL) {
                ingame.breakedWool.put(block, ((Wool) block.getState().getData()).getColor());
            }

            if (block.hasMetadata("WoolBattleMetaStorage")) {
                block.removeMetadata("WoolBattleMetaStorage", woolbattle);
            }
            block.setType(Material.AIR);
            return true;
        }
        return false;
    }

    public Team getLastTeam(WBUser user) {
        return this.lastTeam.containsKey(user) ? this.lastTeam.get(user) : user.getTeam();
    }

    @Override
    public void onEnable() {
        this.startingIngame = true;

        CloudNetLink.update();

        CompletableFuture<Void> future = woolbattle.mapLoader().loadMap(woolbattle.gameData().map());
        woolbattle.sendMessage(Message.STARTING_GAME);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (!PServerProvider.instance().isPServer()) {
            String taskName = Wrapper.getInstance().getServiceId().getTaskName();
            Wrapper.getInstance().getServiceTaskProvider().getServiceTaskAsync(taskName).onComplete(
                    task -> Wrapper.getInstance().getCloudServiceFactory()
                            .createCloudServiceAsync(task).onComplete(
                                    snap -> Wrapper.getInstance().getCloudServiceProvider(snap)
                                            .setCloudServiceLifeCycleAsync(
                                                    ServiceLifeCycle.RUNNING)));
        }

        this.splitPlayersToTeams();
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

        for (Team team : woolbattle.teamManager().getTeams()) team.setLifes(lifes);

        if (this.placedBlocks != null) {
            for (Block b : new ArrayList<>(this.placedBlocks)) {
                destroy(b);
            }
        }

        WBUser.onlineUsers().forEach(u -> {
            this.loadScoreboardObjective(u);
            u.perks().reloadFromStorage();
            u.getBukkitEntity().closeInventory();
            this.setPlayerItems(u);
            u.resetTicksAfterLastHit();
            u.getBukkitEntity().teleport(u.getTeam().getSpawn());
        });

        this.isGlobalSpawnProtection = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player t : Bukkit.getOnlinePlayers()) {
                if (p != t) {
                    if (!p.canSee(t)) {
                        p.showPlayer(t);
                    }
                }
            }
        }
        woolbattle.perkRegistry().perks().values().forEach(Perk::startLogic);
        this.startingIngame = false;
    }

    @Override
    public void onDisable() {

        woolbattle.perkRegistry().perks().values().forEach(Perk::stopLogic);

        if (this.placedBlocks != null) {
            for (Block b : new ArrayList<>(this.placedBlocks)) {
                destroy(b);
            }
        }
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

    public int getKillstreak(WBUser user) {
        return this.killstreak.getOrDefault(user, 0);
    }

    public void kill(WBUser user) {
        this.kill(user, false);
    }

    public void kill(WBUser user, boolean leaving) {
        final WBUser killer = user.getLastHit();
        boolean countAsDeath = killer != null && user.getTicksAfterLastHit() <= 200;
        EventUserKill pe2 = new EventUserKill(user, countAsDeath ? killer : null);
        Bukkit.getPluginManager().callEvent(pe2);
        if (pe2.isCancelled()) {
            return;
        }
        if (countAsDeath) {
            this.killstreak.remove(user);
            killer.setKills(killer.getKills() + 1);
            user.setDeaths(user.getDeaths() + 1);
            this.killstreak.put(killer, this.getKillstreak(killer) + 1);
            int killstreak = this.getKillstreak(killer);
            if (killstreak > 0 && killstreak % this.killsFor1Life == 0) {
                new Scheduler(() -> woolbattle
                        .sendMessage(Message.KILLSTREAK, killer.getTeamPlayerName(),
                                killstreak)).runTask();
                killer.getTeam().setLifes(killer.getTeam().getLifes() + 1);
            }
            StatsLink.addKill(killer);
            StatsLink.addDeath(user);
            StatsLink.updateKillElo(killer, user);
        }

        if (leaving) {
            this.killstreak.remove(user);
        }

        if (!countAsDeath && !leaving) {
            user.getBukkitEntity().teleport(user.getTeam().getSpawn());
            if (killer != null) {
                user.setLastHit(null);
                user.resetTicksAfterLastHit();
                user.setSpawnProtectionTicks(spawnprotectionTicks);
            }
            return;
        }
        if (countAsDeath) {
            woolbattle
                    .sendMessage(Message.PLAYER_WAS_KILLED_BY_PLAYER, user.getTeamPlayerName(),
                            killer.getTeamPlayerName());
        }

        Team userTeam = user.getTeam();
        if (userTeam.getLifes() == 0 || leaving) {
            woolbattle.teamManager()
                    .setTeam(user, woolbattle.teamManager().getSpectator());
        }

        if (userTeam.getUsers().size() == 0) {
            for (Entry<WBUser, Team> e : this.lastTeam.entrySet()) {
                if (e.getValue() == userTeam) {
                    StatsLink.addLoss(e.getKey());
                }
            }

            woolbattle.sendMessage(Message.TEAM_WAS_ELIMINATED,
                    u -> new Object[]{userTeam.getName(u)});
            if (leaving) {
                user.getBukkitEntity().kickPlayer("Disconnected");
            }
            this.checkGameEnd();
            return;
        }
        if (countAsDeath) {
            if (user.getTeam().getLifes() > 0) {
                user.getTeam().setLifes(user.getTeam().getLifes() - 1);
            }
        }
        user.getBukkitEntity().teleport(user.getTeam().getSpawn());
        user.setLastHit(null);
        user.resetTicksAfterLastHit();
        user.setSpawnProtectionTicks(spawnprotectionTicks);
    }

    public void checkGameEnd() {
        if (this.startingIngame)
            return;
        List<Team> teams = woolbattle.teamManager().getTeams().stream()
                .filter(t -> t.getUsers().size() >= 1).collect(Collectors.toList());
        if (teams.size() == 1) {
            this.winner = teams.get(0);
            for (WBUser i : this.winner.getUsers()) {
                StatsLink.addWin(i);
            }
            this.disable();
            woolbattle.endgame().enable();
        }
    }

    public void loadScoreboardObjective(WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        Objective obj = sb.getObjective(ScoreboardObjective.INGAME.getKey());
        int i = 0;
        for (Team team : woolbattle.teamManager().getTeams()) {
            ScoreboardTeam t = sb.getTeam(team.getType().getIngameScoreboardTag());
            t.addPlayer(team.getType().getInvisibleTag());
            t.setSuffix(Component.text(Characters.SHIFT_SHIFT_LEFT.toString())
                    .color(NamedTextColor.GOLD).append(team.getName(user.user())));
            obj.setScore(team.getType().getInvisibleTag(), i++);
            this.reloadScoreboardLifes(sb, team);
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public boolean revive(WBUser target) {
        if (!this.lastTeam.containsKey(target)) {
            return false;
        }
        woolbattle.teamManager().setTeam(target, this.lastTeam.remove(target));
        return true;
    }

    private void splitPlayersToTeams() {
        Collection<? extends Team> teams = woolbattle.teamManager().getTeams();
        int chosenCount = 0;
        for (Team team : teams) {
            chosenCount += team.getUsers().size();
        }
        if (chosenCount == WBUser.onlineUsers().size()) {
            if (chosenCount == 0) {
                throw new Error("Starting game without players!");
            }
            Set<WBUser> users1 = new HashSet<>();
            for (Team team : teams) {
                if (team.getUsers().size() == chosenCount) {
                    int half = team.getUsers().size() / 2;
                    while (users1.size() < half) {
                        Optional<? extends WBUser> o = team.getUsers().stream().findAny();
                        if (o.isPresent()) {
                            WBUser user = o.get();
                            users1.add(user);
                        } else {
                            woolbattle
                                    .sendConsole("§cUser of team was somehow not found");
                        }
                    }
                    break;
                }
            }
            if (users1.size() != 0) {
                for (Team team : teams) {
                    if (team.getUsers().size() == 0) {
                        for (WBUser user : users1) {
                            user.setTeam(team);
                        }
                        break;
                    }
                }
            }
        }
        int max = Objects.requireNonNull(teams.stream().findAny().orElse(null)).getType()
                .getMaxPlayers();
        for (int i = 0; i < max; i++) {
            for (Team team : teams) {
                if (team.getUsers().size() == i) {
                    for (WBUser user : WBUser.onlineUsers()) {
                        if (user.getTeam().isSpectator()) {
                            user.setTeam(team);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setArmor(WBUser user) {
        Player p = user.getBukkitEntity();
        UserArmorSetEvent event =
                new UserArmorSetEvent(user, user.getTeam().getType().getWoolColor().getColor());
        Bukkit.getPluginManager().callEvent(event);
        Color color = event.color();
        ItemStack boots = Item.ARMOR_LEATHER_BOOTS.getItem(user);
        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(color);
        boots.setItemMeta(meta);

        ItemStack leggings = Item.ARMOR_LEATHER_LEGGINGS.getItem(user);
        meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(color);
        leggings.setItemMeta(meta);

        ItemStack chest = Item.ARMOR_LEATHER_CHESTPLATE.getItem(user);
        meta = (LeatherArmorMeta) chest.getItemMeta();
        meta.setColor(color);
        chest.setItemMeta(meta);

        ItemStack helmet = Item.ARMOR_LEATHER_HELMET.getItem(user);
        meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(color);
        helmet.setItemMeta(meta);
        p.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chest, helmet});
    }

    public void setPlayerItems(WBUser user) {
        if (!this.enabled()) {
            return;
        }
        if (user.getTeam().isSpectator()) {
            this.fixSpectator(user);
            return;
        }

        CraftPlayer p = user.getBukkitEntity();
        InventoryView v = p.getOpenInventory();
        int woolCount = user.woolCount();
        v.getBottomInventory().clear();
        v.getTopInventory().clear();
        v.setCursor(new ItemStack(Material.AIR));

        this.setArmor(user);

        PlayerPerks perks = user.perksStorage();
        Set<Integer> slots = new HashSet<>();
        int required = 0;
        for (ActivationType type : ActivationType.values()) {
            for (int slot : perks.perkInvSlots(type)) {
                required++;
                slots.add(slot);
            }
        }
        if (slots.size() < required) {
            Map<ActivationType, PerkName[]> perkMap = new HashMap<>(perks.perks());
            perks.reset();
            perks.perks(perkMap);
            user.perksStorage(perks);
            user.perks().reloadFromStorage();
        }

        for (UserPerk perk : user.perks().perks()) {
            perk.currentPerkItem().setItem();
        }

        for (int i = 0; i < woolCount; i++) {
            p.getInventory().addItem(user.getSingleWoolItem());
        }
        p.getHandle().updateInventory(p.getHandle().activeContainer);
        p.getHandle().collidesWithEntities = true;
    }

    public void fixSpectator(WBUser user) {
        if (user.getTeam() != null)
            this.lastTeam.put(user, user.getTeam());
        Player p = user.getBukkitEntity();
        Scoreboard sb = new Scoreboard(user);
        p.setScoreboard(sb.getScoreboard());
        WoolBattle.initScoreboard(sb, user);
        WBUser.onlineUsers().forEach(u -> {
            Scoreboard s = new Scoreboard(u);
            s.getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
            if (!u.getTeam().isSpectator()) {
                u.getBukkitEntity().hidePlayer(p);
            } else {
                p.showPlayer(u.getBukkitEntity());
            }
            if (u != user) {
                sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());
            }
        });
        this.loadScoreboardObjective(user);
        p.spigot().setCollidesWithEntities(false);
        p.setAllowFlight(true);
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
    }

    public void reloadScoreboardLifes(WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        for (Team team : woolbattle.teamManager().getTeams()) {
            this.reloadScoreboardLifes(sb, team);
        }
    }

    public void reloadScoreboardLifes(Scoreboard sb, Team team) {
        String llifes = Integer.toString(team.getLifes());
        if (llifes.length() > 3) {
            llifes = llifes.substring(0, 3);
        }
        sb.getTeam(team.getType().getIngameScoreboardTag()).setPrefix(
                LegacyComponentSerializer.legacySection().deserialize(
                        ChatColor.translateAlternateColorCodes('&',
                                "&6" + Characters.SHIFT_SHIFT_RIGHT + " &4" + Characters.HEART
                                        + "&r" + llifes + "&4" + Characters.HEART + " ")));
    }

    public boolean canAttack(WBUser user, WBUser target) {
        if (!user.isTrollMode()) {
            if (target.getTicksAfterLastHit() < 10)
                return false;
            if (isGlobalSpawnProtection)
                return false;
            if (target.hasSpawnProtection())
                return false;
            if (!user.getTeam().canPlay())
                return false;
            if (target.getTeam().equals(user.getTeam()))
                return false;
            EventUserMayAttack mayAttack = new EventUserMayAttack(target, true);
            Bukkit.getPluginManager().callEvent(mayAttack);
            return mayAttack.mayAttack();
            //			return !ListenerGhost.isGhost(user) || ignoreGhost;
        }
        return true;
    }

    public boolean attack(WBUser user, WBUser target) {
        if (!canAttack(user, target))
            return false;
        Bukkit.getPluginManager().callEvent(new EventUserAttackUser(user, target));
        target.setLastHit(user);
        target.setTicksAfterLastHit(0);
        return true;
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
}