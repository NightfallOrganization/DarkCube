/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game;

import eu.darkcube.system.sumo.game.items.StickItem;
import eu.darkcube.system.sumo.game.items.WoolItem;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scoreboard.Team;

public class GameItemManager implements Listener {
    private TeamManager teamManager;

    public GameItemManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();

        // Wenn der Spieler nicht in der Welt "world" ist
        if (!player.getWorld().getName().equalsIgnoreCase("world")) {
            setGameItems(player);
        }
    }

    private void setGameItems(Player player) {
        // Setze die Items auf die entsprechenden Slots
        player.getInventory().setItem(0, StickItem.getItem());

        DyeColor woolColor = DyeColor.WHITE; // Standardfarbe ist Weiß

        if (teamManager.isPlayerInTeam(player)) {
            Team team = teamManager.playerTeams.get(player);
            if (team.getName().equalsIgnoreCase("Black")) {
                woolColor = DyeColor.BLACK;
            } else if (team.getName().equalsIgnoreCase("White")) {
                woolColor = DyeColor.WHITE;
            }
        }

        player.getInventory().setItem(1, WoolItem.getItem(woolColor));
    }
}
