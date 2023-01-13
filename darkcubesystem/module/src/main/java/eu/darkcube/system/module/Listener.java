/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import de.dytanic.cloudnet.service.ICloudService;

import java.io.File;

public class Listener {

	@EventListener
	public void handle(CloudServicePreStartEvent e) {
		if (e.getCloudService().getServiceInfoSnapshot().getServiceId().getEnvironment()
				== ServiceEnvironmentType.MINECRAFT_SERVER) {
			this.copy(e.getCloudService());
		}
	}

	private void copy(ICloudService service) {
		File file = this.getFile(service);
		file.delete();
		DefaultModuleHelper.copyCurrentModuleInstanceFromClass(Listener.class, file.toPath());
	}

	private File getFile(ICloudService service) {
		File folder = new File(service.getDirectoryPath().toFile(), "plugins");
		folder.mkdirs();
		return new File(folder, DarkCubeSystemModule.PLUGIN_NAME);
	}
}
