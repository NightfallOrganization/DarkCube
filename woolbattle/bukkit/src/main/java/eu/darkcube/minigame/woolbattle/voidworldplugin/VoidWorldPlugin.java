/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.voidworldplugin;

import com.avaje.ebean.EbeanServer;
import eu.darkcube.minigame.woolbattle.listener.ListenerVoidWorld;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.IChunkProvider;
import net.minecraft.server.v1_8_R3.IDataManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.generator.CustomChunkGenerator;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class VoidWorldPlugin implements Plugin {
    private static VoidWorldPlugin instance;
    private ListenerVoidWorld listenerVoidWorld;
    private boolean naggable = true;
    private boolean enabled = false;
    private PluginDescriptionFile description = VoidWorldPluginDescription.get();
    private Logger logger = Logger.getLogger("WoolBattleVoidWorld");
    private Server server;
    private VoidWorldPluginLoader loader;

    public VoidWorldPlugin(Server server, VoidWorldPluginLoader loader) {
        instance = this;
        this.server = server;
        this.loader = loader;
    }

    public static VoidWorldPlugin instance() {
        return instance;
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

    public final void loadWorld(World world) {
        CraftWorld w = (CraftWorld) world;
        // Setting gamerules
        w.getHandle().tracker = new EntityTracker(w.getHandle());
        w.setDifficulty(Difficulty.NORMAL);
        w.setKeepSpawnInMemory(false);
        w.setFullTime(0);
        w.setAutoSave(false);
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
            Bukkit.getConsoleSender().sendMessage(
                    "Preparing void world generation for world '" + world.getName() + "'");
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
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException |
                 SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label,
                                      String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return description;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String var1) {
        return null;
    }

    @Override
    public void saveConfig() {
    }

    @Override
    public void saveDefaultConfig() {
    }

    @Override
    public void saveResource(String string, boolean bool) {

    }

    @Override
    public void reloadConfig() {
    }

    @Override
    public PluginLoader getPluginLoader() {
        return loader;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listenerVoidWorld);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(listenerVoidWorld = new ListenerVoidWorld(), this);
    }

    @Override
    public boolean isNaggable() {
        return naggable;
    }

    @Override
    public void setNaggable(boolean naggable) {
        this.naggable = naggable;
    }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String var1, String var2) {
        return new ChunkGenerator() {

            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z,
                                               BiomeGrid biome) {
                return this.createChunkData(world);
            }

        };
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getName() {
        return "WoolBattleVoidWorld";
    }

}
