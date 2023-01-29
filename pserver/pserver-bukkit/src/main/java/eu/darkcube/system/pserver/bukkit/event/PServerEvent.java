/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit.event;

import de.dytanic.cloudnet.driver.event.Event;
import eu.darkcube.system.pserver.common.PServer;

public abstract class PServerEvent extends Event {

	private final PServer pserver;

	public PServerEvent(PServer pserver) {
		super();
		this.pserver = pserver;
	}

	public PServer getPServer() {
		return pserver;
	}
}
