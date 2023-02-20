/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.command.*;
import eu.darkcube.minigame.woolbattle.game.Endgame;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.*;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerWeatherChange;
import eu.darkcube.minigame.woolbattle.map.DefaultMapManager;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapManager;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.team.DefaultTeamManager;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamManager;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.user.WBUserModifier;
import eu.darkcube.minigame.woolbattle.util.*;
import eu.darkcube.minigame.woolbattle.util.DependencyManager.Dependency;
import eu.darkcube.minigame.woolbattle.util.convertingrule.*;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardTeam;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPluginLoader;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.ILanguagedCommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.loader.ReflectionClassLoader;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.data.UserModifier;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

public class WoolBattle extends Plugin {

	private static WoolBattle instance;
	public String name;
	public String tabprefix;
	public String atall;
	public String atteam;
	public Integer baseLifes;
	public Map baseMap;
	private PerkRegistry perkRegistry;
	private UserModifier userModifier;
	private GameState state;
	private Lobby lobby;
	private Ingame ingame;
	private Endgame endgame;
	private Map map;
	private TeamManager teamManager;
	private MapManager mapManager;
	private Collection<SchedulerTask> schedulers;
	private PluginClassLoader pluginClassLoader;
	private BukkitTask tickTask;
	private ListenerPlayerInteract listenerPlayerInteract;
	private ListenerInventoryClick listenerInventoryClick;
	private ListenerInventoryClose listenerInventoryClose;
	private ListenerFoodLevelChange listenerFoodLevelChange;
	private ListenerWeatherChange listenerWeatherChange;
	private ListenerLaunchable listenerLaunchable;
	private ListenerAntiMonster listenerAntiMonster;
	private ListenerChat listenerChat;
	private MySQL mysql;
	private int maxPlayers;
	private boolean epGlitch = true;
	private int currentTick;

	public WoolBattle() {
		super("woolbattle");
		WoolBattle.instance = this;
		System.setProperty("file.encoding", "UTF-8");
	}

	public static void initScoreboard(Scoreboard sb, WBUser owner) {
		// Spectator is not included in "Team"
		Collection<Team> teams =
				new HashSet<>(WoolBattle.getInstance().getTeamManager().getTeams());
		teams.add(WoolBattle.getInstance().getTeamManager().getSpectator());
		for (Team t : teams) {
			ScoreboardTeam team = sb.createTeam(t.getType().getScoreboardTag());
			System.out.println(java.util.Arrays.toString(LegacyComponentSerializer.legacySection()
					.serialize(Component.text("", t.getPrefixStyle())).toCharArray()));
			team.setPrefix(Component.text("", t.getPrefixStyle()));
		}
		for (ScoreboardObjective obj : ScoreboardObjective.values()) {
			Objective o = sb.createObjective(obj.getKey(), "dummy");
			o.setDisplayName(Message.getMessage(obj.getMessageKey(), owner.getLanguage()));
		}
		for (ObjectiveTeam team : ObjectiveTeam.values()) {
			ScoreboardTeam t = sb.createTeam(team.getKey());
			t.setPrefix(Message.getMessage(team.getMessagePrefix(), owner.getLanguage()));
			t.setSuffix(Message.getMessage(team.getMessageSuffix(), owner.getLanguage()));
		}
	}

	public static WoolBattle getInstance() {
		return WoolBattle.instance;
	}

	public static void registerListeners(Listener... listener) {
		for (Listener l : listener) {
			if (l instanceof RegisterNotifyListener) {
				((RegisterNotifyListener) l).registered();
			}
			WoolBattle.getInstance().getServer().getPluginManager()
					.registerEvents(l, WoolBattle.getInstance());
		}
	}

	public static void unregisterListeners(Listener... listener) {
		for (Listener l : listener) {
			if (l instanceof RegisterNotifyListener) {
				((RegisterNotifyListener) l).unregistered();
			}
			HandlerList.unregisterAll(l);
		}
	}

	@Override
	public void onLoad() {
		this.pluginClassLoader = new ReflectionClassLoader(this);
		new DependencyManager(this).loadDependencies(Dependency.values());

		// Load all messages
		try {
			Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties",
					Message.KEY_MODFIIER);
			Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties",
					Message.KEY_MODFIIER);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		List<String> languageEntries = new ArrayList<>();
		languageEntries.addAll(Arrays.asList(Message.values()).stream().map(Message::getKey)
				.collect(Collectors.toList()));
		languageEntries.addAll(
				Arrays.asList(Item.values()).stream().map(i -> Message.ITEM_PREFIX + i.getKey())
						.collect(Collectors.toList()));
		languageEntries.addAll(
				Arrays.asList(Item.values()).stream().filter(i -> i.getBuilder().lore().size() > 0)
						.map(i -> Message.ITEM_PREFIX + Message.LORE_PREFIX + i.getKey())
						.collect(Collectors.toList()));
		Language.validateEntries(languageEntries.toArray(new String[0]),
				s -> Message.KEY_PREFIX + s);

