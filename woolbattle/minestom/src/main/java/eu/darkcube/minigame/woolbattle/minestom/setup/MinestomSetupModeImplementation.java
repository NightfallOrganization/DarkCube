/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.setup;

import java.util.function.BiConsumer;

import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;

public class MinestomSetupModeImplementation implements SetupMode.Implementation {
    private final @NotNull MinestomWoolBattleApi woolbattle;
    private final @NotNull Key worldKey;
    private final @NotNull Key configurationKey;

    public MinestomSetupModeImplementation(@NotNull MinestomWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        this.worldKey = Key.key(woolbattle, "minestom_setup_join_world");
        this.configurationKey = Key.key(woolbattle, "minestom_setup_configuration_join");
    }

    public @NotNull Key worldKey() {
        return worldKey;
    }

    public @NotNull Key configurationKey() {
        return configurationKey;
    }

    @Override
    public void joinWorld(@NotNull CommonWBUser user, @NotNull CommonWorld world) {
        user.metadata().set(worldKey, world);
    }

    @Override
    public void enter(@NotNull CommonWBUser oldUser) {
        var game = oldUser.game();
        var player = woolbattle.woolbattle().player(oldUser);
        oldUser.metadata().set(configurationKey, true);
        oldUser.clearTeam();
        player.startConfigurationPhase();
        if (game != null) {
            game.quietRemove(oldUser);
        }
        if (game != null) {
            game.playerQuit(oldUser);
        }
    }

    public boolean configurationEvent(@NotNull MinestomPlayer player, AsyncPlayerConfigurationEvent event) {
        var oldUser = player.user();
        if (oldUser == null) return false;
        var value = oldUser.metadata().remove(configurationKey);
        if (value == null) return false;

        var rawUser = oldUser.user();
        var user = woolbattle.woolbattle().userFactory().create(rawUser.uniqueId(), null);
        woolbattle.woolbattle().player(user, player);
        enterSetupMode(user, (instance, pos) -> {
            event.getPlayer().setRespawnPoint(pos);
            event.setSpawningInstance(instance);
        });

        return true;
    }

    public void playerJoined(CommonWBUser user) {
        var player = woolbattle.woolbattle().player(user);
        player.setGameMode(GameMode.CREATIVE);
    }

    @Override
    public void leave(@NotNull CommonWBUser user) {
        var woolbattle = this.woolbattle.woolbattle();
        var player = woolbattle.player(user);
        player.kick("Left SetupMode");
    }

    public void enterSetupMode(@NotNull CommonWBUser user, @NotNull BiConsumer<Instance, Pos> teleportConsumer) {
        var woolbattle = this.woolbattle.woolbattle();
        woolbattle.setupMode().playerJoined(user);
        var world = user.metadata().<MinestomWorld>remove(worldKey());
        var spawnPoint = woolbattle.setupMode().spawnPoint();
        var pos = new Pos(spawnPoint.x(), spawnPoint.y(), spawnPoint.z(), spawnPoint.yaw(), spawnPoint.pitch());
        if (world != null) {
            var instance = world.instance();
            teleportConsumer.accept(instance, pos);
        } else {
            var player = woolbattle.player(user);
            player.kick("Failed to enter SetupMode");
        }
    }
}
