package eu.darkcube.minigame.woolbattle.minestom.world.impl;

import java.nio.file.Path;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.instance.Instance;

public class MinestomGameWorldImpl extends CommonGameWorld implements MinestomWorld {
    private final @NotNull Instance instance;
    private final @Nullable Path worldDirectory;

    public MinestomGameWorldImpl(@NotNull CommonGame game, @NotNull Instance instance, @Nullable Path worldDirectory) {
        super(game);
        this.instance = instance;
        this.worldDirectory = worldDirectory;
    }

    @Override
    public @NotNull Instance instance() {
        return instance;
    }

    @Override
    public @Nullable Path worldDirectory() {
        return worldDirectory;
    }
}
