/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public interface TeamManager {

    Team getTeam(UUID id);

    Team getTeam(String displayNameKey);

    Team getTeam(TeamType type);

    Team getSpectator();

    Team getTeam(WBUser user);

    Team loadTeam(TeamType teamType);

    void unloadTeam(Team team);

    void setTeam(WBUser user, Team team);

    Collection<? extends Team> getTeams();

    void save(TeamType teamType);

    void delete(TeamType teamType);

    TeamType create(MapSize mapSize, String displayNameKey, int weight, DyeColor woolcolor, ChatColor namecolor, boolean enabled);

    TeamType byDisplayNameKey(MapSize size, String displayNameKey);

    Collection<TeamType> teamTypes(MapSize mapSize);

    Collection<TeamType> teamTypes();
}
