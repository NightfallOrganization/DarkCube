/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.CustomChunkGenerator;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import eu.darkcube.minigame.woolbattle.command.CommandDisableStats;
import eu.darkcube.minigame.woolbattle.command.CommandFix;
import eu.darkcube.minigame.woolbattle.command.CommandIsStats;
import eu.darkcube.minigame.woolbattle.command.CommandLanguage;
import eu.darkcube.minigame.woolbattle.command.CommandRevive;
import eu.darkcube.minigame.woolbattle.command.CommandSetLifes;
import eu.darkcube.minigame.woolbattle.command.CommandSetMap;
import eu.darkcube.minigame.woolbattle.command.CommandSetTeam;
import eu.darkcube.minigame.woolbattle.command.CommandSettings;
import eu.darkcube.minigame.woolbattle.command.CommandTimer;
import eu.darkcube.minigame.woolbattle.command.CommandTroll;
import eu.darkcube.minigame.woolbattle.command.CommandVoteLifes;
import eu.darkcube.minigame.woolbattle.command.CommandWoolBattle;
import eu.darkcube.minigame.woolbattle.game.Endgame;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.ListenerAntiMonster;
import eu.darkcube.minigame.woolbattle.listener.ListenerChat;
import eu.darkcube.minigame.woolbattle.listener.ListenerFoodLevelChange;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClick;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClose;
import eu.darkcube.minigame.woolbattle.listener.ListenerLaunchable;
import eu.darkcube.minigame.woolbattle.listener.ListenerPlayerInteract;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerWeatherChange;
import eu.darkcube.minigame.woolbattle.map.DefaultMapManager;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapManager;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.team.DefaultTeamManager;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamManager;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.ConsoleUser;
import eu.darkcube.minigame.woolbattle.user.DefaultUserWrapper;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.user.UserWrapper;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.CloudNetLink;
import eu.darkcube.minigame.woolbattle.util.DependencyManager;
import eu.darkcube.minigame.woolbattle.util.DependencyManager.Dependency;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleChatColor;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleDyeColor;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleLanguage;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleMap;
import eu.darkcube.minigame.woolbattle.util.convertingrule.ConvertingRuleTeam;
import eu.darkcube.minigame.woolbattle.util.scheduler.SchedulerTask;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Objective;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPluginLoader;
import eu.darkcube.system.GameState;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.loader.ReflectionClassLoader;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.IDataManager;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;

public class WoolBattle extends Plugin {

	public String name;

	public String tabprefix;

	public String atall;

	public String atteam;

	private UserWrapper userWrapper;

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

	public Integer baseLifes;

	public Map baseMap;

	private ListenerChat listenerChat;

	private MySQL mysql;

	private int maxPlayers;

	private boolean epGlitch = true;

	private int currentTick;

	private static WoolBattle instance;

	public static String tab_header;

	public static String tab_footer;

