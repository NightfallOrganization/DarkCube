/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerPlayerQuit(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override @EventHandler public void handle(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (user.getTeam().isSpectator()) {
            return;
        }
        Team t = woolbattle.ingame().lastTeam.remove(user);
        if (t != null) {
            if (!t.getUsers().isEmpty()) {
                StatsLink.addLoss(user);
            }
        }
        woolbattle.sendMessage(Message.PLAYER_LEFT, user.getTeamPlayerName());
        woolbattle.ingame().playerUtil().kill(user, true);
    }

}
