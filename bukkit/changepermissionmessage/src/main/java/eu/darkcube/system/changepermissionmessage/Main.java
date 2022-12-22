/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.changepermissionmessage;

import eu.darkcube.system.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class Main extends JavaPlugin {

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					// Plugin / Bukkit / Spigot Command registration
					Object craftServer = Bukkit.getServer();
					Method getCommandMap = craftServer.getClass().getMethod("getCommandMap");
					Object commandMap = getCommandMap.invoke(craftServer);
					Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
					knownCommandsField.setAccessible(true);
					Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
					knownCommandsField.setAccessible(false);
					String msg = ReflectionUtils.getValue(null, "org.spigotmc.SpigotConfig", true,
							"unknownCommandMessage").toString();
					for (Command cmd : knownCommands.values()) {
						cmd.setPermissionMessage(msg);
						if (cmd.getPermission() == null) {
							cmd.setPermission(cmd.getName());
						}
					}
					// Vanilla commands such like /op /gamemode etc. are handled by the modified
					// spigot.jar
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException |
						IllegalArgumentException | InvocationTargetException |
						NoSuchFieldException ex) {
					ex.printStackTrace();
				}
			}

		}.runTask(this);
	}

}
