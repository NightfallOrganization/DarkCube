/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common;

import java.util.Collection;
import java.util.UUID;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
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

	JsonDocument getData();

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
