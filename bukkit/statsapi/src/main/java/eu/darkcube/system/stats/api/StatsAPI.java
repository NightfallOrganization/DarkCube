package eu.darkcube.system.stats.api;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import eu.darkcube.system.stats.api.user.User;

public class StatsAPI {

	public static User getUser(UUID uuid) {
		if(uuid == null)
			return null;
		OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
		if(p.getName() != null) {
			return new User(p.getName(), uuid);
		}
		String name = UUIDFetcher.getPlayerName(uuid);
		if(name == null) {
			return null;
		}
		return new User(name, uuid);
	}
}
