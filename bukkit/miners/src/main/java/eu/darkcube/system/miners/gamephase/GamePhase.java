/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.miners.Miners;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class GamePhase {
    protected final List<Listener> listeners = new ArrayList<>();
    private boolean enabled;

    public void enable() {
        if (enabled) return;
        enabled = true;
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Miners.getInstance());
        }
    }

    public void disable() {
        if (!enabled) return;
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    protected abstract void onEnable();

    protected abstract void onDisable();
}
