/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.node.event.service.CloudServicePreLifecycleEvent;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger("PServer");

    @EventListener
    public void handle(CloudServicePreProcessStartEvent e) {
        if (e.serviceConfiguration().serviceId().environment().equals(ServiceEnvironmentType.MINECRAFT_SERVER)) {
            this.copy(e.service());
        }
    }

    @EventListener
    public void handle(CloudServicePreLifecycleEvent e) {
        if (e.targetLifecycle() == ServiceLifeCycle.DELETED || e.targetLifecycle() == ServiceLifeCycle.STOPPED) {
            UniqueId id = ServiceInfoUtil.getInstance().getUniqueId(e.serviceInfo());
            if (id != null) {
                // Resources are automatically deployed because of autoDeleteOnStop
                NodePServerProvider.instance().lifecycleStopped(id);
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
            LOGGER.error("error during copy for pserver", e);
        }
    }

    private Path getFile(CloudService service) throws IOException {
        Path plugins = service.pluginDirectory();
        Files.createDirectories(plugins);
        return plugins.resolve(PServerModule.PLUGIN_NAME);
    }
}
