package eu.darkcube.system.commandapi.v3;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitCommandExecutorConfigureEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private BukkitCommandExecutor executor;

	public BukkitCommandExecutorConfigureEvent(BukkitCommandExecutor executor) {
		this.executor = executor;
	}

	public BukkitCommandExecutor getExecutor() {
		return executor;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
