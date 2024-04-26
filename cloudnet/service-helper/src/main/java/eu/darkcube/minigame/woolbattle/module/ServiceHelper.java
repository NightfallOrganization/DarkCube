/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.template.TemplateStorage;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

@Singleton public class ServiceHelper extends DriverModule {

    private ServiceHelperConfig config;

    @ModuleTask(order = 0, lifecycle = ModuleLifeCycle.STARTED)
    public void start(ServiceRegistry serviceRegistry, EventManager eventManager, ServiceListener serviceListener) {
        reloadConfiguration();
        registerStorages(serviceRegistry);
        eventManager.registerListener(serviceListener);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.RELOADING) public void reload(ServiceRegistry serviceRegistry) {
        unregisterStorages(serviceRegistry);
        reloadConfiguration();
        registerStorages(serviceRegistry);
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void stop(ServiceRegistry serviceRegistry, EventManager eventManager, ServiceListener serviceListener) {
        unregisterStorages(serviceRegistry);
        eventManager.unregisterListener(serviceListener);
    }

    private void unregisterStorages(ServiceRegistry serviceRegistry) {
        for (ServiceHelperConfig.CustomStorage storage : config.customStorages()) {
            serviceRegistry.unregisterProvider(TemplateStorage.class, storage.name());
        }
    }

    private void registerStorages(ServiceRegistry serviceRegistry) {
        for (ServiceHelperConfig.CustomStorage customStorageConfiguration : config.customStorages()) {
            serviceRegistry.registerProvider(TemplateStorage.class, customStorageConfiguration.name(), new CustomTemplateStorage(customStorageConfiguration));
        }
    }

    private void reloadConfiguration() {
        config = readConfig(ServiceHelperConfig.class, defaultConfig(), DocumentFactory.json());
    }

    public ServiceHelperConfig config() {
        return config;
    }

    private Supplier<ServiceHelperConfig> defaultConfig() {
        return () -> new ServiceHelperConfig(Set.of(new ServiceHelperConfig.ServiceTaskEntry("woolbattle", 2, 1)), Set.of(new ServiceHelperConfig.CustomStorage("woolbattle", Path.of("local", "woolbattle"))));
    }
}
