/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.gamemode;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.mysql.MySQL;
import eu.darkcube.system.stats.api.stats.Stats;
import eu.darkcube.system.stats.api.stats.StatsElo;
import eu.darkcube.system.stats.api.stats.StatsKillDeath;
import eu.darkcube.system.stats.api.stats.StatsWinLoss;
import eu.darkcube.system.stats.api.user.User;

public class StatsWoolBattle extends Stats implements StatsWinLoss, StatsKillDeath, StatsElo {

    private long kills;
    private long placementKills;

    private long deaths;
    private long placementDeaths;

    private double killDeathRatio;
    private long placementKillDeathRatio;

    private long wins;
    private long placementWins;

    private long losses;
    private long placementLosses;

    private double winLossRatio;
    private long placementWinLossRatio;

    private double elo;
    private long placementElo;

    public StatsWoolBattle(User owner, Duration duration, GameMode gamemode, long kills, long deaths, long placementKills, long placementDeaths, long placementKillDeathRatio, long wins, long losses, long placementWins, long placementLosses, long placementWinLossRatio, double elo2, long placementElo) {
        super(owner, duration, gamemode);
        this.kills = kills;
        this.deaths = deaths;
        this.killDeathRatio = deaths != 0 ? ((double) kills / (double) deaths) : kills;
        if (MySQL.getWoolBattleKillDeathRatio(owner, duration) != killDeathRatio) {
            MySQL.setWoolBattleKillDeathRatio(owner, duration, killDeathRatio);
        }
        this.placementKills = placementKills;
        this.placementDeaths = placementDeaths;
        this.placementKillDeathRatio = placementKillDeathRatio;
        this.wins = wins;
        this.losses = losses;
        this.winLossRatio = losses != 0 ? ((double) wins / (double) losses) : wins;
        if (MySQL.getWoolBattleWinLossRatio(owner, duration) != winLossRatio) {
            MySQL.setWoolBattleWinLossRatio(owner, duration, winLossRatio);
        }
        this.placementWins = placementWins;
        this.placementLosses = placementLosses;
        this.placementWinLossRatio = placementWinLossRatio;
        this.elo = elo2;
        this.placementElo = placementElo;
    }

    @Override public String toString() {
        return "StatsWoolBattle [kills=" + kills + ", placementKills=" + placementKills + ", deaths=" + deaths + ", placementDeaths=" + placementDeaths + ", killDeathRatio=" + killDeathRatio + ", placementKillDeathRatio=" + placementKillDeathRatio + ", wins=" + wins + ", placementWins=" + placementWins + ", losses=" + losses + ", placementLosses=" + placementLosses + ", winLossRatio=" + winLossRatio + ", placementWinLossRatio=" + placementWinLossRatio + ", elo=" + elo + ", placementElo=" + placementElo + "]";
    }

    @Override protected Component create() {
        return insertElo(this, elo, placementElo)
                .append(insertKillDeath(this, kills, deaths, killDeathRatio, placementKills, placementDeaths, placementKillDeathRatio))
                .append(insertWinLoss(this, wins, losses, winLossRatio, placementWins, placementLosses, placementWinLossRatio));
    }

    @Override public Document serializeData() {
        return Document
                .newJsonDocument()
                .append("kills", kills)
                .append("deaths", deaths)
                .append("wins", wins)
                .append("losses", losses)
                .append("elo", elo);
    }

    @Override public void loadData(Document document) {
        kills = document.getLong("kills");
        deaths = document.getLong("deaths");
        wins = document.getLong("wins");
        losses = document.getLong("losses");
        elo = document.getLong("elo");
    }

    public long getPlacementKills() {
        return placementKills;
    }

    public long getPlacementDeaths() {
        return placementDeaths;
    }

    public long getPlacementKillDeathRatio() {
        return placementKillDeathRatio;
    }

    public long getPlacementWins() {
        return placementWins;
    }

    public long getPlacementLosses() {
        return placementLosses;
    }

    public long getPlacementWinLossRatio() {
        return placementWinLossRatio;
    }

    public long getPlacementElo() {
        return placementElo;
    }

    public long getDeaths() {
        return deaths;
    }

    public double getElo() {
        return elo;
    }

    public double getKillDeathRatio() {
        return killDeathRatio;
    }

    public long getWins() {
        return wins;
    }

    public double getWinLossRatio() {
        return winLossRatio;
    }

    public long getLosses() {
        return losses;
    }

    public long getKills() {
        return kills;
    }
}
