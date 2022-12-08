/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;

public abstract class PServerProvider {

	private static PServerProvider instance;

	public PServerProvider() {
		instance = this;
	}

	public abstract void setPServerCommand(BiFunction<Object, String[], Boolean> command)
			throws IllegalStateException;

	/**
	 * @return true if this Server is a {@link PServer}
	 */
	public abstract boolean isPServer();

	/**
	 * @return the current {@link PServer}, if this is a {@link PServer}. Otherwise
	 *         {@link IllegalStateException}
	 * @throws IllegalStateException
	 */
	public abstract PServer getCurrentPServer() throws IllegalStateException;

	public abstract PServer getPServer(UniqueId pserver);

	public abstract JsonDocument getPServerData(UniqueId pserver);

	public abstract void setPServerData(UniqueId pserver, JsonDocument data);

	public abstract PServer createPServer(PServerSerializable configuration);

	public abstract PServer createPServer(PServerSerializable configuration, ServiceTask task);

	public Optional<? extends PServer> getPServerOptional(UniqueId pserver) {
		return Optional.ofNullable(getPServer(pserver));
	}

	public abstract Collection<? extends PServer> getPServers();

	public abstract Collection<UniqueId> getPServerIDs(UUID owner);

	public abstract Collection<UUID> getOwners(UniqueId pserver);

	public abstract void delete(UniqueId pserver);

	public abstract void clearOwners(UniqueId id);

	public abstract void addOwner(UniqueId id, UUID owner);

	public abstract void removeOwner(UniqueId id, UUID owner);

	public abstract String newName();

	public static PServerProvider getInstance() {
		return instance;
	}

	public static String getTemplatePrefix() {
		return "pserver";
	}

	public static ServiceTemplate getTemplate(UniqueId pserver) {
		return new ServiceTemplate(getTemplatePrefix(), pserver.toString(), "local");
	}
}
