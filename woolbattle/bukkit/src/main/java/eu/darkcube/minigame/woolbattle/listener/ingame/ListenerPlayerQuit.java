/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

    @Override
    @EventHandler
    public void handle(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        WoolBattle main = WoolBattle.instance();
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (user.getTeam().getType() == TeamType.SPECTATOR) {
            return;
        }
        Team t = WoolBattle.instance().ingame().lastTeam.remove(user);
        if (t != null) {
            if (t.getUsers().size() != 0) {
                StatsLink.addLoss(user);
            }
        }
        WoolBattle.instance().sendMessage(Message.PLAYER_LEFT, user.getTeamPlayerName());
        main.ingame().kill(user, true);
    }

}
