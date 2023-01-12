/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.module.userapi.UserAPI;
import eu.darkcube.system.packetapi.PacketAPI;

import java.io.File;

public class DarkCubeSystemModule extends DriverModule {
	public static final String PLUGIN_NAME = new File(
			DarkCubeSystemModule.class.getProtectionDomain().getCodeSource().getLocation()
					.getPath()).getName();
	private UserAPI userAPI = new UserAPI();

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void load() {
		PacketAPI.init();
		CloudNetDriver.getInstance().getEventManager().registerListener(userAPI);
		CloudNetDriver.getInstance().getEventManager().registerListener(new Listener());
	}
}
