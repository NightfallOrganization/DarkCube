package eu.darkcube.system.lobbysystem.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.pserver.common.PServer;

public class EventPServerMayJoin extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final LobbyUser user;
	private final PServer pserver;
	private boolean mayJoin;

	public EventPServerMayJoin(LobbyUser user, PServer pserver, boolean mayJoin) {
		this.user = user;
		this.pserver = pserver;
		this.mayJoin = mayJoin;
	}
	
	public LobbyUser getUser() {
		return user;
	}
	
	public PServer getPServer() {
		return pserver;
	}
	
	public boolean mayJoin() {
		return mayJoin;
	}
	
	public void setMayJoin(boolean mayJoin) {
		this.mayJoin = mayJoin;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
