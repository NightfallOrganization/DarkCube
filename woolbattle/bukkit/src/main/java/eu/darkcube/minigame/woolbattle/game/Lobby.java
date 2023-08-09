/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.GameData;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.lobby.LobbyDeathLineTask;
import eu.darkcube.minigame.woolbattle.game.lobby.LobbyOverrideTimer;
import eu.darkcube.minigame.woolbattle.game.lobby.LobbyTimer;
import eu.darkcube.minigame.woolbattle.game.lobby.LobbyTimerTask;
import eu.darkcube.minigame.woolbattle.listener.lobby.*;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.*;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Lobby extends GamePhase {

    public final Map<WBUser, Vote<eu.darkcube.minigame.woolbattle.map.Map>> VOTES_MAP;
    public final Map<WBUser, Vote<Boolean>> VOTES_EP_GLITCH;
    public final Map<WBUser, Integer> VOTES_LIFES = new HashMap<>();
    private final int MAX_TIMER_SECONDS = 60;
    private final ObservableInteger timer;
    private final ObservableInteger overrideTimer;
    private final WoolBattleBukkit woolbattle;
    private int maxPlayerCount;
    private Location spawn;

    public Lobby(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        addListener(new ListenerPlayerDropItem(), new ListenerEntityDamage(), new ListenerBlockBreak(), new ListenerBlockPlace(), new ListenerPlayerJoin(this), new ListenerPlayerQuit(woolbattle), new ListenerPlayerLogin(), new ListenerInteract(), new ListenerItemParticles(), new ListenerItemSettings(woolbattle), new ListenerItemVoting(woolbattle), new ListenerItemTeams(woolbattle), new ListenerItemPerks(woolbattle), new ListenerInteractMenuBack());

        this.VOTES_MAP = new HashMap<>();
        this.VOTES_EP_GLITCH = new HashMap<>();

        this.timer = new LobbyTimer(this);
        this.overrideTimer = new LobbyOverrideTimer(timer);
        this.overrideTimer.setSilent(0);

        this.timer.setSilent(this.MAX_TIMER_SECONDS * 20);

        addScheduler(new LobbyDeathLineTask(this), new LobbyTimerTask(this, overrideTimer));
    }

    @Override
    public void onEnable() {
        CloudNetLink.update();
        woolbattle.gameData(new GameData(woolbattle));

        this.setTimer(60 * 20);
        this.VOTES_LIFES.clear();
        this.VOTES_MAP.clear();
        this.VOTES_EP_GLITCH.clear();
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.closeInventory();
            setupPlayer(WBUser.getUser(p), false);
            this.setTimer(Math.max(this.getTimer(), 300));
            p.teleport(getSpawn());
        });

        this.maxPlayerCount = -1;
    }

    @Override
    public void onDisable() {
    }

    public void unloadGame() {
        MapSize mapSize = woolbattle.gameData().mapSize();
        if (mapSize == null) return;
        for (Team team : new ArrayList<>(woolbattle.teamManager().getTeams())) {
            woolbattle.teamManager().unloadTeam(team);
        }
    }

    public void loadGame(MapSize mapSize) {
        unloadGame();
        woolbattle.gameData().mapSize(mapSize);
        int i = 0;
        for (Team team : woolbattle.teamManager().getTeams()) {
            if (team.getType().isEnabled())
                i += team.getType().getMaxPlayers();
        }
        maxPlayerCount = i;
        for (TeamType type : woolbattle.teamManager().teamTypes(mapSize)) {
            Team team = woolbattle.teamManager().loadTeam(type);
            ICommandExecutor console = ICommandExecutor.create(Bukkit.getConsoleSender());
            console.sendMessage(Component.text("loaded team ").append(team.getName(console)));
        }
    }

    public void setupScoreboard(WBUser user) {
        Scoreboard sb = new Scoreboard(user);

        ScoreboardHelper.initObjectives(sb, user, ScoreboardObjective.LOBBY);
        ScoreboardHelper.initObjectiveTeams(sb, user, ObjectiveTeam.MAP, ObjectiveTeam.EP_GLITCH, ObjectiveTeam.NEEDED, ObjectiveTeam.TIME, ObjectiveTeam.ONLINE);

        ScoreboardHelper.initTeam(sb, woolbattle.teamManager().getSpectator());
        for (Team team : woolbattle.teamManager().getTeams()) ScoreboardHelper.initTeam(sb, team);

        for (WBUser u : WBUser.onlineUsers()) sb.getTeam(u.getTeam().getType().getScoreboardTag()).addPlayer(u.getPlayerName());

        setupLobbyObjective(sb, user);
    }

    private void setupLobbyObjective(Scoreboard sb, WBUser user) {
        Objective obj = sb.getObjective(ScoreboardObjective.LOBBY.getKey());

        ScoreboardHelper.setNeeded(woolbattle, user);
        ScoreboardHelper.setOnline(user);
        ScoreboardHelper.setEpGlitch(woolbattle, user);
        ScoreboardHelper.setMap(woolbattle, user);

        setupObjectiveTeam(sb, obj, ObjectiveTeam.EP_GLITCH, 5);
        setupObjectiveTeam(sb, obj, ObjectiveTeam.TIME, 4);
        setupObjectiveTeam(sb, obj, ObjectiveTeam.MAP, 3);
        setupObjectiveTeam(sb, obj, ObjectiveTeam.NEEDED, 2);
        setupObjectiveTeam(sb, obj, ObjectiveTeam.ONLINE, 1);

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void setupObjectiveTeam(Scoreboard sb, Objective obj, ObjectiveTeam team, int score) {
        sb.getTeam(team.getKey()).addPlayer(team.getInvisTag());
        obj.setScore(team.getInvisTag(), score);
    }

    public void setupPlayer(WBUser user, @Deprecated /*TODO*/boolean fullSetup) {
        Player p = user.getBukkitEntity();
        CloudNetLink.update();

        woolbattle.teamManager().setTeam(user, woolbattle.teamManager().getSpectator());

        setupScoreboard(user);

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        if (fullSetup) {
            p.teleport(getSpawn());
        }

        p.setGameMode(GameMode.SURVIVAL);
        p.resetMaxHealth();
        p.resetPlayerTime();
        p.resetPlayerWeather();
        p.setAllowFlight(false);
        p.setExhaustion(0);
        p.setExp(0);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.setHealth(20);
        p.setItemOnCursor(null);
        p.setSaturation(0);
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        inv.setItem(0, Item.LOBBY_PERKS.getItem(user));
        inv.setItem(1, Item.LOBBY_TEAMS.getItem(user));
        inv.setItem(4, (user.particles() ? Item.LOBBY_PARTICLES_ON : Item.LOBBY_PARTICLES_OFF).getItem(user));
        inv.setItem(7, Item.SETTINGS.getItem(user));
        inv.setItem(8, Item.LOBBY_VOTING.getItem(user));

        if (fullSetup) {
            setTimer(Math.max(getTimer(), 300));
            woolbattle.sendMessage(Message.PLAYER_JOINED, user.getTeamPlayerName());
        } else {
            setTimer(getTimer());
        }
    }

    public void recalculateMap() {
        eu.darkcube.minigame.woolbattle.map.Map map = Vote.calculateWinner(this.VOTES_MAP.values().stream().filter(m -> m.vote.isEnabled()).collect(Collectors.toList()), woolbattle.mapManager().getMaps().stream().filter(eu.darkcube.minigame.woolbattle.map.Map::isEnabled).collect(Collectors.toSet()), woolbattle.gameData().map());

        if (map != null && !map.isEnabled()) map = null;

        woolbattle.gameData().votedMap(map);
    }

    public void recalculateEpGlitch() {
        boolean glitch = Vote.calculateWinner(this.VOTES_EP_GLITCH.values(), Arrays.asList(true, false), GameData.EP_GLITCH_DEFAULT);
        woolbattle.gameData().epGlitch(glitch);
    }

    public void setOverrideTimer(int ticks) {
        this.overrideTimer.setObject(ticks);
    }

    public int getTimer() {
        return this.timer.getObject();
    }

    public void setTimer(int ticks) {
        this.timer.setObject(ticks);
    }

    public Location getSpawn() {
        if (this.spawn == null) this.spawn = woolbattle.persistentDataStorage().get(Config.SPAWN, PersistentDataTypes.LOCATION, () -> new Location(Bukkit.getWorlds().get(0), 0.5, 100, 0.5));
        return this.spawn.clone();
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        woolbattle.persistentDataStorage().set(Config.SPAWN, PersistentDataTypes.LOCATION, spawn);
    }

    public int minPlayerCount() {
        return woolbattle.persistentDataStorage().get(Config.MIN_PLAYER_COUNT, PersistentDataTypes.INTEGER, () -> 2);
    }

    public int deathline() {
        return woolbattle.persistentDataStorage().get(Config.LOBBYDEATHLINE, PersistentDataTypes.INTEGER, () -> 0);
    }

    public WoolBattleBukkit woolbattle() {
        return woolbattle;
    }

    public int maxPlayerCount() {
        return maxPlayerCount;
    }

    public int maxTimerSeconds() {
        return MAX_TIMER_SECONDS;
    }
}
