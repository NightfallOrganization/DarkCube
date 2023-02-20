/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import de.dytanic.cloudnet.event.service.CloudServicePreStopEvent;
import de.dytanic.cloudnet.service.ICloudService;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Listener {

	@EventListener
	public void handle(CloudServicePreStartEvent e) {
		if (e.getCloudService().getServiceInfoSnapshot().getServiceId().getEnvironment()
				== ServiceEnvironmentType.MINECRAFT_SERVER) {
			try {
				this.copy(e.getCloudService());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventListener
	public void handle(CloudServicePreStopEvent e) {
		UniqueId id = ServiceInfoUtil.getInstance()
				.getUniqueId(e.getCloudService().getServiceInfoSnapshot());
		if (id != null) {
			NodePServerProvider.instance().unload(id);
			e.getCloudService().deployResources(false);
		}
	}

	//	@EventListener
	//	public void handle(CloudServiceInfoUpdateEvent e) {
	//		ServiceInfoSnapshot info = e.getServiceInfo();
	//		for (NodePServerExecutor ps : NodePServerProvider.getInstance().getPServers()) {
	//			if (ps.getServiceId() != null) {
	//				if (ps.getServiceId().getUniqueId().equals(info.getServiceId().getUniqueId())) {
	//					ps.setSnapshot(info);
	//					break;
	//				}
	//			}
	//		}
	//	}

	private void copy(ICloudService service) throws IOException {
		Path file = this.getFile(service);
		Files.delete(file);
		DefaultModuleHelper.copyCurrentModuleInstanceFromClass(Listener.class, file);
	}

	private Path getFile(ICloudService service) throws IOException {
		//		Path folder = new File(service.getDirectoryPath().toFile(), "plugins");
		Path folder = service.getDirectoryPath().resolve("plugins");
		Files.createDirectories(folder);
		return folder.resolve(PServerModule.PLUGIN_NAME);
	}

}
