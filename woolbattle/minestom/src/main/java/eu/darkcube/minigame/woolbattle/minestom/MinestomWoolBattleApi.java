/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.command.MinestomWoolBattleCommands;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntityImplementations;
import eu.darkcube.minigame.woolbattle.minestom.game.MinestomGamePhaseCreator;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomMaterialProvider;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomColoredWoolProvider;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorldHandler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import net.minestom.server.MinecraftServer;

public class MinestomWoolBattleApi extends CommonWoolBattleApi {
    private final MinestomWoolBattle woolbattle;
    private final MinestomColoredWoolProvider woolProvider = new MinestomColoredWoolProvider();
    private final MinestomMaterialProvider materialProvider = new MinestomMaterialProvider(woolProvider);
    private final MinestomEntityImplementations entityImplementations = new MinestomEntityImplementations();
    private final MinestomWorldHandler worldHandler = new MinestomWorldHandler(this, MinecraftServer.getInstanceManager(), MinecraftServer.getBiomeManager());
    private final MinestomGamePhaseCreator gamePhaseCreator;
    private final MinestomWoolBattleCommands commands = new MinestomWoolBattleCommands(this, MinecraftServer.getCommandManager());
    private final CompletableFuture<Void> fullyLoadedFuture = new CompletableFuture<>(); // TODO complete this

    public MinestomWoolBattleApi(MinestomWoolBattle woolbattle) {
        super("minestom");
        this.woolbattle = woolbattle;
        this.gamePhaseCreator = new MinestomGamePhaseCreator(woolbattle);
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

    @Override
    public @NotNull MinestomWoolBattleCommands commands() {
        return commands;
    }

    @Override
    public @NotNull MinestomColoredWoolProvider woolProvider() {
        return woolProvider;
    }

    @Override
    public @Nullable CommonWBUser user(User user) {
        var player = (MinestomPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(user.uniqueId());
        if (player == null) return null;
        return player.user();
    }

    @Override
    public @NotNull MinestomWoolBattle woolbattle() {
        return woolbattle;
    }
}
