package eu.darkcube.system.lobbysystem.pserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.ChatUtils.ChatEntry;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.AsyncExecutor;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.wrapper.event.PServerRemoveEvent;
import eu.darkcube.system.pserver.wrapper.event.PServerUpdateEvent;

public class PServerJoinOnStart implements Listener {

	public Map<UUID, UniqueId> waiting = new HashMap<>();

	public void register(User user, PServer pserver) {
		if (pserver.getState() == State.RUNNING) {
			AsyncExecutor.service().submit(() -> {
				pserver.connectPlayer(user.getUniqueId());
			});
			return;
		}
		this.waiting.put(user.getUniqueId(), pserver.getId());
	}

	private BukkitRunnable runnable = new BukkitRunnable() {

		@Override
		public void run() {
			for (UUID uuid : PServerJoinOnStart.this.waiting.keySet()) {
				Player p = Bukkit.getPlayer(uuid);
				if (p == null) {
					continue;
				}
				ChatEntry.buildActionbar(new ChatEntry.Builder().text(Message.CONNECTING_TO_PSERVER_AS_SOON_AS_ONLINE
						.getMessage(UserWrapper.getUser(p.getUniqueId()))).build()).sendPlayer(p);
//				PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(), (byte) 2);
//				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
		}

	};

	public PServerJoinOnStart() {
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
		Bukkit.getPluginManager().registerEvents(this, Lobby.getInstance());
		this.runnable.runTaskTimer(Lobby.getInstance(), 10, 10);
	}

	@EventListener
	public void handle(PServerUpdateEvent event) {
		if (event.getPServer().getState() == State.RUNNING) {
			for (Map.Entry<UUID, UniqueId> e : this.waiting.entrySet()) {
				if (e.getValue().equals(event.getPServer().getId())) {
					event.getPServer().connectPlayer(e.getKey());
				}
			}
		}
	}

	@EventListener
	public void handle(PServerRemoveEvent event) {
		Set<UUID> toRemove = new HashSet<>();
		for (Map.Entry<UUID, UniqueId> e : this.waiting.entrySet()) {
			if (e.getValue().equals(event.getPServer().getId())) {
				toRemove.add(e.getKey());
			}
		}
		toRemove.forEach(this.waiting::remove);
	}

	@EventHandler
	public void handle(PlayerQuitEvent event) {
		this.waiting.remove(event.getPlayer().getUniqueId());
	}

	public void unregister() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
		HandlerList.unregisterAll(this);
		this.waiting.clear();
		this.runnable.cancel();
	}

}