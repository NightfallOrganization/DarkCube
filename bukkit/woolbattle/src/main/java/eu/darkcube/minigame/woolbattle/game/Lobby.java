package eu.darkcube.minigame.woolbattle.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.util.concurrent.AtomicDouble;

import eu.darkcube.minigame.woolbattle.Config;
import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerBlockBreak;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerBlockPlace;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerEntityDamage;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerInteract;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerInteractMenuBack;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerPlayerDropItem;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerPlayerJoin;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerPlayerLogin;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerPlayerQuit;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemGadgets;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemParticles;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemPerks;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemSettings;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemTeams;
import eu.darkcube.minigame.woolbattle.listener.lobby.item.ListenerItemVoting;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Characters;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.Enableable;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.minigame.woolbattle.util.Vote;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.tab.Footer;
import eu.darkcube.minigame.woolbattle.util.tab.Header;
import eu.darkcube.minigame.woolbattle.util.tab.TabManager;

public class Lobby implements Enableable {

	public final ListenerPlayerDropItem listenerPlayerDropItem;
	public final ListenerEntityDamage listenerEntityDamage;
	public final ListenerBlockBreak listenerBlockBreak;
	public final ListenerBlockPlace listenerBlockPlace;
	public final ListenerPlayerJoin listenerPlayerJoin;
	public final ListenerPlayerQuit listenerPlayerQuit;
	public final ListenerPlayerLogin listenerPlayerLogin;
	public final ListenerInteract listenerInteract;

	public final ListenerItemParticles listenerItemParticles;
	public final ListenerItemGadgets listenerItemGadgets;
	public final ListenerItemVoting listenerItemVoting;
	public final ListenerItemPerks listenerItemPerks;
	public final ListenerItemTeams listenerItemTeams;
	public final ListenerItemSettings listenerItemSettings;
	public final ListenerInteractMenuBack listenerInteractMenuBack;

	private final Map<User, Scoreboard> SCOREBOARD_BY_USER;
	private final Map<User, Set<User>> SCOREBOARD_MISSING_USERS;

	public final Map<User, Vote<eu.darkcube.minigame.woolbattle.map.Map>> VOTES_MAP;
	public final Map<User, Vote<Boolean>> VOTES_EP_GLITCH;
	public final Map<User, Integer> VOTES_LIFES = new HashMap<>();

	private final ObservableInteger timer;
	private final ObservableInteger overrideTimer;
	public final int MAX_TIMER_SECONDS = 60;
	private int MIN_PLAYER_COUNT;
	private int MAX_PLAYER_COUNT;
	private int deathline;

	private Scheduler timerTask;
	private Scheduler deathLineTask;

	private Location spawn;

