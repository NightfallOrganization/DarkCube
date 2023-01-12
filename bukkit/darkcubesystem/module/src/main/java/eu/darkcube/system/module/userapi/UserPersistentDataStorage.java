/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserPersistentDataStorage {
	final JsonDocument data = new JsonDocument();
	final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
}
