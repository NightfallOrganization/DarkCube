/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.util.scoreboard;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.scoreboard.NameTagVisibility;

public class ScoreboardTeam {

	private org.bukkit.scoreboard.Team team;
	private Scoreboard sb;

	public ScoreboardTeam(Scoreboard sb, org.bukkit.scoreboard.Team team) {
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

	public void allowFriendlyFire(boolean flag) {
		team.setAllowFriendlyFire(flag);
	}

	public void canSeeFriendlyInvisible(boolean flag) {
		team.setCanSeeFriendlyInvisibles(flag);
	}

	public void displayName(String name) {
		team.setDisplayName(name);
	}

	public void setPrefix(Component prefix) {
		String p = LegacyComponentSerializer.legacySection().serialize(prefix);
		if (p.length() > 16)
			p = p.substring(0, 16);
		team.setPrefix(p);
	}

	public void setSuffix(Component suffix) {
		String s = LegacyComponentSerializer.legacySection().serialize(suffix);
		if (s.length() > 16)
			s = s.substring(0, 16);
		team.setSuffix(s);
	}

	public void nameTagVisibility(NameTagVisibility visibility) {
		team.setNameTagVisibility(visibility);
	}

	public Scoreboard getScoreboard() {
		return sb;
	}
}
