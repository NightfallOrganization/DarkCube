/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.event;

import eu.darkcube.system.pserver.common.PServerExecutor;

import java.util.UUID;

public class PServerRemoveOwnerEvent extends PServerEvent {

	private UUID owner;

	public PServerRemoveOwnerEvent(PServerExecutor pserver, UUID owner) {
		super(pserver);
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

}
