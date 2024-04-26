/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.game.ingame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.user.EventUserAttackUser;
import eu.darkcube.minigame.woolbattle.event.user.EventUserKill;
import eu.darkcube.minigame.woolbattle.event.user.EventUserMayAttack;
import eu.darkcube.minigame.woolbattle.event.user.UserArmorSetEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Characters;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayerUtil {

    private final Ingame ingame;
    private final WoolBattleBukkit woolbattle;

    public PlayerUtil(Ingame ingame) {
        this.ingame = ingame;
        this.woolbattle = ingame.woolbattle();
    }

    public void setupPlayers() {
        WBUser.onlineUsers().forEach(this::setupPlayer);
    }

    public void setupPlayer(WBUser user) {
        Player p = user.getBukkitEntity();
        p.setGameMode(GameMode.SURVIVAL);
        p.resetMaxHealth();
        p.resetPlayerTime();
        p.resetPlayerWeather();
        p.setAllowFlight(true);
        p.setExhaustion(0);
        p.setExp(0);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setItemOnCursor(null);
        p.setSaturation(0);
        user.resetTicksAfterLastHit();
        if (user.getTeam() == null) user.setTeam(woolbattle.teamManager().getSpectator());
        //        if (!user.getTeam().isSpectator()) {
        user.perks().reloadFromStorage();
        loadScoreboard(user);
        user.getBukkitEntity().closeInventory();
        setPlayerItems(user);
        Location loc = user.getTeam().getSpawn();
        if (loc == null) loc = woolbattle.lobby().getSpawn();
        user.getBukkitEntity().teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);

        //        }
    }

    public void loadScoreboard(WBUser user) {
        loadScoreboardObjective(user);
        var sb = new Scoreboard(user);
        ScoreboardHelper.initTeam(sb, woolbattle.teamManager().getSpectator());
        for (Team team : woolbattle.teamManager().getTeams()) {
            ScoreboardHelper.initTeam(sb, team);
        }
    }

    public void loadScoreboardObjective(WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardHelper.initObjectives(sb, user, ScoreboardObjective.INGAME);
        Objective obj = sb.getObjective(ScoreboardObjective.INGAME.getKey());
        int i = 0;
        for (Team team : woolbattle.teamManager().getTeams()) {
            ScoreboardTeam t = sb.getTeam(team.getType().getLivesScoreboardTag());
            t.addPlayer(team.getType().getInvisibleTag());
            t.setSuffix(Component.text(Characters.SHIFT_SHIFT_LEFT.toString()).color(NamedTextColor.GOLD).append(team.getName(user.user())));
            obj.setScore(team.getType().getInvisibleTag(), i++);
            reloadScoreboardLifes(sb, team);
        }
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public boolean revive(WBUser target) {
        if (!ingame.lastTeam.containsKey(target)) {
            return false;
        }
        target.setTeam(ingame.lastTeam.remove(target));
        return true;
    }

    public void setArmor(WBUser user) {
        Player p = user.getBukkitEntity();
        UserArmorSetEvent event = new UserArmorSetEvent(user, user.getTeam().getType().getWoolColor().getColor());
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
        if (!ingame.enabled()) {
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
        for (Perk.ActivationType type : Perk.ActivationType.values()) {
            for (int slot : perks.perkInvSlots(type)) {
                required++;
                slots.add(slot);
            }
        }
        if (slots.size() < required) {
            Map<Perk.ActivationType, PerkName[]> perkMap = new HashMap<>(perks.perks());
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
        fixAllVisibilities(user);
    }

    public int getKillstreak(WBUser user) {
        return ingame.killstreak.getOrDefault(user, 0);
    }

    public void kill(WBUser user) {
        this.kill(user, false);
    }

    public boolean canAttack(WBUser user, WBUser target) {
        if (!user.isTrollMode()) {
            if (target.getTicksAfterLastHit() < 10) return false;
            if (ingame.globalSpawnProtection()) return false;
            if (target.hasSpawnProtection()) return false;
            if (!user.getTeam().canPlay()) return false;
            if (target.getTeam().equals(user.getTeam())) return false;
            EventUserMayAttack mayAttack = new EventUserMayAttack(target, true);
            Bukkit.getPluginManager().callEvent(mayAttack);
            return mayAttack.mayAttack();
        }
        return true;
    }

    public boolean attack(WBUser user, WBUser target) {
        if (!canAttack(user, target)) return false;
        Bukkit.getPluginManager().callEvent(new EventUserAttackUser(user, target));
        target.setLastHit(user);
        target.setTicksAfterLastHit(0);
        return true;
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
            ingame.killstreak.remove(user);
            killer.setKills(killer.getKills() + 1);
            user.setDeaths(user.getDeaths() + 1);
            ingame.killstreak.put(killer, this.getKillstreak(killer) + 1);
            int killstreak = this.getKillstreak(killer);
            if (killstreak > 0 && killstreak % ingame.killsFor1Life == 0) {
                new Scheduler(woolbattle, () -> woolbattle.sendMessage(Message.KILLSTREAK, killer.getTeamPlayerName(), killstreak)).runTask();
                killer.getTeam().setLifes(killer.getTeam().getLifes() + 1);
            }
            StatsLink.addKill(killer);
            StatsLink.addDeath(user);
            StatsLink.updateKillElo(killer, user);
        }

        if (leaving) {
            ingame.killstreak.remove(user);
        }

        if (!countAsDeath && !leaving) {
            user.getBukkitEntity().teleport(user.getTeam().getSpawn());
            if (killer != null) {
                user.setLastHit(null);
                user.resetTicksAfterLastHit();
                user.setSpawnProtectionTicks(ingame.spawnprotectionTicks());
            }
            return;
        }
        if (countAsDeath) {
            woolbattle.sendMessage(Message.PLAYER_WAS_KILLED_BY_PLAYER, user.getTeamPlayerName(), killer.getTeamPlayerName());
        }

        Team userTeam = user.getTeam();
        if (userTeam.getLifes() == 0 || leaving) {
            user.setTeam(woolbattle.teamManager().getSpectator());
        }

        if (userTeam.getUsers().isEmpty()) {
            for (Map.Entry<WBUser, Team> e : ingame.lastTeam.entrySet()) {
                if (e.getValue() == userTeam) {
                    StatsLink.addLoss(e.getKey());
                }
            }

            woolbattle.sendMessage(Message.TEAM_WAS_ELIMINATED, u -> new Object[]{userTeam.getName(u)});
            if (leaving) {
                user.getBukkitEntity().kickPlayer("Disconnected");
            }
            ingame.checkGameEnd();
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
        user.setSpawnProtectionTicks(ingame.spawnprotectionTicks());
    }

    private void fixVisibilities(WBUser user, WBUser target) {
        var us = user.getTeam().isSpectator();
        var ts = target.getTeam().isSpectator();
        if ((us && ts) || (!us && ts)) user.getBukkitEntity().hidePlayer(target.getBukkitEntity());
        if (us) {
            user.getBukkitEntity().showPlayer(target.getBukkitEntity());
        } else {
            if (!ts) {
                user.getBukkitEntity().showPlayer(target.getBukkitEntity());
            }
        }
    }

    private void fixAllVisibilities(WBUser user) {
        WBUser.onlineUsers().forEach(target -> {
            fixVisibilities(user, target);
            fixVisibilities(target, user);
        });
    }

    public void fixSpectator(WBUser user) {
        //        if (user.getTeam() != null ) ingame.lastTeam.put(user, user.getTeam());
        Player p = user.getBukkitEntity();
        Scoreboard sb = new Scoreboard(user);
        WBUser.onlineUsers().forEach(u -> {
            Scoreboard s = new Scoreboard(u);
            s.getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
            if (u != user) {
                sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());
            }
        });
        fixAllVisibilities(user);
        loadScoreboardObjective(user);
        p.spigot().setCollidesWithEntities(false);
        p.setAllowFlight(true);
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.getInventory().setItem(0, Item.TELEPORT_COMPASS.getItem(user));
    }

    public void reloadScoreboardLifes() {
        for (WBUser user : WBUser.onlineUsers()) {
            reloadScoreboardLifes(user);
        }
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
        sb.getTeam(team.getType().getLivesScoreboardTag()).setPrefix(LegacyComponentSerializer.legacySection().deserialize(ChatColor.translateAlternateColorCodes('&', "&6" + Characters.SHIFT_SHIFT_RIGHT + " &4" + Characters.HEART + "&r" + llifes + "&4" + Characters.HEART + " ")));
    }
}
