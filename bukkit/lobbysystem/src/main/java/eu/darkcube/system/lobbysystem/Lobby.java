/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem;

import com.github.unldenis.hologram.HologramPool;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceLifeCycle;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.NPCPool;
import eu.darkcube.system.loader.ReflectionClassLoader;
import eu.darkcube.system.lobbysystem.command.CommandLobbysystem;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBuild;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerGrapplingHook;
import eu.darkcube.system.lobbysystem.gadget.listener.ListenerHookArrow;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRManager;
import eu.darkcube.system.lobbysystem.listener.*;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import eu.darkcube.system.lobbysystem.npc.DailyRewardNPC;
import eu.darkcube.system.lobbysystem.npc.WoolBattleNPC;
import eu.darkcube.system.lobbysystem.pserver.PServerJoinOnStart;
import eu.darkcube.system.lobbysystem.pserver.PServerSupport;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.*;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.Language;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Lobby extends Plugin {

	private static Lobby instance;
	private DataManager dataManager;
	private NPCPool npcPool;
	private NPC woolbattleNpc;
	private NPC dailyRewardNpc;
	private PServerJoinOnStart pServerJoinOnStart;
	private JaRManager jaRManager;
	private HologramPool hologramPool;

	public Lobby() {
		Lobby.instance = this;
	}

	public static Lobby getInstance() {
		return Lobby.instance;
	}

	@Override
	public void onLoad() {
		new DependencyManager(new ReflectionClassLoader(this)).loadDependencies();
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().stream().map(UserAPI.getInstance()::getUser)
				.map(UserWrapper::fromUser).forEach(LobbyUser::stopJaR);
		if (PServerSupport.isSupported()) {
			SkullCache.unregister();
			this.pServerJoinOnStart.unregister();
		}
		ConnectorNPC.clear();
	}

	@Override
	public void onEnable() {
		this.npcPool =
				NPCPool.builder(this).spawnDistance(50).actionDistance(45).tabListRemoveTicks(40L)
						.build();
		this.hologramPool = new HologramPool(this, 50, 0, 0);

		UserWrapper userWrapper = new UserWrapper();
		UserAPI.getInstance().addModifier(userWrapper);
		userWrapper.beginMigration();

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
		languageEntries.addAll(
				Arrays.stream(Message.values()).map(Message::getKey).collect(Collectors.toList()));
		languageEntries.addAll(
				Arrays.stream(Item.values()).map(i -> Message.PREFIX_ITEM + i.getKey())
						.collect(Collectors.toList()));
		languageEntries.addAll(
				Arrays.stream(Item.values()).filter(i -> i.getBuilder().lore().size() > 0)
						.map(i -> Message.PREFIX_ITEM + Message.PREFIX_LORE + i.getKey())
						.collect(Collectors.toList()));
		Language.validateEntries(languageEntries.toArray(new String[0]),
				s -> Message.KEY_PREFIX + s);

		this.dataManager = new DataManager();
		this.jaRManager = new JaRManager();
		this.woolbattleNpc = WoolBattleNPC.create();
		this.dailyRewardNpc = DailyRewardNPC.create();

		// CommandAPI.enable(this, new CommandLobbysystemOld());
		// CommandAPI.enable(this, new CommandBuildOld());
		CommandAPI.getInstance().register(new CommandBuild());
		CommandAPI.getInstance().register(new CommandLobbysystem());

		for (World world : Bukkit.getWorlds()) {
			world.setGameRuleValue("randomTickSpeed", "0");
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setFullTime(6000);
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				for (String task : getDataManager().getWoolBattleTasks()) {
					if (CloudNetDriver.getInstance().getServiceTaskProvider()
							.isServiceTaskPresent(task)) {
						ServiceTask serviceTask =
								CloudNetDriver.getInstance().getServiceTaskProvider()
										.getServiceTask(task);
						assert serviceTask != null;
						Collection<ServiceInfoSnapshot> services =
								CloudNetDriver.getInstance().getCloudServiceProvider()
										.getCloudServices(serviceTask.getName());

						int freeServices = 0;
						for (ServiceInfoSnapshot service : services) {
							GameState state = GameState.fromString(
									service.getProperty(BridgeServiceProperty.STATE).orElse(null));
							if (state == GameState.LOBBY || state == GameState.UNKNOWN) {
								freeServices++;
							}
						}
						if (freeServices < serviceTask.getMinServiceCount()) {
							ServiceInfoSnapshot snap =
									CloudNetDriver.getInstance().getCloudServiceFactory()
											.createCloudService(serviceTask);
							assert snap != null;
							CloudNetDriver.getInstance().getCloudServiceProvider(snap)
									.setCloudServiceLifeCycle(ServiceLifeCycle.RUNNING);
						}
					} else {
						Lobby.this.sendMessage("§cCould not find service task named " + task);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 30 * 20, 30 * 20);

		new ListenerJoin();
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
		new ListenerGamemodeConnectorNPC();
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
		new ListenerBorder();

		if (PServerSupport.isSupported()) {
			SkullCache.register();
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!dataManager.isWinter()) {
					return;
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					ParticleEffect.FIREWORKS_SPARK.display(20, 20, 20, 0F, 200, p.getLocation(), p);
					ParticleEffect.SNOW_SHOVEL.display(20, 20, 20, 0F, 100, p.getLocation(), p);
				}
			}
		}.runTaskTimer(this, 1, 1);
		for (World world : Bukkit.getWorlds()) {
			world.setStorm(getDataManager().isWinter());
			world.setThundering(false);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				ConnectorNPC.load();
			}
		}.runTask(this);
	}

	public void savePlayer(LobbyUser user) {
		Player p = user.getUser().asPlayer();
		user.setLastPosition(p.getLocation());
		user.setSelectedSlot(p.getInventory().getHeldItemSlot());
	}

	public void setupPlayer(LobbyUser user) {
		Player p = user.getUser().asPlayer();
		setItems(user);

		DataManager dataManager = getDataManager();
		Location spawn = dataManager.getSpawn();
		p.setGameMode(GameMode.SURVIVAL);
		p.setAllowFlight(user.getCurrentJaR() == null);
		p.setCompassTarget(spawn.getBlock().getLocation());
		p.setExhaustion(0);
		p.setSaturation(0);
		p.setFireTicks(0);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setLevel(0);
		p.setPlayerWeather(WeatherType.CLEAR);
		p.setFoodLevel(20);
		p.setExp(1);
		PlayerInventory inv = p.getInventory();
		inv.setHeldItemSlot(user.getSelectedSlot());

		Location l = user.getLastPosition();
		if (!dataManager.getBorder().isInside(l)) {
			l = dataManager.getSpawn();
		} else if (l.add(0, 1, 0).getBlock().getType().isSolid()) {
			l = dataManager.getSpawn();
		}
		p.teleport(l);
	}

	public void setItems(LobbyUser user) {
		Player p = user.getUser().asPlayer();
		User u = user.getUser();
		PlayerInventory inv = p.getInventory();
		inv.clear();
		if (user.getCurrentJaR() == null) {
			inv.setItem(0, Item.INVENTORY_COMPASS.getItem(u));
			inv.setItem(1, Item.INVENTORY_SETTINGS.getItem(u));
			inv.setItem(4, Item.byGadget(user.getGadget()).getItem(u));
			user.getGadget().fillExtraItems(user);
			try {
				if (PServerSupport.isSupported()) {
					inv.setItem(6, Item.PSERVER_MAIN_ITEM.getItem(u));
				}
			} catch (Exception ignored) {
			}

			inv.setItem(7, Item.INVENTORY_GADGET.getItem(u));
			inv.setItem(8, Item.INVENTORY_LOBBY_SWITCHER.getItem(u));
		} else {
			inv.setItem(8, Item.JUMPANDRUN_STOP.getItem(u));
		}
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

	public HologramPool getHologramPool() {
		return hologramPool;
	}

	public JaRManager getJaRManager() {
		return this.jaRManager;
	}

	public NPCPool getNpcPool() {
		return this.npcPool;
	}

	@Override
	public String getCommandPrefix() {
		return "§aLobbySystem";
	}
}
