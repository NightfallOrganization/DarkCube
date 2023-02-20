/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.StatsAPI;
import eu.darkcube.system.stats.api.mysql.MySQL;

public class StatsLink {

	private static boolean isStats;
	public static boolean enabled = true;

	public static double getAddOrRemoveElo(double elo1, double elo2) {
		double elo = 100 * (1 - 1 / (1 + Math.pow(4, ((elo2 - elo1 - 1000) / (400)))));
		return elo;
	}

	static {
		try {
			Class.forName("eu.darkcube.system.stats.api.StatsAPI");
			isStats = true;
		} catch (Exception ex) {
			isStats = false;
		}
	}

	public static void updateKillElo(WBUser killer, WBUser dead) {
		if (isStats()) {
			for (Duration duration : Duration.values()) {
				double elo1 = StatsAPI.getUser(killer.getUniqueId()).getWoolBattleStats(duration).getElo();
				double elo2 = StatsAPI.getUser(dead.getUniqueId()).getWoolBattleStats(duration).getElo();
				double elo = getAddOrRemoveElo(elo1, elo2);
				elo1 += elo;
				elo2 -= elo;
				MySQL.setWoolBattleElo(StatsAPI.getUser(killer.getUniqueId()), duration, elo1);
				MySQL.setWoolBattleElo(StatsAPI.getUser(dead.getUniqueId()), duration, elo2);
			}
		}
	}

	public static void addDeath(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).addWoolBattleDeath();
		}
	}

	public static void addWin(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).addWoolBattleWin();
		}
	}

	public static void addLoss(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).addWoolBattleLoss();
		}
	}

	public static void addKill(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).addWoolBattleKill();
		}
	}

	public static void removeDeath(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).removeWoolBattleDeath();
		}
	}

	public static void removeWin(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).removeWoolBattleWin();
		}
	}

	public static void removeLoss(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).removeWoolBattleLoss();
		}
	}

	public static void removeKill(WBUser user) {
		if (isStats()) {
			StatsAPI.getUser(user.getUniqueId()).removeWoolBattleKill();
		}
	}
	
	public static boolean isStats() {
		return isStats && enabled;
	}
}
