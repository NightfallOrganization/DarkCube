/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerPlayerJoin extends Listener<PlayerJoinEvent> {
    private final Lobby lobby;

    public ListenerPlayerJoin(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    @EventHandler
    public void handle(PlayerJoinEvent e) {
        lobby.setupPlayer(WBUser.getUser(e.getPlayer()), true);
        e.setJoinMessage(null);
    }
}
