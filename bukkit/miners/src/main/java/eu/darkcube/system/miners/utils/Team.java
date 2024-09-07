/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils;

import java.util.HashSet;
import java.util.Set;

import eu.darkcube.system.miners.enums.TeleportLocations;
import eu.darkcube.system.miners.utils.message.Message;
import org.bukkit.entity.Player;

public class Team {
    private Message name;
    private Set<Player> players;
    private TeleportLocations teleportLocations;
    private boolean isActive;

    public Team(Message name, TeleportLocations teleportLocations, boolean active) {
        this.name = name;
        this.teleportLocations = teleportLocations;
        this.isActive = active;
        this.players = new HashSet<>();
    }

    public TeleportLocations getTeleportLocations() {
        return teleportLocations;
    }

    public boolean isActive() {
        return isActive;
    }

    public Message getName() {
        return name;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public int getSize() {
        return players.size();
    }

    public static boolean isPlayerInTeam(Team team, Player player) {
        return team.hasPlayer(player);
    }
}
