/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.system.sumo.prefix.PrefixManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamManager {
    private final Map<UUID, ChatColor> playerTeams;
    private final PrefixManager prefixManager;

    public TeamManager(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
        playerTeams = new HashMap<>();
    }

    public void setPlayerTeam(Player player, ChatColor teamColor) {
        playerTeams.put(player.getUniqueId(), teamColor);
        prefixManager.setupOtherPlayers(player);
    }

    public void removePlayerTeam(Player player) {
        playerTeams.remove(player.getUniqueId());
        prefixManager.removePlayerPrefix(player);
    }

    public ChatColor getPlayerTeam(UUID playerID) {
        return playerTeams.getOrDefault(playerID, ChatColor.GRAY);
    }

    public boolean isTeamEmpty(ChatColor teamColor) {
        return !playerTeams.containsValue(teamColor);
    }

    public static final ChatColor TEAM_WHITE = ChatColor.WHITE;
    public static final ChatColor TEAM_BLACK = ChatColor.DARK_GRAY;
    public static final ChatColor TEAM_NONE = ChatColor.GRAY;
}
