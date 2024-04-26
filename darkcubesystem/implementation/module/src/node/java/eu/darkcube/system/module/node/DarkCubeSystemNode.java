/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.node;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import dev.derklaro.aerogel.SpecifiedInjector;
import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.module.ModuleImplementation;
import eu.darkcube.system.module.node.data.SynchronizedPersistentDataStorages;
import eu.darkcube.system.module.node.userapi.NodeUserAPI;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.UserAPI;

@Singleton
public class DarkCubeSystemNode implements ModuleImplementation {
    private final Listener listener;
    private final NodeUserAPI userAPI;
    private final EventManager eventManager;

    @Inject
    public DarkCubeSystemNode(Listener listener, NodeUserAPI userAPI, EventManager eventManager) {
        this.listener = listener;
        this.userAPI = userAPI;
        this.eventManager = eventManager;
        InjectionLayer.boot().install(BindingBuilder.create().bind(UserAPI.class).toInstance(userAPI));
    }

    @Override
    public void start(InjectionLayer<SpecifiedInjector> injectionLayer) {
        PacketAPI.init();
        userAPI.init();
        eventManager.registerListener(userAPI);
        eventManager.registerListener(listener);
        SynchronizedPersistentDataStorages.init();
    }

    @Override
    public void stop(InjectionLayer<SpecifiedInjector> injectionLayer) {
        eventManager.unregisterListener(userAPI);
        eventManager.unregisterListener(listener);
        userAPI.exit();
    }
}
