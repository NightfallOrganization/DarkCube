/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.team;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class TeamColors implements Listener {

    private TeamManager teamManager;

    public TeamColors(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (teamManager.isPlayerInTeam(player)) {
            Team team = teamManager.playerTeams.get(player);
            event.setFormat(team.getPrefix() + player.getName() + "§f: " + event.getMessage());
        } else {
            event.setFormat("§7" + player.getName() + "§f: " + event.getMessage()); // Grau, wenn nicht in einem Team
        }
    }
}
