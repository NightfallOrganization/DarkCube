/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerPlayerLogin extends Listener<AsyncPlayerPreLoginEvent> {
    private final Ingame ingame;

    public ListenerPlayerLogin(Ingame ingame) {
        this.ingame = ingame;
    }

    @Override @EventHandler public void handle(AsyncPlayerPreLoginEvent e) {
        if (ingame.startingIngame()) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Loading game...");
        else e.allow();
    }

    @EventHandler public void handle(PlayerLoginEvent e) {
        if (ingame.startingIngame()) e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Loading game...");
        else e.allow();
    }
}
