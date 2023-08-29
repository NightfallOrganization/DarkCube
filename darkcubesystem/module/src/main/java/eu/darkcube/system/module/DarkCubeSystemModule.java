/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.darkcube.system.module.userapi.NodeUserAPI;
import eu.darkcube.system.module.util.data.SynchronizedPersistentDataStorages;
import eu.darkcube.system.packetapi.PacketAPI;

import java.io.File;

public class DarkCubeSystemModule extends DriverModule {
    public static final String PLUGIN_NAME = new File(DarkCubeSystemModule.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()).getName();
    private final Listener listener = new Listener();

    @ModuleTask(order = 0, lifecycle = ModuleLifeCycle.STARTED) public void start(EventManager eventManager, NodeUserAPI userAPI) {
        PacketAPI.init();
        userAPI.init();
        eventManager.registerListener(userAPI);
        eventManager.registerListener(listener);
        SynchronizedPersistentDataStorages.init();
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED) public void stop(EventManager eventManager, NodeUserAPI userAPI) {
        eventManager.unregisterListener(userAPI);
        eventManager.unregisterListener(listener);
        userAPI.exit();
    }
}
