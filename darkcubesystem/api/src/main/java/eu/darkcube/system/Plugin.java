/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.plugin.PacketPluginDataRemove;
import eu.darkcube.system.util.data.plugin.PacketPluginDataSet;
import eu.darkcube.system.util.data.plugin.PluginPersistentDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Plugin extends JavaPlugin {

	private static final Database pluginPersistentData =
			CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("plugin_persistent_data");
	private static final Collection<PluginPersistentDataStorage> storages =
			ConcurrentHashMap.newKeySet();
	private static HashMap<YamlConfiguration, File> fileFromConfig = new HashMap<>();
	private static HashMap<String, YamlConfiguration> configFromName = new HashMap<>();
	private final PluginPersistentDataStorage storage = new PluginPersistentDataStorage(this);
	private final AtomicBoolean storageLoaded = new AtomicBoolean();

	public Plugin() {
	}

	protected static void saveStorages() {
		storages.forEach(storage -> {
			if (pluginPersistentData.contains(storage.plugin.getName())) {
				pluginPersistentData.update(storage.plugin.getName(), storage.data);
			} else {
				pluginPersistentData.insert(storage.plugin.getName(), storage.data);
			}
		});
	}

	public PersistentDataStorage persistentDataStorage() {
		if (!storageLoaded.get()) {
			synchronized (storage) {
				if (!storageLoaded.get()) {
					PacketAPI.getInstance().registerHandler(PacketPluginDataSet.class, packet -> {
						storage.lock.lock();
						storage.data.append(packet.getData());
						storage.lock.unlock();
						return null;
					});
					PacketAPI.getInstance()
							.registerHandler(PacketPluginDataRemove.class, packet -> {
								storage.lock.lock();
								storage.data.remove(packet.getKey().toString());
								storage.lock.unlock();
								return null;
							});
					if (pluginPersistentData.contains(this.getName())) {
						JsonDocument doc = pluginPersistentData.get(this.getName());
						storage.data.append(doc);
					}
					storages.add(storage);
					storageLoaded.set(true);
				}
			}
		}
		return storage;
	}

	public abstract String getCommandPrefix();

	public Plugin saveDefaultConfig(String path) {
		path += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + path);
		if (!f.exists()) {
			this.saveResource(path, true);
		}
		return this;
	}

	public void createConfig(String name) {
		name += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		Plugin.fileFromConfig.put(cfg, f);
		Plugin.configFromName.put(name, cfg);

		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public YamlConfiguration getConfig(String name) {
		name += ".yml";
		return Plugin.configFromName.get(name);
	}

	public Plugin saveConfig(YamlConfiguration cfg) {
		try {
			cfg.save(Plugin.fileFromConfig.get(cfg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public YamlConfiguration reloadConfig(String name) {
		name += ".yml";
		File f = new File(this.getDataFolder().getPath() + "/" + name);
		Plugin.fileFromConfig.remove(Plugin.configFromName.get(name));
		Plugin.configFromName.remove(name);
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		Plugin.configFromName.put(name, cfg);
		Plugin.fileFromConfig.put(cfg, f);
		return cfg;
	}

	public final void sendConsole(String msg, String iprefix) {
		this.sendMessageBridge(
				("§7> " + this.getCommandPrefix() + ("§6 " + iprefix + " §7| ").replace("  ", " ")
						+ msg).replace("§r", "§r§7").replace("§f", "§7"),
				Bukkit.getConsoleSender());
	}

	public final void sendConsole(String msg) {
		this.sendConsole(msg, "");
	}

	public final void sendMessage(String msg) {
		this.sendMessageBridgePrefix(msg);
	}

	public final void sendMessageWithoutPrefix(String msg,
			Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> this.sendMessageBridge(msg, r));
	}

	public final void sendConsoleWithoutPrefix(String msg) {
		this.sendMessageBridge(msg, Bukkit.getConsoleSender());
	}

	public final void sendMessageWithoutPrefix(String msg, CommandSender sender) {
		sender.sendMessage(msg);
	}

	public final void sendMessage(String msg, Collection<? extends CommandSender> receivers) {
		receivers.forEach(r -> this.sendMessage(msg, r));
	}

	public final void sendMessage(String msg, CommandSender sender) {
		this.sendMessageBridgePrefix(msg, sender);
	}

	private final void sendMessageBridgePrefix(String msg, CommandSender sender) {
		if (sender instanceof Player) {
			msg = "§7» §8[§6" + this.getCommandPrefix() + "§8] §7┃ " + msg;
		} else if (sender instanceof ConsoleCommandSender) {
			this.sendConsole(msg);
			return;
		}
		this.sendMessageBridge(msg, sender);
	}

	private void sendMessageBridgePrefix(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.sendMessageBridgePrefix(msg, p);
		}
		this.sendMessageBridgePrefix(msg, Bukkit.getConsoleSender());
	}

	private void sendMessageBridge(String msg, CommandSender sender) {
		this.sendMessageWithoutPrefix(msg, sender);
	}

}
