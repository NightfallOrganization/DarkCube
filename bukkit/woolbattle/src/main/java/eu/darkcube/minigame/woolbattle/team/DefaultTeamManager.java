/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.team;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultTeamManager implements TeamManager {

	private final Collection<Team> TEAMS;
	private final Map<WBUser, Team> TEAM_BY_USER;
	private Team SPECTATOR;

	public DefaultTeamManager() {
		TEAMS = new HashSet<>();
		TEAM_BY_USER = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	public final DefaultTeamManager loadSpectator(TeamType spectator) {
		if (TeamType.SPECTATOR != null) {
			spectator = null;
		}
		if (spectator == null) {
			TeamType.SPECTATOR =
					new TeamType("SPECTATOR", 99, DyeColor.GRAY.getWoolData(), ChatColor.GRAY,
							false, -1);
			SPECTATOR = getOrCreateTeam(TeamType.SPECTATOR);
			TEAMS.remove(SPECTATOR);
		} else {
			TeamType.SPECTATOR = spectator;
			SPECTATOR = getOrCreateTeam(TeamType.SPECTATOR);
			TEAMS.remove(SPECTATOR);
		}
		return this;
	}

	@Override
	public Team getTeam(UUID id) {
		for (Team team : TEAMS) {
			if (team.getUniqueId().equals(id)) {
				return team;
			}
		}
		return null;
	}

	@Override
	public Team getTeam(String displayNameKey) {
		Set<Team> teams = getTeams().stream()
				.filter(t -> t.getType().getDisplayNameKey().equals(displayNameKey))
				.collect(Collectors.toSet());
		for (Team team : teams)
			return team;
		return null;
	}

	@Override
	public Team getTeam(TeamType type) {
		for (Team team : TEAMS) {
			if (team.getType().equals(type)) {
				return team;
			}
		}
		return SPECTATOR;
	}

	@Override
	public Team getTeam(String displayName, Language inLanguage) {
		for (Team team : TEAMS) {
			if (Message.getMessage(team.getType().getDisplayNameKey(), inLanguage)
					.equals(displayName)) {
				return team;
			}
		}
		return null;
	}

	@Override
	public Team getOrCreateTeam(TeamType type) {
		Team team = getTeam(type);
		if (team != SPECTATOR && team != null)
			return team;
		team = new DefaultTeam(type);
		TEAMS.add(team);
		return team;
	}

	@Override
	public Team getSpectator() {
		return SPECTATOR;
	}

	@Override
	public Team getTeam(WBUser user) {
		return TEAM_BY_USER.get(user);
	}

	@Override
	public void setTeam(WBUser user, Team team) {
		Team t = getTeam(user);
		for (Player p : Bukkit.getOnlinePlayers()) {
			WBUser u = WBUser.getUser(p);
			Scoreboard s = new Scoreboard(u);
			if (t != null)
				s.getTeam(t.getType().getScoreboardTag()).removePlayer(user.getPlayerName());
			s.getTeam(team.getType().getScoreboardTag()).addPlayer(user.getPlayerName());
		}
		if (!team.isSpectator()) {
			for (Player o : Bukkit.getOnlinePlayers()) {
				o.showPlayer(user.getBukkitEntity());
			}
		}
		TEAM_BY_USER.put(user, team);
		if (WoolBattle.instance().getIngame().enabled()) {
			WoolBattle.instance().getIngame().setPlayerItems(user);
			WoolBattle.instance().getIngame().checkGameEnd();
		}
	}

	@Override
	public Collection<? extends Team> getTeams() {
		return Collections.unmodifiableCollection(TEAMS);
	}
}
