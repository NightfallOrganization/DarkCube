/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.pserver.common.PServerExecutor.State;

import java.util.*;

public final class PServerSnapshot {
	private final UniqueId uniqueId;
	private final PServerExecutor.State state;
	private final int onlinePlayers;
	private final long startedAt;
	private final String serverName;
	private final UUID[] owners;

	public PServerSnapshot(UniqueId uniqueId, State state, int onlinePlayers, long startedAt,
			String serverName, UUID[] owners) {
		this.uniqueId = uniqueId;
		this.state = state;
		this.onlinePlayers = onlinePlayers;
		this.startedAt = startedAt;
		this.serverName = serverName;
		this.owners = owners.clone();
	}

	public @Unmodifiable Collection<UUID> owners() {
		return Collections.unmodifiableCollection(Arrays.asList(owners));
	}

	public UniqueId uniqueId() {
		return uniqueId;
	}

	public State state() {
		return state;
	}

	public int onlinePlayers() {
		return onlinePlayers;
	}

	public long startedAt() {
		return startedAt;
	}

	public String serverName() {
		return serverName;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(uniqueId, state, onlinePlayers, startedAt, serverName);
		result = 31 * result + Arrays.hashCode(owners);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PServerSnapshot that = (PServerSnapshot) o;
		return onlinePlayers == that.onlinePlayers && startedAt == that.startedAt && Objects.equals(
				uniqueId, that.uniqueId) && state == that.state && Objects.equals(serverName,
				that.serverName) && Arrays.equals(owners, that.owners);
	}

	@Override
	public String toString() {
		return "PServerSnapshot{" + "uniqueId=" + uniqueId + ", state=" + state + ", onlinePlayers="
				+ onlinePlayers + ", startedAt=" + startedAt + ", serverName='" + serverName + '\''
				+ ", owners=" + Arrays.toString(owners) + '}';
	}
}
