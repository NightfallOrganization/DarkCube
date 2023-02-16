/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.pserver.common.PServerExecutor.State;

public final class PServerSerializable {

	public UniqueId id;
	public int online;
	public boolean temporary;
	public long startedAt;
	public String serverName;
	public String taskName;
	public State state;

	public PServerSerializable(UniqueId id, int online, boolean temporary, long startedAt,
			String taskName, String serverName, State state) {
		this.id = id;
		this.online = online;
		this.temporary = temporary;
		this.startedAt = startedAt;
		this.taskName = taskName;
		this.serverName = serverName;
		this.state = state;
	}

	@Override
	public PServerSerializable clone() {
		return new PServerSerializable(id, online, temporary, startedAt, taskName, serverName,
				state);
	}
}