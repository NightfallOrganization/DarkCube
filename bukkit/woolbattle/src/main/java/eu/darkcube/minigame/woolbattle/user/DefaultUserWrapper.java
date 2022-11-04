package eu.darkcube.minigame.woolbattle.user;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;

public class DefaultUserWrapper implements UserWrapper, Listener {

	private Collection<User> users;

	public DefaultUserWrapper() {
		users = new HashSet<>();
		Main.registerListeners(this);
		Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).forEach(this::load);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PlayerJoinEvent e) {
		load(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(PlayerQuitEvent e) {
		MySQL.saveUserData(getUser(e.getPlayer().getUniqueId()));
		unload(getUser(e.getPlayer().getUniqueId()));
	}

	@Override
	public User load(UUID uuid) {
		if (!isLoaded(uuid)) {
			UserData data = MySQL.loadUserData(uuid);
			User user = new DefaultUser(uuid, data, (CraftPlayer) Bukkit.getPlayer(uuid));
			if (user.getData().isParticles()) {
				Main.getInstance().getIngame().particlePlayers.add(user.getBukkitEntity());
			}
			users.add(user);
			return user;
		}
		return null;
	}

	@Override
	public User getUser(UUID uuid) {
		for (User user : users) {
			if (user.getUniqueId().equals(uuid)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public boolean isLoaded(UUID uuid) {
		for (User user : users) {
			if (user.getUniqueId().equals(uuid)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean unload(User user) {
		Main.getInstance().getIngame().particlePlayers.remove(user.getBukkitEntity());
		return users.remove(user);
	}

	@Override
	public Collection<? extends User> getUsers() {
		return Collections.unmodifiableCollection(users);
	}
}