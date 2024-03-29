package eu.darkcube.minigame.woolbattle.common.event;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.UserAPI;

public class CommonEventHandler {
    private final CommonWoolBattle woolbattle;

    public CommonEventHandler(CommonWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    public @Nullable JoinResult playerJoined(UUID uniqueId) {
        var lobbySystemLink = woolbattle.api().lobbySystemLink();
        var connectionRequests = lobbySystemLink.connectionRequests();
        for (var entry : connectionRequests.asMap().entrySet()) {
            var request = entry.getValue();
            if (!request.player().equals(uniqueId)) continue;
            connectionRequests.invalidate(entry.getKey());

            var game = request.game();
            var rawUser = UserAPI.instance().user(uniqueId);
            var user = new CommonWBUser(woolbattle.api(), rawUser, game);
            return new JoinResult(user, game);
        }
        return null;
    }

    public record JoinResult(@NotNull CommonWBUser user, @Nullable CommonGame game) {
    }
}
