/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.event;

import java.util.UUID;

import eu.darkcube.system.pserver.common.PServer;

public class PServerAddOwnerEvent extends PServerEvent {

	private UUID owner;

	public PServerAddOwnerEvent(PServer pserver, UUID owner) {
		super(pserver);
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

}
