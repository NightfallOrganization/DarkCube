/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

/**
 * TODO: Update this to be more efficient in creating new instances. Maybe not load all objectives and teams on instance creation and instead only when required
 */
public class Scoreboard {

    private final org.bukkit.scoreboard.Scoreboard sb;
    private final Map<String, ScoreboardTeam> teams;
    private final Map<String, Objective> objectives;

    public Scoreboard(WBUser owner) {
        org.bukkit.scoreboard.Scoreboard bs = owner.getBukkitEntity().getScoreboard();
        if (bs == Bukkit.getScoreboardManager().getMainScoreboard()) {
            bs = Bukkit.getScoreboardManager().getNewScoreboard();
            owner.getBukkitEntity().setScoreboard(bs);
        }
        sb = bs;
        teams = new HashMap<>();
        sb.getTeams().forEach(team -> teams.put(team.getName(), new ScoreboardTeam(this, team)));
        objectives = new HashMap<>();
        sb.getObjectives().forEach(obj -> objectives.put(obj.getName(), new Objective(obj)));
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

    @Deprecated
    public ScoreboardTeam createTeam(String name) {
        org.bukkit.scoreboard.Team team = sb.registerNewTeam(name);
        ScoreboardTeam t = new ScoreboardTeam(this, team);
        teams.put(name, t);
        return t;
    }

    public ScoreboardTeam getTeam(String name) {
        if (teams.get(name) == null) {
            Team sbteam = sb.getTeam(name);
            if (sbteam == null) sbteam = sb.registerNewTeam(name);
            teams.put(name, new ScoreboardTeam(this, sbteam));
        }
        return teams.get(name);
    }

    public org.bukkit.scoreboard.Scoreboard getScoreboard() {
        return sb;
    }
}
