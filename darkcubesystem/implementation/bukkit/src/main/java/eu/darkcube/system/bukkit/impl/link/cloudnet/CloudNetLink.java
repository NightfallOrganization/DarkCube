/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.link.cloudnet;

import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.bukkit.impl.DarkCubeSystemBukkit;
import eu.darkcube.system.bukkit.link.Link;
import eu.darkcube.system.server.util.DarkCubeServer;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class CloudNetLink extends Link {
    private final DarkCubeSystemBukkit system;
    private Listener listener;

    public CloudNetLink(DarkCubeSystemBukkit system) throws Throwable {
        this.system = system;
    }

    @Override protected void link() {
        var eventManager = InjectionLayer.ext().instance(EventManager.class);
        eventManager.registerListener(listener = new Listener());
        InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
    }

    @Override protected void onEnable() {
        Bukkit.getPluginManager().registerEvents(listener, system);
        Bukkit.getScheduler().runTask(system, () -> {
            if (DarkCubeServer.autoConfigure()) {
                DarkCubeServer.gameState(GameState.INGAME);
                InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
            }
        });
    }

    @Override protected void unlink() {
        InjectionLayer.ext().instance(EventManager.class).unregisterListener(listener);
        HandlerList.unregisterAll(listener);
        listener = null;
    }
}
