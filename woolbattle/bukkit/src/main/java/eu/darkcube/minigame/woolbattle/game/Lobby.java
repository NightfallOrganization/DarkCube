/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import com.google.common.util.concurrent.AtomicDouble;
import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.GameData;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.lobby.LobbyDeathLineTask;
import eu.darkcube.minigame.woolbattle.listener.lobby.*;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.*;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

public class Lobby extends GamePhase {

    public final Map<WBUser, Vote<eu.darkcube.minigame.woolbattle.map.Map>> VOTES_MAP;
    public final Map<WBUser, Vote<Boolean>> VOTES_EP_GLITCH;
    public final Map<WBUser, Integer> VOTES_LIFES = new HashMap<>();
    public final int MAX_TIMER_SECONDS = 60;
    private final Map<WBUser, Scoreboard> SCOREBOARD_BY_USER;
    private final Map<WBUser, Set<WBUser>> SCOREBOARD_MISSING_USERS;
    private final ObservableInteger timer;
    private final ObservableInteger overrideTimer;
    private final WoolBattle woolbattle;
    private int MIN_PLAYER_COUNT;
    private int MAX_PLAYER_COUNT;
    private int deathline;
    private Scheduler timerTask;
    private Location spawn;

    public Lobby(WoolBattle woolbattle) {
        this.woolbattle = woolbattle;
        this.SCOREBOARD_BY_USER = new HashMap<>();
        this.SCOREBOARD_MISSING_USERS = new HashMap<>();
        addListener(new ListenerPlayerDropItem(), new ListenerEntityDamage(), new ListenerBlockBreak(), new ListenerBlockPlace(), new ListenerPlayerJoin(this), new ListenerPlayerQuit(woolbattle), new ListenerPlayerLogin(), new ListenerInteract(), new ListenerItemParticles(), new ListenerItemSettings(), new ListenerItemVoting(), new ListenerItemTeams(), new ListenerItemPerks(), new ListenerInteractMenuBack());

        this.VOTES_MAP = new HashMap<>();
        this.VOTES_EP_GLITCH = new HashMap<>();

        this.timer = new SimpleObservableInteger() {

            @Override
            public void onChange(ObservableObject<Integer> instance, Integer oldValue,
                                 Integer newValue) {
                if (Lobby.this.enabled()) {
                    if (newValue <= 1) {
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            p.setLevel(0);
                            p.setExp(0);
                        });
                        disable();
                        woolbattle.ingame().enable();
                        return;
                    }
                    AtomicDouble exp = new AtomicDouble(
                            (float) newValue / (MAX_TIMER_SECONDS * 20F));
                    if (exp.get() > 1)
                        exp.set(0.9999);
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.setLevel(newValue / 20);
                        p.setExp((float) exp.get());
                    });
                    WBUser.onlineUsers().forEach(
                            user -> new Scoreboard(user).getTeam(ObjectiveTeam.TIME.getKey())
                                    .setSuffix(Component.text(Integer.toString(newValue / 20))));
                }
            }

            @Override
            public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
                                       Integer newValue) {
            }

        };
        this.overrideTimer = new SimpleObservableInteger() {

            @Override
            public void onChange(ObservableObject<Integer> instance, Integer oldValue,
                                 Integer newValue) {
                Lobby.this.timer.setObject(newValue);
            }

            @Override
            public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue,
                                       Integer newValue) {
            }

        };
        this.overrideTimer.setSilent(0);
        this.timer.setSilent(this.MAX_TIMER_SECONDS * 20);

        addScheduler(new LobbyDeathLineTask(this));

        this.timerTask = new Scheduler() {

            private boolean announced = false;

            @Override
            public void run() {
                if (Lobby.this.MAX_PLAYER_COUNT == 0 && !this.announced) {
                    this.announced = true;
                    woolbattle
                            .sendConsole("It does not seem that any teams have been set up");
                } else if (Lobby.this.MAX_PLAYER_COUNT != 0) {
                    final int online = Bukkit.getOnlinePlayers().size();
                    if (online >= Lobby.this.MIN_PLAYER_COUNT) {
                        if (Lobby.this.overrideTimer.getObject() != 0) {
                            Lobby.this.overrideTimer.setObject(
                                    Lobby.this.overrideTimer.getObject() - 1);
                        } else {
                            if (online == Lobby.this.MAX_PLAYER_COUNT
                                    && Lobby.this.timer.getObject() > 200) {
                                Lobby.this.setTimer(200);
                            }
                            Lobby.this.timer.setObject(Lobby.this.timer.getObject() - 1);
                        }
                    } else if (Lobby.this.getTimer() != Lobby.this.MAX_TIMER_SECONDS * 20) {
                        Lobby.this.overrideTimer.setSilent(0);
                        Lobby.this.timer.setObject(Lobby.this.MAX_TIMER_SECONDS * 20);
                    }
                }
            }

        };
    }

    @Override
    public void onEnable() {
        CloudNetLink.update();
        woolbattle.gameData(new GameData(woolbattle));
        //		Main.getInstance().getSchedulers().clear();
        this.setTimer(60 * 20);
        this.VOTES_LIFES.clear();
        this.VOTES_MAP.clear();
        this.VOTES_EP_GLITCH.clear();
        this.SCOREBOARD_BY_USER.clear();
        this.SCOREBOARD_MISSING_USERS.clear();
        WBUser.onlineUsers().forEach(u -> this.SCOREBOARD_MISSING_USERS.put(u, new HashSet<>()));
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.closeInventory();
            setupPlayer(WBUser.getUser(p), false);
            this.setTimer(Math.max(this.getTimer(), 300));
            p.teleport(this.getSpawn());
        });
        WBUser.onlineUsers().forEach(this::reloadUsers);
        this.MIN_PLAYER_COUNT =
                woolbattle.getConfig("config").getInt(Config.MIN_PLAYER_COUNT);
        this.deathline = woolbattle.getConfig("spawns").getInt("lobbydeathline");

        int i = 0;
        for (Team team : woolbattle.teamManager().getTeams()) {
            if (team.getType().isEnabled())
                i += team.getType().getMaxPlayers();
        }
        this.MAX_PLAYER_COUNT = i;

        this.timerTask.runTaskTimer(1);
    }

    @Override
    public void onDisable() {
        this.timerTask.cancel();
    }

    public void setupPlayer(WBUser user, boolean fullSetup) {
        Player p = user.getBukkitEntity();
        CloudNetLink.update();
        woolbattle.teamManager()
                .setTeam(user, woolbattle.teamManager().getSpectator());
        Scoreboard sb = new Scoreboard();
        WoolBattle.initScoreboard(sb, user);
        getScoreboardByUser().put(user, sb);
        loadScoreboard(user);
        p.setScoreboard(sb.getScoreboard());

        Objective obj = sb.getObjective(ScoreboardObjective.LOBBY.getKey());
        int minPlayerCount =
                woolbattle.getConfig("config").getInt(Config.MIN_PLAYER_COUNT);
        ScoreboardTeam needed = sb.getTeam(ObjectiveTeam.NEEDED.getKey());
        for (int i = 0; i < ObjectiveTeam.values().length; i++) {
            ObjectiveTeam ot = ObjectiveTeam.values()[i];
            sb.getTeam(ot.getKey()).addPlayer(ot.getInvisTag());
            obj.setScore(ot.getInvisTag(), i + 1);
        }

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        needed.setSuffix(text(minPlayerCount));
        if (woolbattle.lobby().enabled()) ScoreboardHelper.setMap(woolbattle, user);
        ScoreboardHelper.setOnline(user);
        ScoreboardHelper.setEpGlitch(woolbattle, user);

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        WBUser.onlineUsers().forEach(t -> {
            if (fullSetup) reloadUsers(t);
            ScoreboardHelper.setOnline(t);
        });

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
        setParticlesItem(user, p);
        inv.setItem(0, Item.LOBBY_PERKS.getItem(user));
        inv.setItem(1, Item.LOBBY_TEAMS.getItem(user));
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
        eu.darkcube.minigame.woolbattle.map.Map map = Vote.calculateWinner(
                this.VOTES_MAP.values().stream().filter(m -> m.vote.isEnabled())
                        .collect(Collectors.toList()),
                woolbattle.mapManager().getMaps().stream()
                        .filter(eu.darkcube.minigame.woolbattle.map.Map::isEnabled)
                        .collect(Collectors.toSet()), woolbattle.gameData().map());
        if (map != null)
            if (!map.isEnabled())
                map = null;
        woolbattle.gameData().votedMap(map);
    }

    public void recalculateEpGlitch() {
        boolean glitch = Vote.calculateWinner(this.VOTES_EP_GLITCH.values(), Arrays.asList(true, false), GameData.EP_GLITCH_DEFAULT);
        woolbattle.gameData().epGlitch(glitch);
    }

    public void loadScoreboard(WBUser user) {
        Set<WBUser> missing = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.getUniqueId().equals(user.getUniqueId())) {
                missing.add(WBUser.getUser(p));
            }
        });
        this.SCOREBOARD_MISSING_USERS.values().forEach(users -> users.add(user));
        this.SCOREBOARD_MISSING_USERS.put(user, missing);
        this.SCOREBOARD_BY_USER.get(user).getTeam(user.getTeam().getType().getScoreboardTag()).addPlayer(user.getPlayerName());
    }

    public void setParticlesItem(WBUser user, Player p) {
        boolean particles = user.particles();
        if (particles) {
            p.getInventory().setItem(4, Item.LOBBY_PARTICLES_ON.getItem(user));
        } else {
            p.getInventory().setItem(4, Item.LOBBY_PARTICLES_OFF.getItem(user));
        }
    }

    public void reloadUsers(WBUser user) {
        Scoreboard sb = this.SCOREBOARD_BY_USER.get(user);
        for (WBUser u : this.SCOREBOARD_MISSING_USERS.get(user)) {
            ScoreboardTeam team = sb.getTeam(u.getTeam().getType().getScoreboardTag());
            team.addPlayer(u.getPlayerName());
        }
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
        if (this.spawn == null)
            this.spawn = Locations.deserialize(
                    woolbattle.getConfig("spawns").getString("lobby"),
                    Locations.DEFAULT_LOCATION);
        assert this.spawn != null;
        return this.spawn.clone();
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
        YamlConfiguration cfg = woolbattle.getConfig("spawns");
        cfg.set("lobby", Locations.serialize(spawn));
        woolbattle.saveConfig(cfg);
    }

    public int deathline() {
        return deathline;
    }

    public Map<WBUser, Scoreboard> getScoreboardByUser() {
        return this.SCOREBOARD_BY_USER;
    }

}
