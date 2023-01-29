/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.event;

import eu.darkcube.system.pserver.common.PServer;

public class PServerUpdateEvent extends PServerEvent {

	public PServerUpdateEvent(PServer pserver) {
		super(pserver);
	}
}
