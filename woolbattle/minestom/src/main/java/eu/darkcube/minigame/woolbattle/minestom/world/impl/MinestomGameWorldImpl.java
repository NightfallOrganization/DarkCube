package eu.darkcube.minigame.woolbattle.minestom.world.impl;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.instance.Instance;

public class MinestomGameWorldImpl extends CommonGameWorld implements MinestomWorld {
    private final @NotNull Instance instance;

    public MinestomGameWorldImpl(@NotNull CommonGame game, @NotNull Instance instance) {
        super(game);
        this.instance = instance;
    }

    @Override
    public @NotNull Instance instance() {
        return instance;
    }
}
