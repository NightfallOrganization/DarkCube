package eu.darkcube.system.lobbysystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import eu.darkcube.system.GameState;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.loader.ReflectionClassLoader;
import eu.darkcube.system.lobbysystem.command.CommandLobbysystem;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBuild;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerGrapplingHook;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerHookArrow;
import eu.darkcube.system.lobbysystem.listener.*;
import eu.darkcube.system.lobbysystem.npc.DailyRewardNPC;
import eu.darkcube.system.lobbysystem.npc.WoolBattleNPC;
import eu.darkcube.system.lobbysystem.pserver.PServerJoinOnStart;
import eu.darkcube.system.lobbysystem.pserver.PServerSupport;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.AsyncExecutor;
import eu.darkcube.system.lobbysystem.util.DataManager;
import eu.darkcube.system.lobbysystem.util.DependencyManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public class Lobby extends Plugin {

	private static Lobby instance;
	private DataManager dataManager;
	private NPCPool npcPool;
	private NPC woolbattleNpc;
	private NPC dailyRewardNpc;
	private PServerJoinOnStart pServerJoinOnStart;

	public Lobby() {
		Lobby.instance = this;
	}

	@Override
	public void onLoad() {
		new DependencyManager(new ReflectionClassLoader(this)).loadDependencies();
	}

	@Override
	public void onEnable() {
		this.npcPool = NPCPool.builder(this).spawnDistance(50).actionDistance(45).tabListRemoveTicks(40L).build();

		AsyncExecutor.start();

		PServerSupport.init();
		// Load all messages

		try {
			Language.GERMAN.registerLookup(this.getClassLoader(), "messages_de.properties",
					s -> Message.KEY_PREFIX + s);
			Language.ENGLISH.registerLookup(this.getClassLoader(), "messages_en.properties",
					s -> Message.KEY_PREFIX + s);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		List<String> languageEntries = new ArrayList<>();
		languageEntries
				.addAll(Arrays.asList(Message.values()).stream().map(Message::getKey).collect(Collectors.toList()));
		languageEntries.addAll(Arrays.asList(Item.values()).stream().map(i -> Message.PREFIX_ITEM + i.getKey())
				.collect(Collectors.toList()));
		languageEntries.addAll(Arrays.asList(Item.values()).stream().filter(i -> i.getBuilder().getLores().size() > 0)
				.map(i -> Message.PREFIX_ITEM + Message.PREFIX_LORE + i.getKey()).collect(Collectors.toList()));
		Language.validateEntries(languageEntries.toArray(new String[languageEntries.size()]),
				s -> Message.KEY_PREFIX + s);

		UserWrapper.init();

		this.dataManager = new DataManager();
		this.woolbattleNpc = WoolBattleNPC.create();
		this.dailyRewardNpc = DailyRewardNPC.create();

		CommandAPI.enable(this, new CommandLobbysystem());
		CommandAPI.enable(this, new CommandBuild());

		for (World world : Bukkit.getWorlds()) {
			world.setGameRuleValue("randomTickSpeed", "0");
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setFullTime(6000);
		}

		new BukkitRunnable() {
			private final Collection<String> woolbattleTasks = Lobby.this.getDataManager().getWoolBattleTasks();

			@Override
			public void run() {
				for (String task : this.woolbattleTasks) {
					if (CloudNetDriver.getInstance().getServiceTaskProvider().isServiceTaskPresent(task)) {
						ServiceTask serviceTask = CloudNetDriver.getInstance().getServiceTaskProvider()
								.getServiceTask(task);
						Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance()
								.getCloudServiceProvider().getCloudServices(serviceTask.getName());

						int freeServices = 0;
						for (ServiceInfoSnapshot service : services) {
							GameState state = GameState
									.fromString(service.getProperty(BridgeServiceProperty.STATE).orElse(null));
							if (state == GameState.LOBBY || state == GameState.UNKNOWN) {
								freeServices++;
							}
						}
						if (freeServices < serviceTask.getMinServiceCount()) {
							ServiceInfoSnapshot snap = CloudNetDriver.getInstance().getCloudServiceFactory()
									.createCloudService(serviceTask);
							CloudNetDriver.getInstance().getCloudServiceProvider(snap)
									.setCloudServiceLifeCycle(ServiceLifeCycle.RUNNING);
						}
					} else {
						Lobby.this.sendMessage("§cCould not find service task named " + task);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 30 * 20, 30 * 20);

		for (Player p : Bukkit.getOnlinePlayers()) {
			UserWrapper.loadUser(p.getUniqueId());
		}

		new ListenerSettingsJoin();
		new ListenerScoreboard();
		new ListenerQuit();
		new ListenerBlock();
		new ListenerDoublejump();
		new ListenerDamage();
		new ListenerInventoryClick();
		new ListenerInventoryClose();
		new ListenerInteract();
		new ListenerLobbySwitcher();
		new ListenerWoolBattleNPC();
		new ListenerDailyRewardNPC();
		new ListenerMinigameServer();
		new ListenerItemDropPickup();
		new ListenerFish();
		new ListenerGadget();
		new ListenerDailyReward();
		new ListenerHookArrow();
		new ListenerGrapplingHook();
		new ListenerBoostPlate();
		new ListenerWeather();
		if (PServerSupport.isSupported()) {
			new ListenerPServer();
			this.pServerJoinOnStart = new PServerJoinOnStart();
		}
		new ListenerPhysics();

		for (Player p : Bukkit.getOnlinePlayers()) {
			ListenerSettingsJoin.instance.handle(new PlayerJoinEvent(p, "Custom"));
			ListenerScoreboard.instance.handle(new PlayerJoinEvent(p, "Custom"));
			User user = UserWrapper.getUser(p.getUniqueId());
			user.setGadget(user.getGadget());
		}
		new ListenerBorder();

		if (PServerSupport.isSupported()) {
			SkullCache.register();
		}

	}

	@Override
	public void onDisable() {

		if (PServerSupport.isSupported()) {
			SkullCache.unregister();
			this.pServerJoinOnStart.unregister();
		}
		for (User user : new HashSet<>(UserWrapper.users.values())) {
			this.getDataManager().setUserPos(user.getUniqueId(),
					UUIDManager.getPlayerByUUID(user.getUniqueId()).getLocation());
			UserWrapper.unloadUser(user);
		}

		AsyncExecutor.shutdown();
	}

	public PServerJoinOnStart getPServerJoinOnStart() {
		return this.pServerJoinOnStart;
	}

	public NPC getWoolBattleNPC() {
		return this.woolbattleNpc;
	}

	public NPC getDailyRewardNpc() {
		return this.dailyRewardNpc;
	}

	public DataManager getDataManager() {
		return this.dataManager;
	}

	public NPCPool getNpcPool() {
		return this.npcPool;
	}

	@Override
	public String getCommandPrefix() {
		return "§aLobbySystem";
	}

	public static Lobby getInstance() {
		return Lobby.instance;
	}
}
