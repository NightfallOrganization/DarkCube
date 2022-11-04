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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.CustomChunkGenerator;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;
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
import eu.darkcube.minigame.woolbattle.listener.ListenerChat;
import eu.darkcube.minigame.woolbattle.listener.ListenerFoodLevelChange;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClick;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClose;
import eu.darkcube.minigame.woolbattle.listener.ListenerLaunchable;
import eu.darkcube.minigame.woolbattle.listener.ListenerPlayerInteract;
import eu.darkcube.minigame.woolbattle.listener.ListenerVoidWorld;
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
import eu.darkcube.minigame.woolbattle.util.CustomEntityTracker;
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
import eu.darkcube.system.GameState;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.loader.PluginClassLoader;
import eu.darkcube.system.loader.ReflectionClassLoader;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.IDataManager;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;

public class Main extends Plugin {

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
	private ListenerVoidWorld listenerVoidWorld;
	private ListenerPlayerInteract listenerPlayerInteract;
	private ListenerInventoryClick listenerInventoryClick;
	private ListenerInventoryClose listenerInventoryClose;
	private ListenerFoodLevelChange listenerFoodLevelChange;
	private ListenerWeatherChange listenerWeatherChange;
	private ListenerLaunchable listenerLaunchable;
	public Integer baseLifes;
	public Map baseMap;
	private ListenerChat listenerChat;
	private MySQL mysql;
	private int maxPlayers;
	private boolean epGlitch = true;

	private static Main instance;
	public static String tab_header;
	public static String tab_footer;

	public Main() {
		instance = this;
		System.setProperty("file.encoding", "UTF-8");
	}

	@Override
	public void onLoad() {

		pluginClassLoader = new ReflectionClassLoader(this);
		new DependencyManager(this).loadDependencies(Dependency.values());

		// Load all messages
		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties", Message.KEY_MODFIIER);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties", Message.KEY_MODFIIER);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		List<String> languageEntries = new ArrayList<>();
		languageEntries.addAll(Arrays.asList(Message.values()).stream().map(Message::getKey).collect(Collectors.toList()));
		languageEntries.addAll(Arrays.asList(Item.values()).stream().map(i -> Message.ITEM_PREFIX
						+ i.getKey()).collect(Collectors.toList()));
		languageEntries.addAll(Arrays.asList(Item.values()).stream().filter(i -> i.getBuilder().getLores().size() > 0).map(i -> Message.ITEM_PREFIX
						+ Message.LORE_PREFIX
						+ i.getKey()).collect(Collectors.toList()));
		Language.validateEntries(languageEntries.toArray(new String[languageEntries.size()]), s -> Message.KEY_PREFIX
						+ s);

//		for (Language language : Language.values()) {
//			if (language.getBundle() != null) {
//				for (Message message : Message.values()) {
//					if (message.getMessage(language).startsWith(message.name())) {
//						sendConsole("§cCould not load message " + message.name()
//										+ " in language " + language.name());
//					}
//				}
//				for (Item item : Item.values()) {
//					String id = ItemManager.getItemId(item);
//					try {
//						language.getBundle().getString(id);
//					} catch (Exception ex) {
//						sendConsole("§cCould not load item name " + id
//										+ " in language " + language.name());
//					}
//					if (item.getBuilder().getLores().size() != 0) {
//						id = item.name();
//						try {
//							language.getBundle().getString(Message.ITEM_PREFIX
//											+ Message.LORE_PREFIX + id);
//						} catch (Exception ex) {
//							sendConsole("§cCould not load item lore "
//											+ Message.ITEM_PREFIX
//											+ Message.LORE_PREFIX + id
//											+ " in language "
//											+ language.name());
//						}
//					}
//				}
//				for (ScoreboardObjective obj : ScoreboardObjective.values()) {
//					try {
//						language.getBundle().getString(obj.getMessageKey());
//					} catch (Exception ex) {
//						sendConsole("§cCould not load scoreboard objective "
//										+ obj.getMessageKey() + " in language "
//										+ language.name());
//					}
//				}
//				for (ObjectiveTeam team : ObjectiveTeam.values()) {
//					try {
//						language.getBundle().getString(team.getMessagePrefix());
//					} catch (Exception ex) {
//						sendConsole("§cCould not load objective team prefix "
//										+ team.getMessagePrefix()
//										+ " in language " + language.name());
//					}
//					try {
//						language.getBundle().getString(team.getMessageSuffix());
//					} catch (Exception ex) {
//						sendConsole("§cCould not load objective team suffix "
//										+ team.getMessageSuffix()
//										+ " in language " + language.name());
//					}
//				}
//			} else {
//				sendConsole("§cLanguage bundle for language " + language.name()
//								+ " could not be found!");
//			}
//		}

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
		tab_header = getConfig("config").getString(Config.TAB_HEADER);
		tab_footer = getConfig("config").getString(Config.TAB_FOOTER);

