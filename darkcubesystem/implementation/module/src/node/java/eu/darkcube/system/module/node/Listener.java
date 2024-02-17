/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import eu.cloudnetservice.node.service.CloudService;
import eu.darkcube.system.module.DarkCubeSystemModule;

@Singleton public class Listener {

    private final ModuleHelper moduleHelper;

    @Inject public Listener(ModuleHelper moduleHelper) {
        this.moduleHelper = moduleHelper;
    }

    @EventListener public void handle(CloudServicePreProcessStartEvent e) {
        this.copy(e.service());
    }

    private void copy(CloudService service) {
        try {
            var pluginJar = pluginJarFileName(service);
            if (pluginJar != null) {
                var path = this.getFile(service.pluginDirectory().resolve(DarkCubeSystemModule.PLUGIN_NAME));
                copyPlugin(pluginJar, path);
            }
            moduleHelper.copyJarContainingClass(Listener.class, getFile(service.directory().resolve(".wrapper").resolve("modules").resolve(DarkCubeSystemModule.PLUGIN_NAME)));
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "error during copy for system", e);
        }
    }

    private void copyPlugin(String pluginJar, Path path) {
        try {
            var in = getClass().getClassLoader().getResourceAsStream("plugins/" + pluginJar);
            if (in == null) throw new AssertionError("Plugin not found: " + pluginJar);
            Files.copy(in, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String pluginJarFileName(CloudService service) {
        var env = service.serviceId().environment();
        if (env.equals(ServiceEnvironmentType.MINECRAFT_SERVER)) {
            return "bukkit.jar";
        } else if (env.equals(ServiceEnvironmentType.VELOCITY)) {
            return "velocity.jar";
        } else if (env.equals(ServiceEnvironmentType.MINESTOM)) {
            return "minestom.jar";
        }
        return null;
    }

    private Path getFile(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        Files.deleteIfExists(path);
        return path;
    }
}
