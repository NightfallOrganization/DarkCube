package eu.darkcube.system.pserver.common;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.system.pserver.common.packet.PServerSerializable;

public interface PServer {

	String CHANNEL = "pserver";

	State getState();

	boolean start();

	boolean stop();
	
	void remove();

	int getOnlinePlayers();
	
	boolean isGamemode();

	boolean isPrivate();

	boolean isPublic();
	
	void setPrivate(boolean privateServer);
	
	UniqueId getId();
	
	long getOntime();
	
	Collection<UUID> getOwners();
	
	String getServerName();
	
	long getStartedAt();
	
	PServerSerializable getSerializable();

	String getTaskName();
	
	void connectPlayer(UUID player);

	default boolean isOnline() {
		return getState() != State.OFFLINE;
	}

	default boolean isRunning() {
		return getState() == State.RUNNING;
	}

	default boolean isStopping() {
		return getState() == State.STOPPING;
	}

	default boolean isOffline() {
		return getState() == State.OFFLINE;
	}

	default boolean isStarting() {
		return getState() == State.STARTING;
	}

	public static enum State {
		RUNNING, STARTING, STOPPING, OFFLINE
	}
}
