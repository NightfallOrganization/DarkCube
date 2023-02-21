/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.util;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.pserver.bukkit.event.PServerAddOwnerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

public class OwnerCache {

	private final Collection<UUID> owners = new CopyOnWriteArraySet<>();
	private volatile UniqueId uid;

	public void register() {
		CloudNetDriver.getInstance().getEventManager().registerListener(this);
		try {
			this.uid = PServerProvider.instance().currentPServer().id();
			this.owners.addAll(PServerProvider.instance().currentPServer().owners().get());
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public void unregister() {
		CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
	}

	@EventListener
	public void handle(PServerAddOwnerEvent event) {
		if (!event.pserver().id().equals(uid)) {
			return;
		}
		owners.remove(event.getOwner());
	}

	@EventListener
	public void handle(PServerRemoveOwnerEvent event) {
		if (!event.pserver().id().equals(uid)) {
			return;
		}
		owners.remove(event.getOwner());
	}

	public Collection<UUID> owners() {
		return Collections.unmodifiableCollection(owners);
	}
}