		// Create Array converting rules
		Arrays.addConvertingRule(new ConvertingRuleLanguage());
		Arrays.addConvertingRule(new ConvertingRuleChatColor());
		Arrays.addConvertingRule(new ConvertingRuleDyeColor());
		Arrays.addConvertingRule(new ConvertingRuleTeam());
		Arrays.addConvertingRule(new ConvertingRuleMap());

		// Create configs
		this.saveDefaultConfig("teams");
		this.createConfig("teams");

		this.saveDefaultConfig("config");
		this.createConfig("config");

		this.saveDefaultConfig("mysql");
		this.createConfig("mysql");

		this.saveDefaultConfig("worlds");
		this.createConfig("worlds");

		this.saveDefaultConfig("spawns");
		this.createConfig("spawns");

		this.schedulers = new ArrayList<>();
		this.perkRegistry = new PerkRegistry();

		// Init listeners
		this.listenerInventoryClose = new ListenerInventoryClose();
		this.listenerInventoryClick = new ListenerInventoryClick();
		this.listenerPlayerInteract = new ListenerPlayerInteract();
		this.listenerFoodLevelChange = new ListenerFoodLevelChange();
		this.listenerWeatherChange = new ListenerWeatherChange();
		this.listenerLaunchable = new ListenerLaunchable();
		this.listenerChat = new ListenerChat();
		this.listenerAntiMonster = new ListenerAntiMonster();

		// Load and connect mysql
		this.mysql = new MySQL();
		this.mysql.connect();

		// Load Name and Tab prefix
		this.name = this.getConfig("config").getString("name");
		this.tabprefix = this.getConfig("config").getString("tabprefix");
		this.atall = this.getConfig("config").getString("at_all");
		this.atteam = this.getConfig("config").getString("at_team");

		// Load teamtypes and teams
		List<String> teams = this.getConfig("teams").getStringList("teams");
		List<TeamType> types =
				teams.stream().map(TeamType::deserialize).collect(Collectors.toList());
		AtomicReference<TeamType> spec = new AtomicReference<>();
		new ArrayList<>(types).forEach(type -> {
			if (type.getDisplayNameKey().equals("SPECTATOR")) {
				types.remove(type);
				spec.set(type);
			}
			if (!type.isEnabled()) {
				types.remove(type);
			}
		});
		this.teamManager = new DefaultTeamManager().loadSpectator(spec.get());
		if (spec.get() == null) {
			TeamType.getTypes().remove(TeamType.SPECTATOR);
			this.teamManager.getSpectator().getType().save();
		}
		types.forEach(type -> this.teamManager.getOrCreateTeam(type));
		this.maxPlayers = 0;
		for (Team team : this.teamManager.getTeams()) {
			this.maxPlayers += team.getType().getMaxPlayers();
		}
		// Set gamestate
		this.state = GameState.UNKNOWN;

		// Init all GameStates
		this.lobby = new Lobby();
		this.ingame = new Ingame();
		this.endgame = new Endgame();

		// I've to do this so the listener may be registered
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerInterface(VoidWorldPluginLoader.class);
		try {
			pm.loadPlugin(VoidWorldPluginLoader.file);
		} catch (UnknownDependencyException | InvalidPluginException |
				InvalidDescriptionException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		UserAPI.getInstance().removeModifier(userModifier);
		this.tickTask.cancel();
		this.mysql.disconnect();
	}

