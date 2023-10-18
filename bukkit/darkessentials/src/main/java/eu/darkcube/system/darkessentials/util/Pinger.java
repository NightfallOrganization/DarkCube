/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.stream.Collectors;

public class Pinger implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player sender = event.getPlayer();

        // Liste der zugelassenen UUIDs
        List<UUID> allowedUUIDs = Arrays.asList(
                UUID.fromString("084ef853-25b7-4024-9539-4b07dd1e74ef"),
                UUID.fromString("b0c9246e-7201-4d26-a775-af42216d9501"),
                UUID.fromString("08d8c006-fb59-4015-9a3d-c7c127413939"),
                UUID.fromString("5786ac3a-6458-4749-b3c2-5bc88ebaf2cd"),
                UUID.fromString("fd16b196-2b44-4ae8-96b7-c33ba4769b99"),
                UUID.fromString("04bca12b-d2c3-43d5-bdd6-39cf441f4152")
        );

        // Wenn der Sender nicht in der zugelassenen Liste ist, kehren Sie sofort zurück
        if (!allowedUUIDs.contains(sender.getUniqueId())) {
            return;
        }

        // Überprüfen, ob die Nachricht nur "@" enthält
        if (message.equals("@")) {
            List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());

            String suggestion = String.join(", ", onlinePlayers);
            event.getPlayer().sendMessage("Online Spieler: " + suggestion);
            event.setCancelled(true);  // Die Nachricht nicht im Chat anzeigen
            return;
        }

        // Überprüfen, ob die Nachricht ein "@ Spielername"-Muster enthält
        Pattern pattern = Pattern.compile("@\\s?\\w+");
        Matcher matcher = pattern.matcher(message);

        // Liste der aktuellen Online-Spielernamen abrufen
        List<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        while (matcher.find()) {
            String matched = matcher.group();
            String playerName = matched.replace("@", "").trim();

            if (isPlayerInTabList(playerName)) {
                // Nur wenn der Spieler in der Tabliste ist, führen Sie den Code aus
                // Das Muster durch den grün hervorgehobenen Spielernamen ersetzen, gefolgt von grauem Text
                message = message.replace(matched, ChatColor.GREEN + playerName + ChatColor.GRAY);

                // Dem angegebenen Spieler ein Geräusch senden
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equalsIgnoreCase(playerName)) {
                        player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 2);
                        break;
                    }
                }
            }

        }

        event.setMessage(message);
    }

    public boolean isPlayerInTabList(String playerName) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getPlayerListName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

}