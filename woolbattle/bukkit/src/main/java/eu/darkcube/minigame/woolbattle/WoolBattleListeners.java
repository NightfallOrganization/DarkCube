/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.minigame.woolbattle.listener.ListenerAntiMonster;
import eu.darkcube.minigame.woolbattle.listener.ListenerChat;
import eu.darkcube.minigame.woolbattle.listener.ListenerFoodLevelChange;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClick;
import eu.darkcube.minigame.woolbattle.listener.ListenerInventoryClose;
import eu.darkcube.minigame.woolbattle.listener.ListenerLaunchable;
import eu.darkcube.minigame.woolbattle.listener.ListenerPlayerInteract;
import eu.darkcube.minigame.woolbattle.listener.lobby.ListenerWeatherChange;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

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
