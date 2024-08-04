package eu.darkcube.minigame.woolbattle.minestom.game.lobby;

import static eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport.adventureSupport;

import java.util.List;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.LobbySidebarTeam;
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

public class MinestomLobbyScoreboard {
    private static final byte ALLOW_FRIENDLY_FIRE = 0x01;
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull Key sidebarKey;
    private final @NotNull CommonGame game;

    public MinestomLobbyScoreboard(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonGame game) {
        this.woolbattle = woolbattle;
        this.sidebarKey = Key.key(woolbattle, "minestom_sidebar_lobby");
        this.game = game;
    }

    public void setupPlayer(@NotNull MinestomPlayer player, @NotNull WBUser wbUser) {
        // First setup the player's scoreboard
        for (var team : this.game.teamManager().teams()) {
            setupTeam(player, team);
        }
        // Then add the player to all existing player's scoreboards
        var packet = addPlayerPacket(Objects.requireNonNull(wbUser.team()), player);
        for (var user : this.game.users()) {
            if (user == wbUser) continue;
            var p = this.woolbattle.player(user);
            p.sendPacket(packet);
        }
        var sidebar = new Sidebar(adventureSupport().convert(Messages.getMessage(CommonScoreboardObjective.LOBBY.messageKey(), wbUser.language())));
        for (var team : LobbySidebarTeam.values()) {
            createLine(sidebar, (CommonWBUser) wbUser, team);
        }
        sidebar.addViewer(player);
        wbUser.metadata().set(sidebarKey, sidebar);
    }

    public void update(@NotNull CommonWBUser user, @NotNull LobbySidebarTeam team) {
        var sidebar = (Sidebar) user.metadata().get(sidebarKey);
        update(sidebar, user, team);
    }

    private void createLine(Sidebar sidebar, CommonWBUser user, LobbySidebarTeam team) {
        sidebar.createLine(new Sidebar.ScoreboardLine(team.id(), adventureSupport().convert(team.createContent(game, user)), team.ordinal(), Sidebar.NumberFormat.blank()));
    }

    private void update(Sidebar sidebar, CommonWBUser user, LobbySidebarTeam team) {
        sidebar.updateLineContent(team.id(), adventureSupport().convert(team.createContent(game, user)));
    }

    public void switchTeam(@NotNull MinestomPlayer player, @NotNull Team oldTeam, @NotNull Team newTeam) {
        var removePacket = removePlayerPacket(oldTeam, player);
        var addPacket = addPlayerPacket(newTeam, player);
        for (var user : this.game.users()) {
            var p = this.woolbattle.player(user);
            p.sendPacket(removePacket);
            p.sendPacket(addPacket);
        }
    }

    public void cleanupPlayer(@NotNull MinestomPlayer player, @NotNull Team oldTeam) {
        var removePacket = removePlayerPacket(oldTeam, player);
        for (var u : this.game.users()) {
            var p = this.woolbattle.player(u);
            p.sendPacket(removePacket);
        }
        var user = player.user();
        if (user != null) {
            var sidebar = user.metadata().<Sidebar>remove(sidebarKey);
            if (sidebar != null) {
                // Minestom doesn't send this packet
                player.sendPacket(sidebar.getDisplayScoreboardPacket((byte) 1));
                sidebar.removeViewer(player);
            }
            for (var team : this.game.teamManager().teams()) {
                player.sendPacket(deletePacket(team));
            }
        }
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

    private TeamsPacket createPacket(Team team) {
        var displayName = adventureSupport().convert(Component.text(team.uniqueId().toString(), team.nameColor()));
        var nameTagVisibility = TeamsPacket.NameTagVisibility.ALWAYS;
        var collisionRule = TeamsPacket.CollisionRule.NEVER;
        var customColor = team.nameColor();
        var teamColor = NamedTextColor.nearestTo(adventureSupport().convert(customColor));
        var teamPrefix = adventureSupport().convert(Component.empty());
        var teamSuffix = adventureSupport().convert(Component.empty());
        var entities = team.users().stream().map(WBUser::playerName).toList();
        var action = new TeamsPacket.CreateTeamAction(displayName, ALLOW_FRIENDLY_FIRE, nameTagVisibility, collisionRule, teamColor, teamPrefix, teamSuffix, entities);
        return new TeamsPacket(team.uniqueId().toString(), action);
    }
}