	public WoolBattle() {
		WoolBattle.instance = this;
		System.setProperty("file.encoding", "UTF-8");
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
		languageEntries.addAll(Arrays.asList(Item.values()).stream()
				.filter(i -> i.getBuilder().getLores().size() > 0)
				.map(i -> Message.ITEM_PREFIX + Message.LORE_PREFIX + i.getKey())
				.collect(Collectors.toList()));
		Language.validateEntries(languageEntries.toArray(new String[languageEntries.size()]),
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
		WoolBattle.tab_header = this.getConfig("config").getString(Config.TAB_HEADER);
		WoolBattle.tab_footer = this.getConfig("config").getString(Config.TAB_FOOTER);

		this.saveDefaultConfig("mysql");
		this.createConfig("mysql");

		this.saveDefaultConfig("worlds");
		this.createConfig("worlds");

		this.saveDefaultConfig("spawns");
		this.createConfig("spawns");

		this.schedulers = new ArrayList<>();

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
				teams.stream().map(json -> TeamType.deserialize(json)).collect(Collectors.toList());
		AtomicReference<TeamType> spec = new AtomicReference<>();
		new ArrayList<>(types).forEach(type -> {
			if (type.getDisplayNameKey().equals("SPECTATOR")) {
				types.remove(type);
				spec.set(type);
			}
			if (!type.isEnabled()) {
				types.remove(type);
				return;
			}
		});
		this.teamManager = new DefaultTeamManager().loadSpectator(spec.get());
		if (spec.get() == null) {
			TeamType.getTypes().remove(TeamType.SPECTATOR);
			this.teamManager.getSpectator().getType().save();
		}
		types.forEach(type -> {
			this.teamManager.getOrCreateTeam(type);
		});
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

		// Gotta do this so the listener may be registered
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

		// load user wrapper
		this.userWrapper = new DefaultUserWrapper();

		// Tick task for custom scheduler
		this.tickTask = new BukkitRunnable() {

			@Override
			public void run() {
				currentTick++;
				for (SchedulerTask s : new ArrayList<>(WoolBattle.this.schedulers)) {
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

		// Enable Lobbystate
		this.lobby.enable();

		// Enable commands
		CommandAPI.enable(this, new CommandDisableStats());
		CommandAPI.enable(this, new CommandFix());
		CommandAPI.enable(this, new CommandIsStats());
		CommandAPI.enable(this, new CommandLanguage());
		CommandAPI.enable(this, new CommandSetMap());
		CommandAPI.enable(this, new CommandSettings());
		CommandAPI.enable(this, new CommandTimer());
		CommandAPI.enable(this, new CommandTroll());
		CommandAPI.enable(this, new CommandVoteLifes());
		CommandAPI.enable(this, new CommandWoolBattle());

		eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandSetTeam());
		eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandSetLifes());
		eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandRevive());

		new BukkitRunnable() {

			@Override
			public void run() {
				CloudNetLink.update();
			}

		}.runTaskTimerAsynchronously(WoolBattle.getInstance(), 10, 10);
		new BukkitRunnable() {

			@Override
			public void run() {
				for (User user : WoolBattle.this.userWrapper.getUsers()) {
					MySQL.saveUserData(user);
				}
			}

		}.runTaskTimerAsynchronously(this, 0, 100);
		// new BukkitRunnable() {
		// @Override
		// public void run() {
		// for (Player p : Bukkit.getOnlinePlayers()) {
		// if (p.getOpenInventory().getType() != InventoryType.CRAFTING) {
		// p.updateInventory();
		// }
		// }
		// }
		// }.runTaskTimer(Main.getInstance(), 20, 20);
	}

	@Override
	public void onDisable() {
		// Server-Client sync problem fixing -> player has to rejoin
		// Bukkit.getOnlinePlayers().forEach(p -> {
		// p.kickPlayer("§cReloading");
		// });
		this.tickTask.cancel();
		this.mysql.disconnect();
	}

	public Map getMap() {
		return this.baseMap == null ? this.map : this.baseMap;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public boolean isEpGlitch() {
		return this.epGlitch;
	}

	public void setMap(Map map) {
		this.map = map;
		if (this.getUserWrapper() != null) {
			this.getUserWrapper().getUsers().forEach(p -> {
				this.setMap(p);
			});
		}
	}

	public void setEpGlitch(boolean glitch) {
		this.epGlitch = glitch;
		if (this.getUserWrapper() != null) {
			this.getUserWrapper().getUsers().forEach(p -> {
				this.setEpGlitch(p);
			});
		}
	}

	public void setEpGlitch(User user) {
		Scoreboard sb = new Scoreboard(user);
		eu.darkcube.minigame.woolbattle.util.scoreboard.Team team =
				sb.getTeam(ObjectiveTeam.EP_GLITCH.getKey());
		String suffix = this.epGlitch
				? Message.EP_GLITCH_ON.getMessage(user)
				: Message.EP_GLITCH_OFF.getMessage(user);
		team.setSuffix(suffix);
	}

	public void setMap(User user) {
		if (this.getLobby().isEnabled()) {
			Scoreboard sb = new Scoreboard(user);
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team =
					sb.getTeam(ObjectiveTeam.MAP.getKey());
			Map m = this.getMap();
			String suffix = m == null ? "§cNo Maps" : m.getName();
			team.setSuffix(suffix);
		}
	}

	public void setOnline(User user) {
		Scoreboard sb = new Scoreboard(user);
		eu.darkcube.minigame.woolbattle.util.scoreboard.Team team =
				sb.getTeam(ObjectiveTeam.ONLINE.getKey());
		String suffix = Integer.toString(Bukkit.getOnlinePlayers().size());
		team.setSuffix(suffix);
	}

	public int getCurrentTick() {
		return this.currentTick;
	}

	public static final void initScoreboard(Scoreboard sb, User owner) {
		// Spectator is not included in "Team"
		Collection<Team> teams =
				new HashSet<>(WoolBattle.getInstance().getTeamManager().getTeams());
		teams.add(WoolBattle.getInstance().getTeamManager().getSpectator());
		for (Team t : teams) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team =
					sb.createTeam(t.getType().getScoreboardTag());
			team.setPrefix(t.getPrefix());
		}
		for (ScoreboardObjective obj : ScoreboardObjective.values()) {
			Objective o = sb.createObjective(obj.getKey(), IScoreboardCriteria.b);
			o.setDisplayName(Message.getMessage(obj.getMessageKey(), owner.getLanguage()));
		}
		for (ObjectiveTeam team : ObjectiveTeam.values()) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team t = sb.createTeam(team.getKey());
			t.setPrefix(Message.getMessage(team.getMessagePrefix(), owner.getLanguage()));
			t.setSuffix(Message.getMessage(team.getMessageSuffix(), owner.getLanguage()));
		}
	}

