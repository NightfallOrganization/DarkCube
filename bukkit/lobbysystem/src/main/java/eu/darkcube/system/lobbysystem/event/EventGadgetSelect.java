package eu.darkcube.system.lobbysystem.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.user.User;

public class EventGadgetSelect extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Gadget gadget;
	private boolean cancel;
	private User user;

	public EventGadgetSelect(User user, Gadget gadget) {
		this.user = user;
		cancel = false;
		this.gadget = gadget;
	}

	public Gadget getGadget() {
		return gadget;
	}

	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
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

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
