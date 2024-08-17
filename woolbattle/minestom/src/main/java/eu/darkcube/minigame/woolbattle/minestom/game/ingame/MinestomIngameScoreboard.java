/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.ingame;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.empty;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.List;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.scoreboard.CommonScoreboardObjective;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Sidebar;

public class MinestomIngameScoreboard {
    private static final byte ALLOW_FRIENDLY_FIRE = 0x01;
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull Key sidebarKey;
    private final @NotNull CommonGame game;

    public MinestomIngameScoreboard(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonGame game) {
        this.woolbattle = woolbattle;
        this.sidebarKey = Key.key(woolbattle, "minestom_sidebar_ingame");
        this.game = game;
    }

    public void setupPlayer(@NotNull MinestomPlayer player, @NotNull CommonWBUser wbUser) {
        for (var team : this.game.teamManager().teams()) {
            setupTeam(player, team);
        }
        var team = Objects.requireNonNull(wbUser.team());
        var packet = addPlayerPacket(team, player);
        for (var user : this.game.users()) {
            if (user == wbUser) continue;
            var p = woolbattle.player(user);
            p.sendPacket(packet);
        }
        var sidebar = new Sidebar(adventureSupport().convert(Messages.getMessage(CommonScoreboardObjective.INGAME.messageKey(), wbUser.language())));
        for (var t : this.game.teamManager().teams()) {
            if (t.spectator()) continue;
            sidebar.createLine(new Sidebar.ScoreboardLine(t.key(), adventureSupport().convert(createLifeLine(wbUser, t)), 1));
        }
        sidebar.addViewer(player);
        wbUser.metadata().set(sidebarKey, sidebar);
    }

    public void switchTeam(@NotNull MinestomPlayer player, @NotNull CommonTeam oldTeam, @NotNull CommonTeam newTeam) {
        var removePacket = removePlayerPacket(oldTeam, player);
        var addPacket = addPlayerPacket(newTeam, player);
        for (var user : this.game.users()) {
            var p = this.woolbattle.player(user);
            p.sendPacket(removePacket);
            p.sendPacket(addPacket);
        }
    }

    public void cleanupPlayer(@NotNull MinestomPlayer player, @NotNull CommonTeam oldTeam) {
        var removePacket = removePlayerPacket(oldTeam, player);
        for (var u : game.users()) {
            var p = woolbattle.player(u);
            p.sendPacket(removePacket);
        }
        var user = player.user();
        if (user != null) {
            var sidebar = user.metadata().<Sidebar>remove(sidebarKey);
            if (sidebar != null) {
                player.sendPacket(sidebar.getDisplayScoreboardPacket((byte) 1));
                sidebar.removeViewer(player);
            }
            for (var team : game.teamManager().teams()) {
                player.sendPacket(deletePacket(team));
            }
        }
    }

    private Component createLifeLine(CommonWBUser user, CommonTeam team) {
        return Messages.INGAME_SIDEBAR_TEAM_LINE.getMessage(user, team.lifes(), team.getName(user));
    }

    private void setupTeam(@NotNull MinestomPlayer player, @NotNull CommonTeam team) {
        var packet = createPacket(team);
        player.sendPacket(packet);
    }

    private TeamsPacket removePlayerPacket(Team team, MinestomPlayer player) {
        var action = new TeamsPacket.RemoveEntitiesToTeamAction(List.of(player.getUsername()));
        return new TeamsPacket(team.uniqueId().toString(), action);
    }

    private TeamsPacket addPlayerPacket(Team team, MinestomPlayer player) {
        var action = new TeamsPacket.AddEntitiesToTeamAction(List.of(player.getUsername()));
        return new TeamsPacket(team.uniqueId().toString(), action);
    }

    private TeamsPacket deletePacket(Team team) {
        var action = new TeamsPacket.RemoveTeamAction();
        return new TeamsPacket(team.uniqueId().toString(), action);
    }

    private TeamsPacket createPacket(CommonTeam team) {
        var customColor = team.nameColor();
        var displayName = adventureSupport().convert(text(team.uniqueId().toString(), customColor));
        var nameTagVisibility = TeamsPacket.NameTagVisibility.ALWAYS;
        var collisionRule = TeamsPacket.CollisionRule.NEVER;
        var teamColor = NamedTextColor.nearestTo(adventureSupport().convert(customColor));
        var teamPrefix = adventureSupport().convert(empty());
        var teamSuffix = adventureSupport().convert(empty());
        var entities = team.users().stream().map(WBUser::playerName).toList();
        var action = new TeamsPacket.CreateTeamAction(displayName, ALLOW_FRIENDLY_FIRE, nameTagVisibility, collisionRule, teamColor, teamPrefix, teamSuffix, entities);
        return new TeamsPacket(team.uniqueId().toString(), action);
    }
}
