package eu.darkcube.system.knockout.user;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.knockout.KnockOut;
import eu.darkcube.system.knockout.language.LanguageKey;
import eu.darkcube.system.knockout.language.LocaleLanguage;

public class User {

	private static Database lobbyDatabase =
			CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_userdata");

	private static Map<Player, User> users = new HashMap<>();

	private Player handle;
	private boolean buildMode = false;
	private User lastHit = null;
	private int ticksAfterLastHit = 0;
	private int killstreak = 0;
	private LocaleLanguage language;
	private BukkitRunnable autoSaver = new BukkitRunnable() {
		@Override
		public void run() {
			save();
		}
	};

	private User(Player handle) {
		this.handle = handle;
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void handle(PlayerQuitEvent e) {
				if (e.getPlayer().equals(User.this.handle)) {
					users.remove(User.this.handle).save();
				}
			}
		}, KnockOut.getInstance());
		autoSaver.runTaskTimerAsynchronously(KnockOut.getInstance(), 60 * 20, 60 * 20);

		JsonDocument lobbyUserData = lobbyDatabase.get(handle.getUniqueId().toString());
		language = LocaleLanguage.languageMap.get(lobbyUserData.getString("language", "GERMAN"));
	}

	public static User getUser(Player p) {
		if (!users.containsKey(p)) {
			User user = new User(p);
			users.put(p, user);
		}
		return users.get(p);
	}
	
	public void sendMessage(LanguageKey key, Object... replacements) {
		handle.sendMessage(language.format(key, replacements));
	}
	
	public LocaleLanguage getLanguage() {
		return language;
	}

	private void save() {

	}

	public Player getHandle() {
		return handle;
	}

	public int getKillstreak() {
		return killstreak;
	}

	public User getLastHit() {
		return lastHit;
	}

	public int getTicksAfterLastHit() {
		return ticksAfterLastHit;
	}

	public boolean isBuildMode() {
		return buildMode;
	}

	public void setLastHit(User lastHit) {
		this.lastHit = lastHit;
	}

	public void setTicksAfterLastHit(int ticksAfterLastHit) {
		this.ticksAfterLastHit = ticksAfterLastHit;
	}

	public void setBuildMode(boolean buildMode) {
		this.buildMode = buildMode;
	}

	public void setKillstreak(int killstreak) {
		this.killstreak = killstreak;
	}
}
