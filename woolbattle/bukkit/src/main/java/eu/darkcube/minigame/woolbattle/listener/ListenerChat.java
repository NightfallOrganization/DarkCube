/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.stream.Collectors;

public class ListenerChat extends Listener<AsyncPlayerChatEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerChat(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override @EventHandler public void handle(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        var user = WBUser.getUser(p);
        var msg = e.getMessage();
        var atall = true;
        var replace = !woolbattle.lobby().enabled();
        var startsatall = false;
        var addPrefixAtAll = false;
        if (woolbattle.ingame().enabled()) {
            startsatall = msg.startsWith(woolbattle.atall);
            var startsatteam = msg.startsWith(woolbattle.atteam);
            atall = startsatall || user.getTeam().getUsers().size() == 1;
            if (msg.length() >= woolbattle.atall.length() + 1) {
                if ((startsatall && msg.substring(woolbattle.atall.length()).charAt(0) == ' ') || (startsatteam && msg
                        .substring(woolbattle.atall.length())
                        .charAt(0) == ' ')) {
                    msg = msg.replaceFirst(" ", "");
                }
            }
            if (startsatteam) {
                atall = false;
                msg = msg.substring(2);
            }
            if (user.getTeam().isSpectator()) {
                atall = false;
            }
        }
        var color = user.getTeam().getType().getNameColor().toString();
        e.setCancelled(true);
        if (msg.isEmpty() || (startsatall && msg.substring(2).isEmpty())) {
            return;
        }
        msg = getMessage(p, msg, atall, color, woolbattle, startsatall);

        if (user.getTeam().isSpectator()) {
            woolbattle.sendMessageWithoutPrefix(msg, user
                    .getTeam()
                    .getUsers()
                    .stream()
                    .map(WBUser::getBukkitEntity)
                    .collect(Collectors.toSet()));
            woolbattle.sendConsoleWithoutPrefix(msg);
        } else {
            for (var t : WBUser.onlineUsers()) {
                var pmsg = msg;
                var be = t.getBukkitEntity();
                if (be == null) continue;
                if(replace) {
                    pmsg = pmsg.replaceFirst(woolbattle.atall,LegacyComponentSerializer.legacySection().serialize(Message.AT_ALL.getMessage(t)));
                }
                if (atall && replace) {
                    pmsg = pmsg.replaceFirst(woolbattle.atall, LegacyComponentSerializer
                            .legacySection()
                            .serialize(Message.AT_ALL.getMessage(t)));
                    be.sendMessage(pmsg);
                } else if (t.getTeam().getType().equals(user.getTeam().getType())) {
                    be.sendMessage(pmsg);
                }
            }
            woolbattle.sendConsoleWithoutPrefix(msg);
        }
    }

    private String getMessage(Player p, String msg, boolean atall, String color, WoolBattleBukkit main, boolean satall) {
        var prefix = (atall && main.ingame().enabled() ? main.atall : "");
        return color + prefix + color + p.getName() + ChatColor.WHITE + ": " + (satall ? msg.substring(main.atall.length()) : msg);
    }
}
