/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ObjectiveTeam;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

public class ScoreboardHelper {
    public static void setOnline(WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.ONLINE.getKey());
        Component suffix = text(Bukkit.getOnlinePlayers().size());
        team.setSuffix(suffix);
    }

    public static void setEpGlitch(WoolBattle woolbattle, WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.EP_GLITCH.getKey());
        Component suffix = woolbattle.gameData().epGlitch()
                ? Message.EP_GLITCH_ON.getMessage(user)
                : Message.EP_GLITCH_OFF.getMessage(user);
        team.setSuffix(suffix);
    }

    public static void setMap(WoolBattle woolbattle, WBUser user) {
        Scoreboard sb = new Scoreboard(user);
        ScoreboardTeam team = sb.getTeam(ObjectiveTeam.MAP.getKey());
        Map m = woolbattle.gameData().map();
        Component suffix =
                m == null ? text("No Maps").color(NamedTextColor.RED) : text(m.getName());
        team.setSuffix(suffix);
    }
}
