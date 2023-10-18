/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.UUID;
import java.util.Arrays;

public class TextSound implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Liste der zugelassenen UUIDs
        List<UUID> allowedUUIDs = Arrays.asList(
                UUID.fromString("b0c9246e-7201-4d26-a775-af42216d9501"),
                UUID.fromString("5786ac3a-6458-4749-b3c2-5bc88ebaf2cd"),
                UUID.fromString("084ef853-25b7-4024-9539-4b07dd1e74ef"),
                UUID.fromString("fd16b196-2b44-4ae8-96b7-c33ba4769b99"),
                UUID.fromString("04bca12b-d2c3-43d5-bdd6-39cf441f4152")
        );

        // Überprüfen, ob der Spieler in der zugelassenen Liste ist
        if (allowedUUIDs.contains(event.getPlayer().getUniqueId())) {
            // Spielt den Sound für den Spieler ab, wenn er in der Liste ist
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.WOOD_CLICK, 1.0f, 1.5f);
        }
    }
}