/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import java.util.UUID;

public class ModuleUser {

	private final UUID uniqueId;
	private final UserPersistentDataStorage storage = new UserPersistentDataStorage();
	private volatile String name;

	public ModuleUser(UUID uniqueId, String name) {
		this.uniqueId = uniqueId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public UserPersistentDataStorage getStorage() {
		return storage;
	}
}
