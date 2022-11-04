package eu.darkcube.system.stats.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;

public class StatsUserManager {

	public static List<User> getOnlineUsers() {
		List<User> ls = new ArrayList<>();
		ls.addAll(CloudNetDriver.getInstance()
				.getServicesRegistry()
				.getFirstService(IPlayerManager.class)
				.onlinePlayers()
				.asPlayers()
				.stream()
				.map(s -> new User(s.getName(), s.getUniqueId()))
				.collect(Collectors.toList()));
		return ls;
	}

	public static User getOfflineUser(UUID uuid) {
		ICloudOfflinePlayer op = CloudNetDriver.getInstance()
				.getServicesRegistry()
				.getFirstService(IPlayerManager.class)
				.getOfflinePlayer(uuid);
		return new User(op.getName(), op.getUniqueId());
	}

	public static List<User> getOfflineUser(String name) {
		List<User> ls = new ArrayList<>();
		ls.addAll(CloudNetDriver.getInstance()
				.getServicesRegistry()
				.getFirstService(IPlayerManager.class)
				.getOfflinePlayers(name)
				.stream()
				.map(s -> new User(s.getName(), s.getUniqueId()))
				.collect(Collectors.toList()));
		return ls;
	}

}
