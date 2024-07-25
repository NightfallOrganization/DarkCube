package eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserChangeTeamEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.MinestomLobby;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomLobbyUserChangeTeamListener extends ConfiguredListener<UserChangeTeamEvent> {
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull MinestomLobby lobby;

    public MinestomLobbyUserChangeTeamListener(@NotNull MinestomWoolBattle woolbattle, @NotNull MinestomLobby lobby) {
        super(UserChangeTeamEvent.class);
        this.woolbattle = woolbattle;
        this.lobby = lobby;
    }

    @Override
    public void accept(UserChangeTeamEvent event) {
        var user = (CommonWBUser) event.user();
        var player = this.woolbattle.player(user);
        var oldTeam = event.oldTeam();
        var newTeam = event.newTeam();
        if (oldTeam == null && newTeam != null) {
            // Player just joined
            lobby.scoreboard().setupPlayer(player, user);
        } else if (oldTeam != null && newTeam != null) {
            // Player switched team
            lobby.scoreboard().switchTeam(player, oldTeam, newTeam);
        } else if (oldTeam != null) {
            // Player quit
            lobby.scoreboard().cleanupPlayer(player, user);
        } else {
            // Can't happen
            throw new IllegalStateException("Player switched team from null to null");
        }
    }
}