	public UserWrapper getUserWrapper() {
		return this.userWrapper;
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

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new ChunkGenerator() {

			@Override
			public ChunkData generateChunkData(World world, Random random, int x, int z,
					BiomeGrid biome) {
				return this.createChunkData(world);
			}

		};
	}

	public boolean disableStats() {
		boolean old = StatsLink.enabled;
		StatsLink.enabled = false;
		return old;
	}

	public final void loadWorlds() {
		for (String world : this.getConfig("worlds").getStringList("worlds")) {
			this.loadWorld(world);
		}
	}

	public final void loadWorld(World world) {
		CraftWorld w = (CraftWorld) world;
		// Setting gamerules
		w.getHandle().tracker = new EntityTracker(w.getHandle());
		w.setDifficulty(Difficulty.NORMAL);
		w.setKeepSpawnInMemory(false);
		w.setFullTime(0);
		w.setTime(6000);
		w.setGameRuleValue("commandBlockOutput", "false");
		w.setGameRuleValue("doDaylightCycle", "false");
		w.setGameRuleValue("doEntityDrops", "false");
		w.setGameRuleValue("doFireTick", "false");
		w.setGameRuleValue("doMobLoot", "false");
		w.setGameRuleValue("doMobSpawning", "false");
		w.setGameRuleValue("doTileDrops", "true");
		w.setGameRuleValue("keepInventory", "true");
		w.setGameRuleValue("logAdminCommands", "true");
		w.setGameRuleValue("mobGriefing", "false");
		w.setGameRuleValue("naturalRegeneration", "false");
		w.setGameRuleValue("randomTickSpeed", "0");
		w.setGameRuleValue("reducedDebugInfo", "false");
		w.setGameRuleValue("sendCommandFeedback", "true");
		w.setGameRuleValue("showDeathMessages", "false");
		try {
			// Setting all chunkgenerator fields for world
			this.sendConsole("Preparing void world generation for world '" + world.getName() + "'");
			w.getHandle().generator =
					this.getDefaultWorldGenerator(world.getName(), world.getName());
			Field field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("dataManager");
			field.setAccessible(true);
			IDataManager manager = (IDataManager) field.get(w.getHandle());
			IChunkProvider gen = new CustomChunkGenerator(w.getHandle(), w.getHandle().getSeed(),
					w.getHandle().generator);
			gen = new ChunkProviderServer(w.getHandle(),
					manager.createChunkLoader(w.getHandle().worldProvider), gen);
			w.getHandle().chunkProviderServer = (ChunkProviderServer) gen;
			field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("chunkProvider");
			field.setAccessible(true);
			field.set(w.getHandle(), gen);
			field = w.getClass().getDeclaredField("generator");
			field.setAccessible(true);
			field.set(w, w.getHandle().generator);
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	public final void loadWorld(String world) {
		if (!new File(this.getServer().getWorldContainer(), world).exists() && !new File(
				this.getServer().getWorldContainer().getParent(), world).exists()) {
			System.out.println("World " + world + " not found");
			return;
		}
		if (Bukkit.getWorld(world) == null) {
			// && !new File(Bukkit.getWorldContainer(), world).exists()) {
			try {
				WorldCreator creator = new WorldCreator(world).generator(
						this.getDefaultWorldGenerator(world, world));
				creator.createWorld();
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		} else {
			this.loadWorld(Bukkit.getWorld(world));
		}
	}

	public static final WoolBattle getInstance() {
		return WoolBattle.instance;
	}

	public final void sendMessage(Message msg, Object... replacements) {
		this.sendMessage(msg, (u) -> replacements);
	}

	public final <T> void sendMessage(Message msg, Function<User, Object[]> function) {
		for (User user : this.getUserWrapper().getUsers()) {
			Object[] replacements = new Object[0];
			replacements = Arrays.addAfter(replacements, function.apply(user));
			user.getBukkitEntity().sendMessage(msg.getMessage(user, replacements));
		}
		User console = new ConsoleUser();
		Object[] replacements = new Object[0];
		replacements = Arrays.addAfter(replacements, function.apply(console));
		this.sendConsole(msg.getMessage(console.getLanguage(), replacements));
	}

	public static final void registerListeners(Listener... listener) {
		for (Listener l : listener) {
			if (l instanceof RegisterNotifyListener) {
				((RegisterNotifyListener) l).registered();
			}
			WoolBattle.getInstance().getServer().getPluginManager()
					.registerEvents(l, WoolBattle.getInstance());
		}
	}

	public static final void unregisterListeners(Listener... listener) {
		for (Listener l : listener) {
			if (l instanceof RegisterNotifyListener) {
				((RegisterNotifyListener) l).unregistered();
			}
			HandlerList.unregisterAll(l);
		}
	}

}
