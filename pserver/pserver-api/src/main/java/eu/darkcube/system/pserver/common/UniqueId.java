/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.common;

import java.util.UUID;

public class UniqueId {

	public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private final String id;

	public UniqueId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && id.equals(obj.toString());
	}
}
