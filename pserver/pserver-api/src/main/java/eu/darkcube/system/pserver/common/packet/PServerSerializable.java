/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common.packet;

import java.util.Collection;
import java.util.UUID;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.UniqueId;

public class PServerSerializable {

	public UniqueId id;
	public int online;
	public PServer.State state;
	public boolean temporary;
	public long startedAt;
	public Collection<UUID> owners;
	public String serverName;
	public String taskName;

	public PServerSerializable(UniqueId id, int online, State state, boolean temporary,
			long startedAt, Collection<UUID> owners, String taskName, String serverName) {
		this.id = id;
		this.online = online;
		this.state = state;
		this.temporary = temporary;
		this.startedAt = startedAt;
		this.owners = owners;
		this.taskName = taskName;
		this.serverName = serverName;
	}
}
