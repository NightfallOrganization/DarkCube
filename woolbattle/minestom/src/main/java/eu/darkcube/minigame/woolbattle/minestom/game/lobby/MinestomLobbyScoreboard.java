package eu.darkcube.minigame.woolbattle.minestom.game.lobby;

import static eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport.adventureSupport;

import java.util.List;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.network.packet.server.play.TeamsPacket;

public class MinestomLobbyScoreboard {
    private static final byte ALLOW_FRIENDLY_FIRE = 0x01;
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull CommonGame game;

    public MinestomLobbyScoreboard(@NotNull MinestomWoolBattle woolbattle, @NotNull CommonGame game) {
        this.woolbattle = woolbattle;
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

    public void cleanupPlayer(@NotNull MinestomPlayer player, @NotNull WBUser user) {
        var team = user.team();
        if (team != null) {
            var removePacket = removePlayerPacket(team, player);
            for (var u : this.game.users()) {
                var p = this.woolbattle.player(u);
                p.sendPacket(removePacket);
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

    private TeamsPacket createPacket(Team team) {
        var displayName = adventureSupport().convert(Component.text(team.uniqueId().toString(), team.nameStyle()));
        var friendlyFlags = ALLOW_FRIENDLY_FIRE;
        var nameTagVisibility = TeamsPacket.NameTagVisibility.ALWAYS;
        var collisionRule = TeamsPacket.CollisionRule.NEVER;
        var customColor = team.nameStyle().color();
        var teamColor = NamedTextColor.nearestTo(adventureSupport().convert(customColor == null ? eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.WHITE : customColor));
        var teamPrefix = adventureSupport().convert(Component.empty());
        var teamSuffix = adventureSupport().convert(Component.empty());
        var entities = team.users().stream().map(WBUser::playerName).toList();
        var action = new TeamsPacket.CreateTeamAction(displayName, friendlyFlags, nameTagVisibility, collisionRule, teamColor, teamPrefix, teamSuffix, entities);
        return new TeamsPacket(team.uniqueId().toString(), action);
    }
}
