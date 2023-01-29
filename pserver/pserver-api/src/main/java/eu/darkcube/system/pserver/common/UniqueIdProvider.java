/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common;

public abstract class UniqueIdProvider {

	private static UniqueIdProvider instance;
	
	public UniqueIdProvider() {
		instance = this;
	}
	
	public abstract boolean isAvailable(UniqueId id);
	
	public abstract UniqueId newUniqueId();
	
	public static UniqueIdProvider getInstance() {
		return instance;
	}
}
