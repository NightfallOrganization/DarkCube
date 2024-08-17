/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.listener;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;

public class MinestomQuitListener {
    public static final ThreadLocal<List<MinestomPlayer>> WORKING = ThreadLocal.withInitial(ArrayList::new);

    public static void register(MinestomWoolBattle woolbattle, EventNode<Event> node) {
        node.addListener(PlayerDisconnectEvent.class, event -> {
            var working = WORKING.get();
            @NotNull var player = (MinestomPlayer) event.getPlayer();
            if (working.contains(player)) return;
            try {
                working.add(player);
                @Nullable var user = player.user();
                if (user == null) return; // Not registered in our plugin - do nothing
                @Nullable var game = user.game();
                if (game == null) {
                    woolbattle.setupMode().playerQuit(user);
                } else {
                    game.playerQuit(user);
                }
            } finally {
                working.remove(player);
            }
        });
    }
}
