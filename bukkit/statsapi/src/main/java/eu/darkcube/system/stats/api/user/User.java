/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.user;

import java.util.Arrays;
import java.util.UUID;

import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.gamemode.StatsWoolBattle;
import eu.darkcube.system.stats.api.mysql.MySQL;
import eu.darkcube.system.stats.api.stats.Stats;

public class User {

	private String name;
	private UUID uuid;

	public User(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public StatsWoolBattle getWoolBattleStats(Duration duration) {
		return MySQL.getStatsWoolBatte(this, duration);
	}

	public Stats getLastStats(Duration duration) {
//		return getWoolBattleStats(duration);
		switch (MySQL.getLastGameMode(this)) {
		case MINERS:
		break;
		case SMASH:
		break;
		case WOOLBATTLE:
			return getWoolBattleStats(duration);
		}
		return null;
	}

	public void setUniqueId(UUID uuid) {
		this.uuid = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addWoolBattleKill() {
		durations().forEach(d -> {
			MySQL.setWoolBattleKills(this, d, MySQL.getWoolBattleKills(this, d)
							+ 1);
		});
	}

	public void removeWoolBattleKill() {
		durations().forEach(d -> {
			MySQL.setWoolBattleKills(this, d, MySQL.getWoolBattleKills(this, d)
							- 1);
		});
	}

	public void addWoolBattleDeath() {
		durations().forEach(d -> {
			MySQL.setWoolBattleDeaths(this, d, MySQL.getWoolBattleDeaths(this, d)
							+ 1);
		});
	}

	public void removeWoolBattleDeath() {
		durations().forEach(d -> {
			MySQL.setWoolBattleDeaths(this, d, MySQL.getWoolBattleDeaths(this, d)
							- 1);
		});
	}

	public void addWoolBattleWin() {
		durations().forEach(d -> {
			MySQL.setWoolBattleWins(this, d, MySQL.getWoolBattleWins(this, d)
							+ 1);
		});
	}

	public void removeWoolBattleWin() {
		durations().forEach(d -> {
			MySQL.setWoolBattleWins(this, d, MySQL.getWoolBattleWins(this, d)
							- 1);
		});
	}

	public void addWoolBattleLoss() {
		durations().forEach(d -> {
			MySQL.setWoolBattleLosses(this, d, MySQL.getWoolBattleLosses(this, d)
							+ 1);
		});
	}

	public void removeWoolBattleLoss() {
		durations().forEach(d -> {
			MySQL.setWoolBattleLosses(this, d, MySQL.getWoolBattleLosses(this, d)
							- 1);
		});
	}

	public void addWoolBattleElo(long elo) {
		durations().forEach(d -> {
			MySQL.setWoolBattleElo(this, d, MySQL.getWoolBattleElo(this, d)
							+ elo);
		});
	}

	public void removeWoolBattleElo(long elo) {
		durations().forEach(d -> {
			MySQL.setWoolBattleElo(this, d, MySQL.getWoolBattleElo(this, d)
							- elo);
		});
	}

	private static final Iterable<Duration> durations() {
		return Arrays.asList(Duration.values());
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || ((obj != null && obj instanceof User)
						&& ((User) obj).uuid.equals(uuid));
	}

}
