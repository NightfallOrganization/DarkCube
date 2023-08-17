/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.module;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.template.TemplateStorage;

import java.nio.file.Paths;

public class WoolBattle extends DriverModule {

    private ServiceRegistry serviceRegistry = InjectionLayer.boot().instance(ServiceRegistry.class);

    @ModuleTask(order = 0, lifecycle = ModuleLifeCycle.STARTED) public void start() {
        serviceRegistry.registerProvider(TemplateStorage.class, "woolbattle", new WoolBattleTemplateStorage(Paths.get("local/woolbattle")));
    }
}
