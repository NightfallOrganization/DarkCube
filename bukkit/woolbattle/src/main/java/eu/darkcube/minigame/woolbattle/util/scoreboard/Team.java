/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import org.bukkit.scoreboard.NameTagVisibility;

public class Team {

	private org.bukkit.scoreboard.Team team;
	private Scoreboard sb;

	public Team(Scoreboard sb, org.bukkit.scoreboard.Team team) {
		this.team = team;
		this.sb = sb;
	}

	public void addPlayer(String name) {
		team.addEntry(name);
	}

	public void removePlayer(String name) {
		team.removeEntry(name);
	}

	public org.bukkit.scoreboard.Team getTeam() {
		return team;
	}

	public void setAllowFriendlyFire(boolean flag) {
		team.setAllowFriendlyFire(flag);
	}

	public void setCanSeeFriendlyInvisibles(boolean flag) {
		team.setCanSeeFriendlyInvisibles(flag);
	}

	public void setDisplayName(String name) {
		team.setDisplayName(name);
	}

	public void setPrefix(String prefix) {
		if (prefix.length() > 16)
			prefix = prefix.substring(0, 16);
		team.setPrefix(prefix);
	}

	public void setSuffix(String suffix) {
		if (suffix.length() > 16)
			suffix = suffix.substring(0, 16);
		team.setSuffix(suffix);
	}

	public void setNameTagVisibility(NameTagVisibility visibility) {
		team.setNameTagVisibility(visibility);
	}

	public Scoreboard getScoreboard() {
		return sb;
	}
}
