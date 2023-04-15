/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import eu.darkcube.system.replay.api.ReplayApi;
import eu.darkcube.system.replay.api.ReplayData;

public class ModuleReplayApi implements ReplayApi {

	private final ReplayModule module;

	public ModuleReplayApi(ReplayModule module) {
		this.module = module;
	}

	@Override
	public ModuleReplayIdStream list() {
		return new ModuleReplayIdStream(module);
	}

	@Override
	public ReplayData downloadReplay(int id) {
		return null;
	}
}
