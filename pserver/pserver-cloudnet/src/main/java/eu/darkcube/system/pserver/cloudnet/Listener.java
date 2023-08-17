/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.service.CloudServiceLifecycleChangeEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener {

    @EventListener public void handle(CloudServicePreProcessStartEvent e) {
        if (e.serviceConfiguration().serviceId().environment().equals(ServiceEnvironmentType.MINECRAFT_SERVER)) {
            this.copy(e.service());
        }
    }

    @EventListener public void handle(CloudServiceLifecycleChangeEvent e) {
        if (e.lastLifeCycle() == ServiceLifeCycle.RUNNING) {
            UniqueId id = ServiceInfoUtil.getInstance().getUniqueId(e.serviceInfo());
            if (id != null) {
                NodePServerProvider.instance().unload(id);
                e.serviceInfo().provider().deployResources(false);
            }
        }
    }

    private void copy(CloudService service) {
        try {
            Path file = this.getFile(service);
            Files.deleteIfExists(file);
            ModuleHelper moduleHelper = InjectionLayer.boot().instance(ModuleHelper.class);
            moduleHelper.copyJarContainingClass(Listener.class, file);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "error during copy for pserver", e);
        }
    }

    private Path getFile(CloudService service) throws IOException {
        Path plugins = service.pluginDirectory();
        Files.createDirectories(plugins);
        return plugins.resolve(PServerModule.PLUGIN_NAME);
    }
}
