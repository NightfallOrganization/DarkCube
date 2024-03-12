/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntityImplementations;
import eu.darkcube.minigame.woolbattle.minestom.game.MinestomGamePhaseCreator;
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomMaterialProvider;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorldHandler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomWoolBattleApi extends CommonWoolBattleApi {
    private final WoolBattleMinestom woolbattle;
    private final MinestomMaterialProvider materialProvider = new MinestomMaterialProvider();
    private final MinestomEntityImplementations entityImplementations = new MinestomEntityImplementations();
    private final MinestomWorldHandler worldHandler = new MinestomWorldHandler();
    private final MinestomGamePhaseCreator gamePhaseCreator = new MinestomGamePhaseCreator();
    private final CompletableFuture<Void> fullyLoadedFuture = new CompletableFuture<>(); // TODO complete this

    public MinestomWoolBattleApi(WoolBattleMinestom woolbattle) {
        super("minestom");
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull CompletableFuture<Void> fullyLoadedFuture() {
        return fullyLoadedFuture;
    }

    @Override
    public @NotNull MinestomWorldHandler worldHandler() {
        return worldHandler;
    }

    @Override
    public @NotNull MinestomGamePhaseCreator gamePhaseCreator() {
        return gamePhaseCreator;
    }

    @Override
    public @NotNull MinestomEntityImplementations entityImplementations() {
        return entityImplementations;
    }

    @Override
    public @NotNull MinestomMaterialProvider materialProvider() {
        return materialProvider;
    }

    public WoolBattleMinestom woolbattle() {
        return woolbattle;
    }
}
