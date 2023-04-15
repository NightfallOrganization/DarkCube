/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util.scoreboard;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

	private org.bukkit.scoreboard.Scoreboard sb;
	private Map<String, ScoreboardTeam> teams;
	private Map<String, Objective> objectives;

	public Scoreboard() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		teams = new HashMap<>();
		objectives = new HashMap<>();
	}

	public Scoreboard(WBUser owner) {
		sb = owner.getBukkitEntity().getScoreboard();
		teams = new HashMap<>();
		sb.getTeams().forEach(team -> {
			teams.put(team.getName(), new ScoreboardTeam(this, team));
		});
		objectives = new HashMap<>();
		sb.getObjectives().forEach(obj -> {
			objectives.put(obj.getName(), new Objective(obj));
		});
	}

	public Objective createObjective(String name, String criteria) {
		org.bukkit.scoreboard.Objective objective = sb.registerNewObjective(name, criteria);
		Objective obj = new Objective(objective);
		objectives.put(name, obj);
		return obj;
	}

	public Objective getObjective(String name) {
		return objectives.get(name);
	}

	public ScoreboardTeam createTeam(String name) {
		org.bukkit.scoreboard.Team team = sb.registerNewTeam(name);
		ScoreboardTeam t = new ScoreboardTeam(this, team);
		teams.put(name, t);
		return t;
	}

	public ScoreboardTeam getTeam(String name) {
		if (teams.get(name) == null) {
			teams.put(name, new ScoreboardTeam(this, sb.getTeam(name)));
		}
		return teams.get(name) == null || teams.get(name).getTeam() == null
				? createTeam(name)
				: teams.get(name);
	}

	public org.bukkit.scoreboard.Scoreboard getScoreboard() {
		return sb;
	}
}
