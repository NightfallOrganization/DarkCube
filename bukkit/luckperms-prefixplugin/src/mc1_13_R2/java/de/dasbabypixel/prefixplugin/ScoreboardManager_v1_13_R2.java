/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

class ScoreboardManager_v1_13_R2 extends IScoreboardManager {

	//	@Override
	//	protected void setPrefix(UUID uuid, String prefix) {
	//		super.setPrefix(uuid, prefix);
	//		setPrefixAfter1_13(uuid, prefix);
	//	}

	@Override
	protected void setPrefix(Team team, String prefix) {
		super.setPrefix(team, prefix);
		char code = 'f';
		for (int i = 0; i < prefix.length(); i++) {
			if (prefix.charAt(i) == 167) {
				code = prefix.charAt(i + 1);
			}
		}
		team.setColor(ChatColor.getByChar(code));
	}
	//
	//	protected void setPrefixAfter1_13(UUID uuid, String prefix) {
	//
	//		ScoreboardTag tag = ScoreboardTag.getScoreboardTag(uuid);
	//		for (Scoreboard sb : SCOREBOARD_BY_UUID.values()) {
	//			Team team = sb.getTeam(tag.toString());
	//			if (team != null)
	//				work(team, tag, code);
	//		}
	//	}
	//
	//	protected void work(Team team, ScoreboardTag tag, char code) {
	//		team.setColor(ChatColor.getByChar(code));
	//	}
}
