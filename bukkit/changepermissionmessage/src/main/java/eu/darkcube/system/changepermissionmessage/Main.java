package eu.darkcube.system.changepermissionmessage;

import java.lang.reflect.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.java.*;

import eu.darkcube.system.*;

public class Main extends JavaPlugin {

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		try {
			// Plugin / Bukkit / Spigot Command registration
			Object craftServer = Bukkit.getServer();
			Method getCommandMap = craftServer.getClass().getMethod("getCommandMap");
			Object commandMap = getCommandMap.invoke(craftServer);
			Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
			knownCommandsField.setAccessible(true);
			Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
			knownCommandsField.setAccessible(false);
			String msg = Reflection.getFieldValue(
					Reflection.getField(Reflection.getClass("org.spigotmc.SpigotConfig"), "unknownCommandMessage"),
					null).toString();
			for (Command cmd : knownCommands.values()) {
				cmd.setPermissionMessage(msg);
			}
			// Vanilla commands such like /op /gamemode etc. are handled by the modified
			// spigot.jar
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		}
	}
}
