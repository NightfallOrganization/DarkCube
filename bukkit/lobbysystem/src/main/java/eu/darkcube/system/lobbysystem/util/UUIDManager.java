package eu.darkcube.system.lobbysystem.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;

public class UUIDManager {

	public static IPlayerManager getManager() {
		return CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);
	}

	public static Player getPlayer(String name) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}

	public static Player getPlayerByUUID(UUID uuid) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().equals(uuid)) {
				return p;
			}
		}
		return null;
	}

	public static Player getPlayerByUUID(String uuid) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().toString().equals(uuid)) {
				return p;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayer(String name) {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayerByUUID(UUID uuid) {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (p.getUniqueId().equals(uuid)) {
				return p;
			}
		}
		return null;
	}

	public static OfflinePlayer getOfflinePlayerByUUID(String uuid) {
		for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if (p.getUniqueId().toString().equals(uuid)) {
				return p;
			}
		}
		return null;
	}

	public static CommandSender getSender(String name) {
		if (name.equals("console"))
			return Bukkit.getConsoleSender();
		return getPlayer(name);
	}
}
