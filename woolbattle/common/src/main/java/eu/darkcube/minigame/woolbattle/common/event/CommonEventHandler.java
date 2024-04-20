package eu.darkcube.minigame.woolbattle.common.event;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.event.world.block.BreakBlockEvent;
import eu.darkcube.minigame.woolbattle.api.event.world.block.PlaceBlockEvent;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.world.CommonBlock;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.material.Material;
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

    public @NotNull PlaceResult blockPlace(@NotNull CommonWBUser user, @NotNull CommonBlock block, @NotNull Material material) {
        var event = new PlaceBlockEvent(user, block, material);
        woolbattle.api().eventManager().call(event);
        return new PlaceResult(event.cancelled());
    }

    public @NotNull BreakResult blockBreak(@NotNull CommonWBUser user, @NotNull CommonBlock block) {
        var event = new BreakBlockEvent(user, block);
        woolbattle.api().eventManager().call(event);
        return new BreakResult(event.cancelled());
    }

    public record BreakResult(boolean cancel) {
    }

    public record PlaceResult(boolean cancel) {
    }

    public record JoinResult(@NotNull CommonWBUser user, @Nullable CommonGame game) {
    }
}
