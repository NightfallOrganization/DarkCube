/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener {

    @EventListener public void handle(CloudServicePreProcessStartEvent e) {
        ServiceEnvironmentType env = e.serviceConfiguration().serviceId().environment();
        if (env.equals(ServiceEnvironmentType.MINECRAFT_SERVER) || env.equals(ServiceEnvironmentType.VELOCITY)) {
            this.copy(e.service());
        }
    }

    private void copy(CloudService service) {
        try {
            Path file = this.getFile(service);
            Files.deleteIfExists(file);
            ModuleHelper moduleHelper = InjectionLayer.boot().instance(ModuleHelper.class);
            moduleHelper.copyJarContainingClass(Listener.class, file);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "error during copy for system", e);
        }
    }

    private Path getFile(CloudService service) throws IOException {
        Path plugins = service.pluginDirectory();
        Files.createDirectories(plugins);
        return plugins.resolve(DarkCubeSystemModule.PLUGIN_NAME);
    }
}