	@Override
	public void onEnable() {

		for (String world : this.getConfig("worlds").getStringList("worlds")) {
			if (Bukkit.getWorld(world) == null)
				Bukkit.createWorld(new WorldCreator(world));
		}

		// Load Maps
		this.mapManager = new DefaultMapManager();

		this.lobby.recalculateMap();
		this.lobby.recalculateEpGlitch();

		// Enable void worlds
		// Bukkit.getWorlds().forEach(world -> {
		// listenerVoidWorld.handle(new WorldInitEvent(world));
		// });

		// load user modifier
		this.userModifier = new WBUserModifier();
		UserAPI.getInstance().addModifier(userModifier);

		// Tick task for custom scheduler
		this.tickTask = new BukkitRunnable() {

			@Override
			public void run() {
				currentTick++;
				for (SchedulerTask s : new ArrayList<>(schedulers)) {
					if (s.canExecute()) {
						s.run();
					}
				}
			}

		}.runTaskTimer(this, 0, 1);

		// Register default listeners
		WoolBattle.registerListeners(this.listenerInventoryClick);
		WoolBattle.registerListeners(this.listenerInventoryClose);
		WoolBattle.registerListeners(this.listenerPlayerInteract);
		WoolBattle.registerListeners(this.listenerFoodLevelChange);
		WoolBattle.registerListeners(this.listenerWeatherChange);
		WoolBattle.registerListeners(this.listenerLaunchable);
		WoolBattle.registerListeners(this.listenerChat);
		WoolBattle.registerListeners(this.listenerAntiMonster);

		// Load worlds (At serverstart there are no worlds but if the plugin
		// gets
		// reloaded there are)
		// loadWorlds();

		// Enable Lobby
		this.lobby.enable();

		// Enable commands
		CommandAPI.getInstance().register(new CommandDisableStats());
		CommandAPI.getInstance().register(new CommandFix());
		CommandAPI.getInstance().register(new CommandIsStats());
		CommandAPI.getInstance().register(new CommandSetMap());
		CommandAPI.getInstance().register(new CommandSettings());
		CommandAPI.getInstance().register(new CommandTimer());
		CommandAPI.getInstance().register(new CommandTroll());
		CommandAPI.getInstance().register(new CommandVoteLifes());
		CommandAPI.getInstance().register(new CommandWoolBattle());

		CommandAPI.getInstance().register(new CommandSetTeam());
		CommandAPI.getInstance().register(new CommandSetLifes());
		CommandAPI.getInstance().register(new CommandRevive());

		new BukkitRunnable() {
			@Override
			public void run() {
				CloudNetLink.update();
			}
		}.runTaskTimerAsynchronously(WoolBattle.getInstance(), 10, 10);
	}

	public Map getMap() {
		return this.baseMap == null ? this.map : this.baseMap;
	}

	public void setMap(Map map) {
		this.map = map;
		WBUser.onlineUsers().forEach(this::setMap);
	}

	public void setMap(WBUser user) {
		if (this.getLobby().isEnabled()) {
			Scoreboard sb = new Scoreboard(user);
			ScoreboardTeam team = sb.getTeam(ObjectiveTeam.MAP.getKey());
			Map m = this.getMap();
			Component suffix =
					m == null ? text("No Maps").color(NamedTextColor.RED) : text(m.getName());
			team.setSuffix(suffix);
		}
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public boolean isEpGlitch() {
		return this.epGlitch;
	}

	public void setEpGlitch(boolean glitch) {
		this.epGlitch = glitch;
		WBUser.onlineUsers().forEach(this::setEpGlitch);
	}

	public void setEpGlitch(WBUser user) {
		Scoreboard sb = new Scoreboard(user);
		ScoreboardTeam team = sb.getTeam(ObjectiveTeam.EP_GLITCH.getKey());
		Component suffix = this.epGlitch
				? Message.EP_GLITCH_ON.getMessage(user)
				: Message.EP_GLITCH_OFF.getMessage(user);
		team.setSuffix(suffix);
	}

	public void setOnline(WBUser user) {
		Scoreboard sb = new Scoreboard(user);
		ScoreboardTeam team = sb.getTeam(ObjectiveTeam.ONLINE.getKey());
		Component suffix = text(Bukkit.getOnlinePlayers().size());
		team.setSuffix(suffix);
	}

	public int getCurrentTick() {
		return this.currentTick;
	}

	public PluginClassLoader getPluginClassLoader() {
		return this.pluginClassLoader;
	}

	public Lobby getLobby() {
		return this.lobby;
	}

	public Ingame getIngame() {
		return this.ingame;
	}

	public TeamManager getTeamManager() {
		return this.teamManager;
	}

	public Endgame getEndgame() {
		return this.endgame;
	}

	public GameState getGameState() {
		return this.state;
	}

	public Collection<SchedulerTask> getSchedulers() {
		return this.schedulers;
	}

	public MapManager getMapManager() {
		return this.mapManager;
	}

	@Override
	public String getCommandPrefix() {
		return this.reloadConfig("config").getString(Config.COMMAND_PREFIX);
	}

	public boolean disableStats() {
		boolean old = StatsLink.enabled;
		StatsLink.enabled = false;
		return old;
	}

	public final void sendMessage(Message msg, Object... replacements) {
		this.sendMessage(msg, (u) -> replacements);
	}

	public final void sendMessage(Message msg,
			Function<ILanguagedCommandExecutor, Object[]> function) {
		for (WBUser user : WBUser.onlineUsers()) {
			user.user().sendMessage(msg, function.apply(user.user()));
		}
		BukkitCommandExecutor e = new BukkitCommandExecutor(Bukkit.getConsoleSender());
		e.sendMessage(msg, function.apply(e));
	}

	public PerkRegistry perkRegistry() {
		return perkRegistry;
	}
}
