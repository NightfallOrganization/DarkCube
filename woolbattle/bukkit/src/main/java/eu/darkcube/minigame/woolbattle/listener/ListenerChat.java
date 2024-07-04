/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import java.util.stream.Collectors;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ListenerChat extends Listener<AsyncPlayerChatEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerChat(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        var user = WBUser.getUser(p);
        var msg = e.getMessage();
        var atall = true;
        var startsatall = false;
        var ingame = woolbattle.ingame().enabled();
        if (ingame) {
            startsatall = msg.startsWith(woolbattle.atall);
            var startsatteam = msg.startsWith(woolbattle.atteam);
            atall = isIngameAtAll(msg, user);
            if (startsatall) {
                msg = msg.substring(woolbattle.atall.length());
                msg = msg.trim();
            }
            if (startsatteam) {
                msg = msg.substring(woolbattle.atteam.length());
                msg = msg.trim();
            }
        }
        var color = user.getTeam().getType().getNameColor().toString();
        e.setCancelled(true);
        if (msg.isEmpty()) {
            return;
        }
        msg = getMessage(p, msg, atall, color, woolbattle);
        var replaceAtAll = woolbattle.ingame().enabled() && atall;

        if (ingame && user.getTeam().isSpectator()) {
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
                if (atall || t.getTeam().getType().equals(user.getTeam().getType())) {
                    if (replaceAtAll) {
                        pmsg = pmsg.replaceFirst(woolbattle.atall, LegacyComponentSerializer
                                .legacySection()
                                .serialize(Message.AT_ALL.getMessage(t)));
                    }
                    be.sendMessage(pmsg);
                }
            }
            woolbattle.sendConsoleWithoutPrefix(msg);
        }
    }

    private boolean isIngameAtAll(String msg, WBUser user) {
        if (user.getTeam().isSpectator()) return false;
        if (msg.startsWith(woolbattle.atall)) return true;
        if (msg.startsWith(woolbattle.atteam)) return false;
        return user.getTeam().getUsers().size() == 1;
    }

    private String getMessage(Player p, String msg, boolean atall, String color, WoolBattleBukkit main) {
        var prefix = (atall && main.ingame().enabled() ? main.atall : "");
        return color + prefix + color + p.getName() + ChatColor.WHITE + ": " + msg;
    }
}
