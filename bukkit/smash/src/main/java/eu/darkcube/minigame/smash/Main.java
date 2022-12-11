/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.CustomChunkGenerator;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.minigame.smash.command.CommandSmash;
import eu.darkcube.minigame.smash.map.Map;
import eu.darkcube.minigame.smash.state.ingame.Ingame;
import eu.darkcube.minigame.smash.state.lobby.Lobby;
import eu.darkcube.minigame.smash.user.UserWrapper;
import eu.darkcube.minigame.smash.util.Locations;
import eu.darkcube.minigame.smash.util.scheduler.SchedulerTask;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.IDataManager;

public class Main extends Plugin {

	private static Main instance;

	private Collection<SchedulerTask> schedulers;
	private BukkitRunnable tickTask;
	private Lobby lobby;
	private Ingame ingame;

	public Main() {
		instance = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig("spawns");
		createConfig("spawns");
		createConfig("maps");
		
		lobby = new Lobby();
		lobby.setSpawn(Locations.deserialize(getConfig("spawns").getString("lobby"), Locations.DEFAULT_LOCATION));
	
		ingame = new Ingame();
		
		UserWrapper.init();

		schedulers = new ArrayList<>();
		tickTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (SchedulerTask s : new ArrayList<>(schedulers)) {
					if (s.canExecute()) {
						s.run();
					}
				}
			}
		};
		tickTask.runTaskTimer(this, 0, 1);
		
		for(String mapname : Map.getConfig().getKeys(false)) {
			Map.deserialize(Map.getConfig().getString(mapname));
		}
		
		CommandAPI.enable(this, new CommandSmash());
		
		lobby.enable();
		ingame.enable();
	}

	@Override
	public void onDisable() {
	}
	
	public Lobby getLobby() {
		return lobby;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new ChunkGenerator() {
			@Override
			public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
				return createChunkData(world);
			}
		};
	}

	public final void loadWorlds() {
		for (String world : getConfig("worlds").getStringList("worlds")) {
			loadWorld(world);
		}
	}

	public final void loadWorld(World world) {
		CraftWorld w = (CraftWorld) world;
		// Setting gamerules
		w.getHandle().tracker = new EntityTracker(w.getHandle());
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
			sendConsole("Preparing void world generation for world '" + world.getName() + "'");
			w.getHandle().generator = getDefaultWorldGenerator(world.getName(), world.getName());
			Field field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("dataManager");
			field.setAccessible(true);
			IDataManager manager = (IDataManager) field.get(w.getHandle());
			IChunkProvider gen = new CustomChunkGenerator(w.getHandle(), w.getHandle().getSeed(),
					w.getHandle().generator) {

			};
			gen = new ChunkProviderServer(w.getHandle(), manager.createChunkLoader(w.getHandle().worldProvider), gen) {
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
				&& !new File(this.getServer().getWorldContainer().getParent(), world).exists()) {
			return;
		}
		if (Bukkit.getWorld(world) == null && !new File(Bukkit.getWorldContainer(), world).exists()) {
			try {
				WorldCreator creator = new WorldCreator(world).generator(getDefaultWorldGenerator(world, world));
				creator.createWorld();
			} catch (NullPointerException ex) {
			}
		} else {
			if (new File(Bukkit.getWorldContainer(), world).exists()) {
				File serverfolder = new File(System.getProperty("user.dir"));
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(serverfolder, "bukkit.yml"));
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

	public Collection<SchedulerTask> getSchedulers() {
		return schedulers;
	}

	@Override
	public String getCommandPrefix() {
		return "Smash";
	}

	public static final void registerListeners(Listener... listener) {
		for (Listener l : listener) {
			Bukkit.getServer().getPluginManager().registerEvents(l, getInstance());
		}
	}

	public static final void unregisterListeners(Listener... listener) {
		for (Listener l : listener) {
			HandlerList.unregisterAll(l);
		}
	}

	public static Main getInstance() {
		return instance;
	}
}