		this.saveDefaultConfig("mysql");
		this.createConfig("mysql");

		this.saveDefaultConfig("worlds");
		this.createConfig("worlds");

		this.saveDefaultConfig("spawns");
		this.createConfig("spawns");

		this.schedulers = new ArrayList<>();

		// Init listeners
		this.listenerVoidWorld = new ListenerVoidWorld();
		this.listenerInventoryClose = new ListenerInventoryClose();
		this.listenerInventoryClick = new ListenerInventoryClick();
		this.listenerPlayerInteract = new ListenerPlayerInteract();
		this.listenerFoodLevelChange = new ListenerFoodLevelChange();
		this.listenerWeatherChange = new ListenerWeatherChange();
		this.listenerLaunchable = new ListenerLaunchable();
		this.listenerChat = new ListenerChat();

		// Load and connect mysql
		mysql = new MySQL();
		mysql.connect();

		// Load Name and Tab prefix
		name = getConfig("config").getString("name");
		tabprefix = getConfig("config").getString("tabprefix");
		atall = getConfig("config").getString("at_all");
		atteam = getConfig("config").getString("at_team");

		// Load teamtypes and teams
		List<String> teams = getConfig("teams").getStringList("teams");
		List<TeamType> types = teams.stream().map(json -> TeamType.deserialize(json)).collect(Collectors.toList());
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
		teamManager = new DefaultTeamManager().loadSpectator(spec.get());
		if (spec.get() == null) {
			TeamType.getTypes().remove(TeamType.SPECTATOR);
			teamManager.getSpectator().getType().save();
		}
		types.forEach(type -> {
			teamManager.getOrCreateTeam(type);
		});
		maxPlayers = 0;
		for (Team team : teamManager.getTeams()) {
			maxPlayers += team.getType().getMaxPlayers();
		}
		// Set gamestate
		state = GameState.UNKNOWN;

		// Init all GameStates
		lobby = new Lobby();
		ingame = new Ingame();
		endgame = new Endgame();
	}

	@Override
	public void onEnable() {

		for (String world : getConfig("worlds").getStringList("worlds")) {
			if (Bukkit.getWorld(world) == null)
				Bukkit.createWorld(new WorldCreator(world));
		}

		// Load Maps
		mapManager = new DefaultMapManager();

		lobby.recalculateMap();
		lobby.recalculateEpGlitch();

		// Server-Client sync problem fixing -> player has to rejoin
//		Bukkit.getOnlinePlayers().forEach(p -> {
//			p.kickPlayer("§cReloading");
//		});

		// Enable void worlds
		Bukkit.getWorlds().forEach(world -> {
			listenerVoidWorld.handle(new WorldInitEvent(world));
		});

		// load user wrapper
		userWrapper = new DefaultUserWrapper();

		// Tick task for custom scheduler
		tickTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (SchedulerTask s : new ArrayList<>(schedulers)) {
					if (s.canExecute()) {
						s.run();
					}
				}
			}
		}.runTaskTimer(this, 0, 1);

		// Register default listeners
		registerListeners(listenerVoidWorld);
		registerListeners(listenerInventoryClick);
		registerListeners(listenerInventoryClose);
		registerListeners(listenerPlayerInteract);
		registerListeners(listenerFoodLevelChange);
		registerListeners(listenerWeatherChange);
		registerListeners(listenerLaunchable);
		registerListeners(listenerChat);
		listenerLaunchable.start();

		// Load worlds (At serverstart there are no worlds but if the plugin
		// gets
		// reloaded there are)
		loadWorlds();

		// Enable Lobbystate
		lobby.enable();

		// Enable commands
		CommandAPI.enable(this, new CommandDisableStats());
		CommandAPI.enable(this, new CommandFix());
		CommandAPI.enable(this, new CommandIsStats());
		CommandAPI.enable(this, new CommandLanguage());
