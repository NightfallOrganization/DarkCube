/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.minigame.woolbattle.util.ScoreboardObjective;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

public class ScoreboardHelper {

    public static void initTeam(Scoreboard sb, Team team) {
        ScoreboardTeam sbteam = sb.getTeam(team.getType().getScoreboardTag());
        sbteam.setPrefix(Component.text("", team.getPrefixStyle()));
    }

    public static void initObjectives(Scoreboard sb, WBUser owner, ScoreboardObjective... objectives) {
        for (ScoreboardObjective objective : objectives) {
            Objective o = sb.getObjective(objective.getKey());
            if (o == null) o = sb.createObjective(objective.getKey(), "dummy");
            o.setDisplayName(Message.getMessage(objective.getMessageKey(), owner.getLanguage()));
        }
    }

    public static void initObjectiveTeams(Scoreboard sb, WBUser owner, ObjectiveTeam... objectiveTeams) {
        for (ObjectiveTeam objective : objectiveTeams) {
            ScoreboardTeam sbteam = sb.getTeam(objective.getKey());
            sbteam.setPrefix(Message.getMessage(objective.getMessagePrefix(), owner.getLanguage()));
            sbteam.setSuffix(Message.getMessage(objective.getMessageSuffix(), owner.getLanguage()));
        }
    }

    public static void setNeeded(WoolBattle woolbattle, WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.NEEDED.getKey());
        team.setSuffix(Component.text(woolbattle.lobby().minPlayerCount()));
    }

    public static void setOnline(WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.ONLINE.getKey());
        team.setSuffix(text(WBUser.onlineUsers().size()));
    }

    public static void setEpGlitch(WoolBattle woolbattle, WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.EP_GLITCH.getKey());
        team.setSuffix((woolbattle.gameData().epGlitch() ? Message.EP_GLITCH_ON : Message.EP_GLITCH_OFF).getMessage(user));
    }

    public static void setMap(WoolBattle woolbattle, WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.MAP.getKey());
        Map m = woolbattle.gameData().map();
        team.setSuffix(m == null ? text("No Maps").color(NamedTextColor.RED) : text(m.getName()));
    }
}
