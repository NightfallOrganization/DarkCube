package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.darkcube.minigame.woolbattle.user.User;

public class EventPlayerDeath extends Event {

	private static final HandlerList handlers = new HandlerList();

	private User user;

	public EventPlayerDeath(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