	public Lobby() {
		SCOREBOARD_BY_USER = new HashMap<>();
		SCOREBOARD_MISSING_USERS = new HashMap<>();

		listenerPlayerDropItem = new ListenerPlayerDropItem();
		listenerEntityDamage = new ListenerEntityDamage();
		listenerBlockBreak = new ListenerBlockBreak();
		listenerBlockPlace = new ListenerBlockPlace();
		listenerPlayerJoin = new ListenerPlayerJoin();
		listenerPlayerQuit = new ListenerPlayerQuit();
		listenerPlayerLogin = new ListenerPlayerLogin();
		listenerInteract = new ListenerInteract();

		listenerItemParticles = new ListenerItemParticles();
		listenerItemSettings = new ListenerItemSettings();
		listenerItemGadgets = new ListenerItemGadgets();
		listenerItemVoting = new ListenerItemVoting();
		listenerItemTeams = new ListenerItemTeams();
		listenerItemPerks = new ListenerItemPerks();

		listenerInteractMenuBack = new ListenerInteractMenuBack();

		VOTES_MAP = new HashMap<>();
		VOTES_EP_GLITCH = new HashMap<>();

		timer = new SimpleObservableInteger() {
			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				if (isEnabled()) {
					if (newValue <= 1) {
						Bukkit.getOnlinePlayers().forEach(p -> {
							p.setLevel(0);
							p.setExp(0);
						});
						Lobby.this.disable();
						Main.getInstance().getIngame().enable();
						return;
					}
					AtomicDouble exp = new AtomicDouble((float) newValue / (MAX_TIMER_SECONDS * 20F));
					if (exp.get() > 1)
						exp.set(0.9999);
					Bukkit.getOnlinePlayers().forEach(p -> {
						p.setLevel(newValue / 20);
						p.setExp((float) exp.get());
					});
					Main.getInstance().getUserWrapper().getUsers().forEach(user -> {
						new Scoreboard(user).getTeam(ObjectiveTeam.TIME.getKey())
								.setSuffix(Integer.toString(newValue / 20));
					});
				}
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
			}
		};
		overrideTimer = new SimpleObservableInteger() {
			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
			}

			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				timer.setObject(newValue);
			}
		};
		overrideTimer.setSilent(0);
		timer.setSilent(MAX_TIMER_SECONDS * 20);

		deathLineTask = new Scheduler() {

			@Override
			public void run() {
				Bukkit.getOnlinePlayers().forEach(p -> {
					if (p.getLocation().getBlockY() < deathline) {
						p.teleport(getSpawn());
					}
				});
			}
		};

		timerTask = new Scheduler() {
			private boolean announced = false;

			@Override
			public void run() {
				if (MAX_PLAYER_COUNT == 0 && !announced) {
					announced = true;
					Main.getInstance().sendConsole("It does not seem that any teams have been set up");
				} else if (MAX_PLAYER_COUNT != 0) {
					final int online = Bukkit.getOnlinePlayers().size();
					if (online >= MIN_PLAYER_COUNT) {
						if (overrideTimer.getObject() != 0) {
							overrideTimer.setObject(overrideTimer.getObject() - 1);
						} else {
							if (online == MAX_PLAYER_COUNT && timer.getObject() > 200) {
								setTimer(200);
							}
							timer.setObject(timer.getObject() - 1);
						}
					} else if (getTimer() != MAX_TIMER_SECONDS * 20) {
						overrideTimer.setSilent(0);
						timer.setObject(MAX_TIMER_SECONDS * 20);
					}
//					if (Bukkit.getOnlinePlayers().size() >= MIN_PLAYER_COUNT) {
//						if (Bukkit.getOnlinePlayers().size() == MAX_PLAYER_COUNT && getTimer() > 200) {
//							setTimer(200);
//						} else if (getTimer() > 1) {
//							setTimer(getTimer() - 1);
//						}
//					} else if (getTimer() != MAX_TIMER_SECONDS * 20) {
//						setTimer(MAX_TIMER_SECONDS * 20);
//					}
				}
			}
		};
	}

	@Override
	public void onEnable() {
		CloudNetLink.update();
		Main.getInstance().reloadConfig("config");
		Main.getInstance().baseLifes = null;
		Main.getInstance().getSchedulers().clear();
		setTimer(60 * 20);
		VOTES_LIFES.clear();
		VOTES_MAP.clear();
		VOTES_EP_GLITCH.clear();
		SCOREBOARD_BY_USER.clear();
		SCOREBOARD_MISSING_USERS.clear();
		Main.getInstance().getUserWrapper().getUsers().forEach(u -> SCOREBOARD_MISSING_USERS.put(u, new HashSet<>()));
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.closeInventory();
			listenerPlayerJoin.handle(new PlayerJoinEvent(p, null));
			setTimer(getTimer() < 300 ? 300 : getTimer());
			p.teleport(getSpawn());
		});
		Main.getInstance().getUserWrapper().getUsers().forEach(u -> reloadUsers(u));
		MIN_PLAYER_COUNT = Main.getInstance().getConfig("config").getInt(Config.MIN_PLAYER_COUNT);
		deathline = Main.getInstance().getConfig("spawns").getInt("lobbydeathline");

		int i = 0;
		for (Team team : Main.getInstance().getTeamManager().getTeams()) {
			if (team.getType().isEnabled())
				i += team.getType().getMaxPlayers();
		}
		MAX_PLAYER_COUNT = i;

		Main.registerListeners(listenerItemTeams, listenerItemPerks, listenerItemVoting, listenerItemGadgets,
				listenerItemParticles, listenerBlockBreak, listenerBlockPlace, listenerPlayerJoin, listenerPlayerQuit,
				listenerPlayerDropItem, listenerEntityDamage, listenerInteractMenuBack, listenerPlayerLogin,
				listenerItemSettings, listenerInteract);
		timerTask.runTaskTimer(1);
		deathLineTask.runTaskTimer(20);
	}

	@Override
	public void onDisable() {
		Main.unregisterListeners(listenerItemTeams, listenerItemPerks, listenerItemVoting, listenerItemGadgets,
				listenerItemParticles, listenerBlockBreak, listenerBlockPlace, listenerPlayerJoin, listenerPlayerQuit,
				listenerPlayerDropItem, listenerEntityDamage, listenerInteractMenuBack, listenerPlayerLogin,
				listenerItemSettings, listenerInteract);
		timerTask.cancel();
		deathLineTask.cancel();
	}

	public void recalculateMap() {
		eu.darkcube.minigame.woolbattle.map.Map map = Vote.calculateWinner(
				VOTES_MAP.values().stream().filter(m -> m.vote.isEnabled()).collect(Collectors.toList()),
				Main.getInstance().getMapManager().getMaps().stream().filter(m -> m.isEnabled())
						.collect(Collectors.toSet()),
				Main.getInstance().getMap());
		if (map != null)
			if (!map.isEnabled())
				map = null;
		Main.getInstance().setMap(map);
	}

	public void recalculateEpGlitch() {
		boolean glitch = Vote.calculateWinner(VOTES_EP_GLITCH.values(), Arrays.asList(true, false), true);
		Main.getInstance().setEpGlitch(glitch);
	}

	public void loadScoreboard(User user) {
		Set<User> missing = new HashSet<>();
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (!p.getUniqueId().equals(user.getUniqueId())) {
				missing.add(Main.getInstance().getUserWrapper().getUser(p.getUniqueId()));
			}
		});
		SCOREBOARD_MISSING_USERS.values().forEach(users -> users.add(user));
		SCOREBOARD_MISSING_USERS.put(user, missing);
		SCOREBOARD_BY_USER.get(user).getTeam(user.getTeam().getType().getScoreboardTag())
				.addPlayer(user.getPlayerName());
	}

	public void setParticlesItem(User user, Player p) {
		boolean particles = user.getData().isParticles();
		if (particles) {
			p.getInventory().setItem(4, Item.LOBBY_PARTICLES_ON.getItem(user));
		} else {
			p.getInventory().setItem(4, Item.LOBBY_PARTICLES_OFF.getItem(user));
		}
	}

	public void reloadUsers(User user) {
		Scoreboard sb = SCOREBOARD_BY_USER.get(user);
		for (User u : SCOREBOARD_MISSING_USERS.get(user)) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team = sb
					.getTeam(u.getTeam().getType().getScoreboardTag());
			team.addPlayer(u.getPlayerName());
		}
	}

	public void reloadTab(User user) {
//		Header header = new Header(Main.getInstance().getConfig("config").getString(Config.TAB_HEADER));
//		Footer footer = new Footer(Main.getInstance().getConfig("config").getString(Config.TAB_FOOTER));
		Header header = new Header(Main.tab_header);
		Footer footer = new Footer(Main.tab_footer);
		header.setMessage(header.getMessage().replace("%map%",
				Main.getInstance().getMap() == null ? "Unknown Map" : Main.getInstance().getMap().getName()));
		footer.setMessage(footer.getMessage().replace("%map%",
				Main.getInstance().getMap() == null ? "Unknown Map" : Main.getInstance().getMap().getName()));
		header.setMessage(header.getMessage().replace("%name%", Main.getInstance().name));
		footer.setMessage(footer.getMessage().replace("%name%", Main.getInstance().name));
		header.setMessage(header.getMessage().replace("%prefix%", "&8" + Characters.SHIFT_SHIFT_RIGHT + " &6"
				+ Main.getInstance().tabprefix + " &8" + Characters.SHIFT_SHIFT_LEFT));
		footer.setMessage(footer.getMessage().replace("%prefix%", "&8" + Characters.SHIFT_SHIFT_RIGHT + " &6"
				+ Main.getInstance().tabprefix + " &8" + Characters.SHIFT_SHIFT_LEFT));
		header.setMessage(header.getMessage().replace("%server%", Bukkit.getServerName()));
		footer.setMessage(footer.getMessage().replace("%server%", Bukkit.getServerName()));
		header.setMessage(ChatColor.translateAlternateColorCodes('&', header.getMessage()).replace("\\n", "\n"));
		footer.setMessage(ChatColor.translateAlternateColorCodes('&', footer.getMessage()).replace("\\n", "\n"));
		TabManager.setHeaderFooter(user, header, footer);
	}

	public void setTimer(int ticks) {
		timer.setObject(ticks);
	}

	public void setOverrideTimer(int ticks) {
		overrideTimer.setObject(ticks);
	}

	public int getTimer() {
		return timer.getObject();
	}

	public Location getSpawn() {
		if (spawn == null)
			spawn = Locations.deserialize(Main.getInstance().getConfig("spawns").getString("lobby"),
					Locations.DEFAULT_LOCATION);
		return spawn.clone();
	}

	public Map<User, Scoreboard> getScoreboardByUser() {
		return SCOREBOARD_BY_USER;
	}
}