/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.voidworldplugin;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class Main extends DarkCubePlugin implements Listener {
    public Main() {
        super("voidworld");
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler public void handle(WorldInitEvent e) {
        this.loadWorld(e.getWorld());
    }

    @Override public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new ChunkGenerator() {

            @Override public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return this.createChunkData(world);
            }

        };
    }

    public final void loadWorld(World w) {
        //		CraftWorld w = (CraftWorld) world;
        // Setting gamerules
        ReflectionUtils.invokeMethod(w, ReflectionUtils.getMethod(w.getClass(), "getHandle"));
        Object handle = ReflectionUtils.invokeMethod(w, ReflectionUtils.getMethod(w.getClass(), "getHandle"));
        Class<?> etclass = ReflectionUtils.getClass("EntityTracker", ReflectionUtils.PackageType.MINECRAFT_SERVER);
        Object tracker = ReflectionUtils.instantiateObject(ReflectionUtils.getConstructor(etclass, ReflectionUtils.getClass("WorldServer", ReflectionUtils.PackageType.MINECRAFT_SERVER)), handle);

        ReflectionUtils.setValue(handle, handle.getClass(), false, "tracker", tracker);
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
        Object manager = ReflectionUtils.getValue(handle, "World", ReflectionUtils.PackageType.MINECRAFT_SERVER, true, "dataManager");
        Object gen = ReflectionUtils.instantiateObject(ReflectionUtils.getConstructor("CustomChunkGenerator", ReflectionUtils.PackageType.CRAFTBUKKIT_GENERATOR, ReflectionUtils.getClass("World", ReflectionUtils.PackageType.MINECRAFT_SERVER), long.class, ChunkGenerator.class), handle, ReflectionUtils.invokeMethod(handle, ReflectionUtils.getMethod(handle.getClass(), "getSeed")), ReflectionUtils.getValue(handle, false, "generator"));
        gen = ReflectionUtils.instantiateObject(ReflectionUtils.getConstructor("ChunkProviderServer", ReflectionUtils.PackageType.MINECRAFT_SERVER, ReflectionUtils.getClass("WorldServer", ReflectionUtils.PackageType.MINECRAFT_SERVER), ReflectionUtils.getClass("IChunkLoader", ReflectionUtils.PackageType.MINECRAFT_SERVER), ReflectionUtils.getClass("IChunkProvider", ReflectionUtils.PackageType.MINECRAFT_SERVER)), handle, ReflectionUtils.invokeMethod(manager, ReflectionUtils.getMethod(manager.getClass(), "createChunkLoader", ReflectionUtils.getClass("IDataManager", ReflectionUtils.PackageType.MINECRAFT_SERVER)), ReflectionUtils.getValue(handle, false, "worldProvider")), gen);
        ReflectionUtils.setValue(handle, false, "chunkProviderServer", gen);
        ReflectionUtils.setValue(handle, "World", ReflectionUtils.PackageType.MINECRAFT_SERVER, true, "chunkProvider", gen);
        ReflectionUtils.setValue(w, ReflectionUtils.getClass("CraftWorld", ReflectionUtils.PackageType.CRAFTBUKKIT), true, "generator", ReflectionUtils.getValue(handle, false, "generator"));
    }

}
