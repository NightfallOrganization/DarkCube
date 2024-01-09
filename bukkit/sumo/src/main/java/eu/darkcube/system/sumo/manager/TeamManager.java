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
import org.bukkit.ChatColor;

public class TeamManager {
    private final Map<UUID, ChatColor> playerTeams;

    public TeamManager() {
        playerTeams = new HashMap<>();
    }

    public void setPlayerTeam(UUID playerID, ChatColor teamColor) {
        playerTeams.put(playerID, teamColor);
    }

    public ChatColor getPlayerTeam(UUID playerID) {
        return playerTeams.getOrDefault(playerID, null);
    }

    public static final ChatColor TEAM_WHITE = ChatColor.WHITE;
    public static final ChatColor TEAM_BLACK = ChatColor.BLACK;
}
