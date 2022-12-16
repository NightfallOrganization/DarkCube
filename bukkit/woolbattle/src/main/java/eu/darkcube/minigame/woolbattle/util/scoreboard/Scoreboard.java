/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import eu.darkcube.minigame.woolbattle.user.User;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;

public class Scoreboard {

	private org.bukkit.scoreboard.Scoreboard sb;
	private Map<String, Team> teams;
	private Map<String, Objective> objs;

	public Scoreboard() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		teams = new HashMap<>();
		objs = new HashMap<>();
	}

	public Scoreboard(User owner) {
		sb = owner.getBukkitEntity().getScoreboard();
		teams = new HashMap<>();
		sb.getTeams().forEach(team -> {
			teams.put(team.getName(), new Team(this, team));
		});
		objs = new HashMap<>();
		sb.getObjectives().forEach(obj -> {
			objs.put(obj.getName(), new Objective(obj));
		});
	}

	public Objective createObjective(String name, IScoreboardCriteria criteria) {
		org.bukkit.scoreboard.Objective objective = sb.registerNewObjective(name, criteria.getName());
		Objective obj = new Objective(objective);
		objs.put(name, obj);
		return obj;
	}

	public Objective getObjective(String name) {
		return objs.get(name);
	}

	public Team createTeam(String name) {
		org.bukkit.scoreboard.Team team = sb.registerNewTeam(name);
		Team t = new Team(this, team);
		teams.put(name, t);
		return t;
	}

	public Team getTeam(String name) {
		if (teams.get(name) == null) {
			teams.put(name, new Team(this, sb.getTeam(name)));
		}
		return teams.get(name) == null || teams.get(name).getTeam() == null ? createTeam(name) : teams.get(name);
	}

	public org.bukkit.scoreboard.Scoreboard getScoreboard() {
		return sb;
	}
}
