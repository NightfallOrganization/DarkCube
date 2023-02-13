/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game;

import com.google.common.util.concurrent.AtomicDouble;
import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.lobby.*;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.*;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class Lobby extends GamePhase {

	public final ListenerPlayerDropItem listenerPlayerDropItem;

	public final ListenerEntityDamage listenerEntityDamage;

	public final ListenerBlockBreak listenerBlockBreak;

	public final ListenerBlockPlace listenerBlockPlace;

	public final ListenerPlayerJoin listenerPlayerJoin;

	public final ListenerPlayerQuit listenerPlayerQuit;

	public final ListenerPlayerLogin listenerPlayerLogin;

	public final ListenerInteract listenerInteract;

	public final ListenerItemParticles listenerItemParticles;

	public final ListenerItemVoting listenerItemVoting;

	public final ListenerItemPerks listenerItemPerks;

	public final ListenerItemTeams listenerItemTeams;

	public final ListenerItemSettings listenerItemSettings;

	public final ListenerInteractMenuBack listenerInteractMenuBack;
	public final Map<WBUser, Vote<eu.darkcube.minigame.woolbattle.map.Map>> VOTES_MAP;
	public final Map<WBUser, Vote<Boolean>> VOTES_EP_GLITCH;
	public final Map<WBUser, Integer> VOTES_LIFES = new HashMap<>();
	public final int MAX_TIMER_SECONDS = 60;
	private final Map<WBUser, Scoreboard> SCOREBOARD_BY_USER;
	private final Map<WBUser, Set<WBUser>> SCOREBOARD_MISSING_USERS;
	private final ObservableInteger timer;
	private final ObservableInteger overrideTimer;
	private int MIN_PLAYER_COUNT;

	private int MAX_PLAYER_COUNT;

	private int deathline;

	private Scheduler timerTask;

	private Scheduler deathLineTask;

	private Location spawn;

	public Lobby() {
		this.SCOREBOARD_BY_USER = new HashMap<>();
		this.SCOREBOARD_MISSING_USERS = new HashMap<>();

		this.listenerPlayerDropItem = new ListenerPlayerDropItem();
		this.listenerEntityDamage = new ListenerEntityDamage();
		this.listenerBlockBreak = new ListenerBlockBreak();
		this.listenerBlockPlace = new ListenerBlockPlace();
		this.listenerPlayerJoin = new ListenerPlayerJoin();
		this.listenerPlayerQuit = new ListenerPlayerQuit();
		this.listenerPlayerLogin = new ListenerPlayerLogin();
		this.listenerInteract = new ListenerInteract();

		this.listenerItemParticles = new ListenerItemParticles();
		this.listenerItemSettings = new ListenerItemSettings();
		this.listenerItemVoting = new ListenerItemVoting();
		this.listenerItemTeams = new ListenerItemTeams();
		this.listenerItemPerks = new ListenerItemPerks();

		this.listenerInteractMenuBack = new ListenerInteractMenuBack();

		this.VOTES_MAP = new HashMap<>();
		this.VOTES_EP_GLITCH = new HashMap<>();

		this.timer = new SimpleObservableInteger() {

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue,
					Integer newValue) {
				if (Lobby.this.isEnabled()) {
					if (newValue <= 1) {
						Bukkit.getOnlinePlayers().forEach(p -> {
							p.setLevel(0);
							p.setExp(0);
						});
						Lobby.this.disable();
						WoolBattle.getInstance().getIngame().enable();
						return;
					}
					AtomicDouble exp = new AtomicDouble(
							(float) newValue / (Lobby.this.MAX_TIMER_SECONDS * 20F));
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

		this.deathLineTask = new Scheduler() {

			@Override
			public void run() {
				Bukkit.getOnlinePlayers().forEach(p -> {
					if (p.getLocation().getBlockY() < Lobby.this.deathline) {
						p.teleport(Lobby.this.getSpawn());
					}
				});
			}

		};

		this.timerTask = new Scheduler() {

			private boolean announced = false;

			@Override
			public void run() {
				if (Lobby.this.MAX_PLAYER_COUNT == 0 && !this.announced) {
					this.announced = true;
					WoolBattle.getInstance()
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
		WoolBattle.getInstance().reloadConfig("config");
		WoolBattle.getInstance().baseLifes = null;
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
			this.listenerPlayerJoin.handle(new PlayerJoinEvent(p, null));
			this.setTimer(Math.max(this.getTimer(), 300));
			p.teleport(this.getSpawn());
		});
		WBUser.onlineUsers().forEach(this::reloadUsers);
		this.MIN_PLAYER_COUNT =
				WoolBattle.getInstance().getConfig("config").getInt(Config.MIN_PLAYER_COUNT);
		this.deathline = WoolBattle.getInstance().getConfig("spawns").getInt("lobbydeathline");

		int i = 0;
		for (Team team : WoolBattle.getInstance().getTeamManager().getTeams()) {
			if (team.getType().isEnabled())
				i += team.getType().getMaxPlayers();
		}
		this.MAX_PLAYER_COUNT = i;

		WoolBattle.registerListeners(this.listenerItemTeams, this.listenerItemPerks,
				this.listenerItemVoting, this.listenerItemParticles, this.listenerBlockBreak,
				this.listenerBlockPlace, this.listenerPlayerJoin, this.listenerPlayerQuit,
				this.listenerPlayerDropItem, this.listenerEntityDamage,
				this.listenerInteractMenuBack, this.listenerPlayerLogin, this.listenerItemSettings,
				this.listenerInteract);
		this.timerTask.runTaskTimer(1);
		this.deathLineTask.runTaskTimer(20);
	}

	@Override
	public void onDisable() {
		WoolBattle.unregisterListeners(this.listenerItemTeams, this.listenerItemPerks,
				this.listenerItemVoting, this.listenerItemParticles, this.listenerBlockBreak,
				this.listenerBlockPlace, this.listenerPlayerJoin, this.listenerPlayerQuit,
				this.listenerPlayerDropItem, this.listenerEntityDamage,
				this.listenerInteractMenuBack, this.listenerPlayerLogin, this.listenerItemSettings,
				this.listenerInteract);
		this.timerTask.cancel();
		this.deathLineTask.cancel();
	}

	public void recalculateMap() {
		eu.darkcube.minigame.woolbattle.map.Map map = Vote.calculateWinner(
				this.VOTES_MAP.values().stream().filter(m -> m.vote.isEnabled())
						.collect(Collectors.toList()),
				WoolBattle.getInstance().getMapManager().getMaps().stream()
						.filter(eu.darkcube.minigame.woolbattle.map.Map::isEnabled)
						.collect(Collectors.toSet()), WoolBattle.getInstance().getMap());
		if (map != null)
			if (!map.isEnabled())
				map = null;
		WoolBattle.getInstance().setMap(map);
	}

	public void recalculateEpGlitch() {
		boolean glitch =
				Vote.calculateWinner(this.VOTES_EP_GLITCH.values(), Arrays.asList(true, false),
						false);
		WoolBattle.getInstance().setEpGlitch(glitch);
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
		this.SCOREBOARD_BY_USER.get(user).getTeam(user.getTeam().getType().getScoreboardTag())
				.addPlayer(user.getPlayerName());
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
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team =
					sb.getTeam(u.getTeam().getType().getScoreboardTag());
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
					WoolBattle.getInstance().getConfig("spawns").getString("lobby"),
					Locations.DEFAULT_LOCATION);
		assert this.spawn != null;
		return this.spawn.clone();
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		YamlConfiguration cfg = WoolBattle.getInstance().getConfig("spawns");
		cfg.set("lobby", Locations.serialize(spawn));
		WoolBattle.getInstance().saveConfig(cfg);
	}

	public Map<WBUser, Scoreboard> getScoreboardByUser() {
		return this.SCOREBOARD_BY_USER;
	}

}
