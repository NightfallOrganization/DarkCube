/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.voidworldplugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import com.avaje.ebean.EbeanServer;

import eu.darkcube.minigame.woolbattle.listener.ListenerVoidWorld;

public class VoidWorldPlugin implements Plugin {

	private ListenerVoidWorld listenerVoidWorld;
	private boolean naggable = true;
	private boolean enabled = false;
	private PluginDescriptionFile description = VoidWorldPluginDescription.get();
	private Logger logger = Logger.getLogger("WoolBattleVoidWorld");
	private Server server;
	private VoidWorldPluginLoader loader;

	public VoidWorldPlugin(Server server, VoidWorldPluginLoader loader) {
		this.server = server;
		this.loader = loader;
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(listenerVoidWorld);
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(listenerVoidWorld = new ListenerVoidWorld(), this);
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
	public FileConfiguration getConfig() {
		return null;
	}

	@Override
	public File getDataFolder() {
		return null;
	}

	@Override
	public EbeanServer getDatabase() {
		return null;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String var1, String var2) {
		return null;
	}

	@Override
	public PluginDescriptionFile getDescription() {
		return description;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getName() {
		return "WoolBattleVoidWorld";
	}

	@Override
	public PluginLoader getPluginLoader() {
		return loader;
	}

	@Override
	public InputStream getResource(String var1) {
		return null;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isNaggable() {
		return naggable;
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void reloadConfig() {
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
	public void setNaggable(boolean naggable) {
		this.naggable = naggable;
	}

}
