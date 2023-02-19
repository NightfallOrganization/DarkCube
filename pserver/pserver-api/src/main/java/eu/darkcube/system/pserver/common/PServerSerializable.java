/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;

public final class PServerSerializable {

	public final UniqueId id;
	public final int onlinePlayers;
	public final Type type;
	public final long startedAt;
	public final String serverName;
	public final String taskName;
	public final State state;
	public final AccessLevel accessLevel;

	public PServerSerializable(UniqueId id, int onlinePlayers, Type type, long startedAt,
			String serverName, String taskName, State state, AccessLevel accessLevel) {
		this.id = id;
		this.onlinePlayers = onlinePlayers;
		this.type = type;
		this.startedAt = startedAt;
		this.serverName = serverName;
		this.taskName = taskName;
		this.state = state;
		this.accessLevel = accessLevel;
	}
}


