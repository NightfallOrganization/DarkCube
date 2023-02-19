/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;

import java.util.Objects;

public class PServerSnapshot {
	private final UniqueId uniqueId;
	private final PServerExecutor.State state;
	private final PServerExecutor.Type type;
	private final PServerExecutor.AccessLevel accessLevel;
	private final String taskName;
	private final int onlinePlayers;
	private final long startedAt;
	private final String serverName;

	public PServerSnapshot(UniqueId uniqueId, State state, Type type, AccessLevel accessLevel,
			String taskName, int onlinePlayers, long startedAt, String serverName) {
		this.uniqueId = uniqueId;
		this.state = state;
		this.type = type;
		this.accessLevel = accessLevel;
		this.taskName = taskName;
		this.onlinePlayers = onlinePlayers;
		this.startedAt = startedAt;
		this.serverName = serverName;
	}

	public UniqueId uniqueId() {
		return uniqueId;
	}

	public State state() {
		return state;
	}

	public Type type() {
		return type;
	}

	public AccessLevel accessLevel() {
		return accessLevel;
	}

	public String taskName() {
		return taskName;
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
		return Objects.hash(uniqueId, state, type, accessLevel, taskName, onlinePlayers, startedAt,
				serverName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PServerSnapshot that = (PServerSnapshot) o;
		return onlinePlayers == that.onlinePlayers && startedAt == that.startedAt && Objects.equals(
				uniqueId, that.uniqueId) && state == that.state && type == that.type
				&& accessLevel == that.accessLevel && Objects.equals(taskName, that.taskName)
				&& Objects.equals(serverName, that.serverName);
	}

	@Override
	public String toString() {
		return "PServerSnapshot{" + "uniqueId=" + uniqueId + ", state=" + state + ", type=" + type
				+ ", accessLevel=" + accessLevel + ", taskName='" + taskName + '\''
				+ ", onlinePlayers=" + onlinePlayers + ", startedAt=" + startedAt + ", serverName='"
				+ serverName + '\'' + '}';
	}
}
