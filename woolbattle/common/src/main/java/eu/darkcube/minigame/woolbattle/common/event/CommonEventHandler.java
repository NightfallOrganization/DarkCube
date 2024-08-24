/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
import eu.darkcube.system.server.item.ItemBuilder;

public class CommonEventHandler {
    private final CommonWoolBattle woolbattle;

    public CommonEventHandler(CommonWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    public @Nullable CommonEventHandler.ConfigurationResult playerConfiguration(UUID uniqueId) {
        var lobbySystemLink = woolbattle.api().lobbySystemLink();
        var connectionRequests = lobbySystemLink.connectionRequests();
        for (var entry : connectionRequests.asMap().entrySet()) {
            var request = entry.getValue();
            if (!request.player().equals(uniqueId)) continue;
            connectionRequests.invalidate(entry.getKey());

            var game = request.game();
            var user = woolbattle.userFactory().create(uniqueId, game);
            return new ConfigurationResult(user, game);
        }
        return null;
    }

    public @NotNull PlaceResult blockPlace(@NotNull CommonWBUser user, @NotNull CommonBlock block, @NotNull ItemBuilder item) {
        var event = new PlaceBlockEvent(user, block, item);
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

    public record ConfigurationResult(@NotNull CommonWBUser user, @Nullable CommonGame game) {
    }
}
