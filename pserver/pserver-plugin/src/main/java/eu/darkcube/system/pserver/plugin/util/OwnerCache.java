/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.util;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.pserver.bukkit.event.PServerAddOwnerEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerRemoveOwnerEvent;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;

public class OwnerCache {

    private final Collection<UUID> owners = new CopyOnWriteArraySet<>();
    private volatile UniqueId uid;

    public void register() {
        InjectionLayer.boot().instance(EventManager.class).registerListener(this);
        try {
            this.uid = PServerProvider.instance().currentPServer().id();
            this.owners.addAll(PServerProvider.instance().currentPServer().owners().get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregister() {
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(this);
    }

    @EventListener
    public void handle(PServerAddOwnerEvent event) {
        if (!event.pserver().id().equals(uid)) {
            return;
        }
        owners.remove(event.getOwner());
    }

    @EventListener
    public void handle(PServerRemoveOwnerEvent event) {
        if (!event.pserver().id().equals(uid)) {
            return;
        }
        owners.remove(event.getOwner());
    }

    public Collection<UUID> owners() {
        return Collections.unmodifiableCollection(owners);
    }
}