//		CommandAPI.enable(this, new CommandSetLifesDeprecated());
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
		}.runTaskTimerAsynchronously(Main.getInstance(), 10, 10);
		new BukkitRunnable() {

			@Override
			public void run() {
				for (User user : userWrapper.getUsers()) {
					MySQL.saveUserData(user);
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 100);
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				for (Player p : Bukkit.getOnlinePlayers()) {
//					if (p.getOpenInventory().getType() != InventoryType.CRAFTING) {
//						p.updateInventory();
//					}
//				}
//			}
//		}.runTaskTimer(Main.getInstance(), 20, 20);
	}

	@Override
	public void onDisable() {
		// Server-Client sync problem fixing -> player has to rejoin
//		Bukkit.getOnlinePlayers().forEach(p -> {
//			p.kickPlayer("§cReloading");
//		});
		tickTask.cancel();
		mysql.disconnect();
	}

	public Map getMap() {
		return baseMap == null ? map : baseMap;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public boolean isEpGlitch() {
		return epGlitch;
	}

	public void setMap(Map map) {
		this.map = map;
		if (getUserWrapper() != null)
			getUserWrapper().getUsers().forEach(p -> {
				setMap(p);
			});
	}

	public void setEpGlitch(boolean glitch) {
		this.epGlitch = glitch;
		if (getUserWrapper() != null)
			getUserWrapper().getUsers().forEach(p -> {
				setEpGlitch(p);
			});
	}

	public void setEpGlitch(User user) {
		Scoreboard sb = new Scoreboard(user);
		eu.darkcube.minigame.woolbattle.util.scoreboard.Team team = sb.getTeam(ObjectiveTeam.EP_GLITCH.getKey());
		String suffix = epGlitch ? Message.EP_GLITCH_ON.getMessage(user)
						: Message.EP_GLITCH_OFF.getMessage(user);
		team.setSuffix(suffix);
	}

	public void setMap(User user) {
		if (getLobby().isEnabled()) {
			Scoreboard sb = new Scoreboard(user);
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team = sb.getTeam(ObjectiveTeam.MAP.getKey());
			String suffix = baseMap == null
							? (map == null ? "§cNo Maps" : map.getName())
							: baseMap.getName();
			team.setSuffix(suffix);
		}
	}

	public void setOnline(User user) {
		Scoreboard sb = new Scoreboard(user);
		eu.darkcube.minigame.woolbattle.util.scoreboard.Team team = sb.getTeam(ObjectiveTeam.ONLINE.getKey());
		String suffix = Integer.toString(Bukkit.getOnlinePlayers().size());
		team.setSuffix(suffix);
	}

	public static final void initScoreboard(Scoreboard sb, User owner) {
//		Spectator is not included in "Team"
		Collection<Team> teams = new HashSet<>(
						Main.getInstance().getTeamManager().getTeams());
		teams.add(Main.getInstance().getTeamManager().getSpectator());
		for (Team t : teams) {
			eu.darkcube.minigame.woolbattle.util.scoreboard.Team team = sb.createTeam(t.getType().getScoreboardTag());
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
		return userWrapper;
	}

	public PluginClassLoader getPluginClassLoader() {
		return pluginClassLoader;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public Ingame getIngame() {
		return ingame;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}

	public Endgame getEndgame() {
		return endgame;
	}

	public GameState getGameState() {
		return state;
	}

	public Collection<SchedulerTask> getSchedulers() {
		return schedulers;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	@Override
	public String getCommandPrefix() {
		return reloadConfig("config").getString(Config.COMMAND_PREFIX);
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName,
					String id) {
		return new ChunkGenerator() {
			@Override
			public ChunkData generateChunkData(World world, Random random,
							int x, int z, BiomeGrid biome) {
				return createChunkData(world);
			}
		};
	}

	public boolean disableStats() {
		boolean old = StatsLink.enabled;
		StatsLink.enabled = false;
		return old;
	}

	public final void loadWorlds() {
		for (String world : getConfig("worlds").getStringList("worlds")) {
			loadWorld(world);
		}
	}

	public final void loadWorld(World world) {
		boolean b = true;
		if (b)
			return;
		CraftWorld w = (CraftWorld) world;
		// Setting gamerules
		w.getHandle().tracker = new CustomEntityTracker(w.getHandle());
		w.setDifficulty(Difficulty.PEACEFUL);
		w.setKeepSpawnInMemory(false);
		w.setFullTime(0);
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
			sendConsole("Preparing void world generation for world '"
							+ world.getName() + "'");
			w.getHandle().generator = getDefaultWorldGenerator(world.getName(), world.getName());
			Field field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("dataManager");
			field.setAccessible(true);
			IDataManager manager = (IDataManager) field.get(w.getHandle());
			IChunkProvider gen = new CustomChunkGenerator(w.getHandle(),
							w.getHandle().getSeed(), w.getHandle().generator) {

			};
			gen = new ChunkProviderServer(w.getHandle(),
							manager.createChunkLoader(w.getHandle().worldProvider),
							gen) {
			};
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
		if (!new File(this.getServer().getWorldContainer(), world).exists()
						&& !new File(this.getServer().getWorldContainer().getParent(),
										world).exists()) {
			return;
		}
		if (Bukkit.getWorld(world) == null
						&& !new File(Bukkit.getWorldContainer(),
										world).exists()) {
			try {
				WorldCreator creator = new WorldCreator(
								world).generator(getDefaultWorldGenerator(world, world));
				creator.createWorld();
			} catch (NullPointerException ex) {
			}
		} else {
			if (new File(Bukkit.getWorldContainer(), world).exists()) {
				File serverfolder = new File(System.getProperty("user.dir"));
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(
								serverfolder, "bukkit.yml"));
				cfg.set("worlds." + world + ".generator", "WoolBattle");
				try {
					cfg.save(new File(serverfolder, "bukkit.yml"));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return;
			}
			loadWorld(Bukkit.getWorld(world));
		}
	}

	public static final Main getInstance() {
		return instance;
	}

	public final void sendMessage(Message msg, Object... replacements) {
		sendMessage(msg, (u) -> replacements);
	}

	public final <T> void sendMessage(Message msg,
					Function<User, Object[]> function) {
		for (User user : getUserWrapper().getUsers()) {
			Object[] replacements = new Object[0];
			replacements = Arrays.addAfter(replacements, function.apply(user));
			user.getBukkitEntity().sendMessage(msg.getMessage(user, replacements));
		}
		User console = new ConsoleUser();
		Object[] replacements = new Object[0];
		replacements = Arrays.addAfter(replacements, function.apply(console));
		sendConsole(msg.getMessage(console.getLanguage(), replacements));
	}

	public static final void registerListeners(Listener... listener) {
		for (Listener l : listener) {
			Main.getInstance().getServer().getPluginManager().registerEvents(l, Main.getInstance());
		}
	}

	public static final void unregisterListeners(Listener... listener) {
		for (Listener l : listener) {
			HandlerList.unregisterAll(l);
		}
	}
}