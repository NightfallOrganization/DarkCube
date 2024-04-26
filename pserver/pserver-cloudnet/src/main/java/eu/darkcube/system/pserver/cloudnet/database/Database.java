/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.database;

public abstract class Database {

	public final <T extends Database> T cast(Class<T> clazz) {
		return clazz.cast(this);
	}

}
