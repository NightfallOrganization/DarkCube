/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.module;

import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.replay.api.ReplayApiHolder;
import eu.darkcube.system.replay.module.network.PacketReceiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReplayModule extends DriverModule {

	private Path dataDirectory;
	private PacketReceiver packetReceiver;

	@ModuleTask(order = 0, event = ModuleLifeCycle.LOADED)
	public void init() {
		dataDirectory = this.getModuleWrapper().getDataDirectory();
		try {
			Files.createDirectories(dataDirectory);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		packetReceiver = new PacketReceiver(this);
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void started() {
		ReplayApiHolder.set(new ModuleReplayApi(this));
	}

	@ModuleTask(order = 0, event = ModuleLifeCycle.STOPPED)
	public void stopped() {
		packetReceiver.shutdown();
	}

	public Path dataDirectory() {
		return dataDirectory;
	}
}
