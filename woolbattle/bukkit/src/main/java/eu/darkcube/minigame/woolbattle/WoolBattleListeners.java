/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import eu.darkcube.minigame.woolbattle.listener.*;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerWeatherChange;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class WoolBattleListeners {
    private final List<Listener> listeners = new ArrayList<>();

    public WoolBattleListeners(WoolBattleBukkit woolbattle) {
        // Init listeners
        listeners.add(new ListenerInventoryClose());
        listeners.add(new ListenerInventoryClick(woolbattle));
        listeners.add(new ListenerPlayerInteract());
        listeners.add(new ListenerFoodLevelChange());
        listeners.add(new ListenerWeatherChange());
        listeners.add(new ListenerLaunchable());
        listeners.add(new ListenerChat(woolbattle));
        listeners.add(new ListenerAntiMonster());
    }

    public void registerAll(WoolBattleBukkit woolbattle) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, woolbattle);
        }
    }

    public void unregisterAll() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
