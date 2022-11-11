package eu.darkcube.system.voidworldplugin;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.ReflectionUtils;
import eu.darkcube.system.ReflectionUtils.PackageType;

public class Main extends DarkCubePlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void handle(WorldInitEvent e) {
		this.loadWorld(e.getWorld());
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new ChunkGenerator() {

			@Override
			public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
				return this.createChunkData(world);
			}

		};
	}

	public final void loadWorld(World w) {
//		CraftWorld w = (CraftWorld) world;
		// Setting gamerules
		ReflectionUtils.invokeMethod(w, "getHandle");
		Object handle = ReflectionUtils.invokeMethod(w, ReflectionUtils.getMethod(w.getClass(), "getHandle"));
//		Class<?> EntityTracker = Reflection.getVersionClass(Reflection.MINECRAFT_PREFIX, "EntityTracker");
//		Object tracker = Reflection.newInstance(Reflection.getConstructor(EntityTracker, handle.getClass()), handle);
		Object tracker = ReflectionUtils
				.instantiateObject(ReflectionUtils.getClass("EntityTracker", PackageType.MINECRAFT_SERVER), handle);
		ReflectionUtils.setValue(handle, handle.getClass(), false, "tracker", tracker);
//		w.getHandle().tracker = new EntityTracker(w.getHandle());
		w.setDifficulty(Difficulty.PEACEFUL);
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
		// Setting all chunkgenerator fields for world
		Bukkit.getConsoleSender().sendMessage("§cPreparing void world generation for world '" + w.getName() + "'");
		ReflectionUtils.setValue(handle, false, "generator", this.getDefaultWorldGenerator(w.getName(), w.getName()));
//			w.getHandle().generator = this.getDefaultWorldGenerator(world.getName(), world.getName());
//			Field field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("dataManager");
//			field.setAccessible(true);
//			IDataManager manager = (IDataManager) field.get(w.getHandle());
		Object manager = ReflectionUtils.getValue(handle, "World", PackageType.MINECRAFT_SERVER, true, "dataManager");
		Object gen = ReflectionUtils.instantiateObject(
				ReflectionUtils.getConstructor("CustomChunkGenerator", PackageType.CRAFTBUKKIT_GENERATOR,
						ReflectionUtils.getClass("World", PackageType.MINECRAFT_SERVER), long.class,
						ChunkGenerator.class),
				handle, ReflectionUtils.invokeMethod(handle, "getSeed"),
				ReflectionUtils.getValue(handle, false, "generator"));
//					ReflectionUtils.getClass("CustomChunkGenerator", PackageType.CRAFTBUKKIT_GENERATOR), null);

//			IChunkProvider gen = new CustomChunkGenerator(w.getHandle(), w.getHandle().getSeed(),
//					w.getHandle().generator) {
//
//			};
		gen = ReflectionUtils.instantiateObject(
				ReflectionUtils.getConstructor("ChunkProviderServer", PackageType.MINECRAFT_SERVER,
						ReflectionUtils.getClass("WorldServer", PackageType.MINECRAFT_SERVER),
						ReflectionUtils.getClass("IChunkLoader", PackageType.MINECRAFT_SERVER),
						ReflectionUtils.getClass("IChunkProvider", PackageType.MINECRAFT_SERVER)),
				handle,
				ReflectionUtils.invokeMethod(manager,
						ReflectionUtils.getMethod(manager.getClass(), "createChunkLoader",
								ReflectionUtils.getClass("IDataManager", PackageType.MINECRAFT_SERVER)),
						ReflectionUtils.getValue(handle, false, "worldProvider")),
				gen);
//			gen = new ChunkProviderServer(w.getHandle(), manager.createChunkLoader(w.getHandle().worldProvider), gen) {
//			};
//			w.getHandle().chunkProviderServer = (ChunkProviderServer) gen;
		ReflectionUtils.setValue(handle, false, "chunkProviderServer", gen);
		ReflectionUtils.setValue(handle, "World", PackageType.MINECRAFT_SERVER, true, "chunkProvider", gen);
		ReflectionUtils.setValue(w, ReflectionUtils.getClass("CraftWorld", PackageType.CRAFTBUKKIT), true, "generator",
				ReflectionUtils.getValue(handle, false, "generator"));
//			field = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("chunkProvider");
//			field.setAccessible(true);
//			field.set(w.getHandle(), gen);
//			field = w.getClass().getDeclaredField("generator");
//			field.setAccessible(true);
//			field.set(w, w.getHandle().generator);
	}

}
