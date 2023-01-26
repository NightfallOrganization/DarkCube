/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.api;

public interface ReplayApi {

	static ReplayApi api() {
		return ReplayApiHolder.api();
	}

	ReplayIdStream list();

	ReplayData downloadReplay(int id);
}
