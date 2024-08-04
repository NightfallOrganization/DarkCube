package eu.darkcube.minigame.woolbattle.minestom.game.ingame;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.entity.GameMode;

public class MinestomIngame extends CommonIngame {
    private final @NotNull MinestomWoolBattle woolbattle;

    public MinestomIngame(@NotNull CommonGame game, @NotNull MinestomWoolBattle woolbattle) {
        super(game);
        this.woolbattle = woolbattle;
    }

    @Override
    public void enable(@Nullable CommonPhase oldPhase) {
        super.enable(oldPhase);
        for (var user : game.users()) {
            var player = woolbattle.player(user);
            player.acquirable().sync(p -> {
                p.setGameMode(GameMode.CREATIVE);
            });
        }
    }
}
