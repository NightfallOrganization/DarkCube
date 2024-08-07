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
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class MinestomSetupModeImplementation implements SetupMode.Implementation {
    private final @NotNull MinestomWoolBattleApi woolbattle;
    private final @NotNull Key worldKey;

    public MinestomSetupModeImplementation(@NotNull MinestomWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        this.worldKey = Key.key(woolbattle, "join_world");
    }

    public @NotNull Key worldKey() {
        return worldKey;
    }

    @Override
    public void joinWorld(@NotNull CommonWBUser user, @NotNull CommonWorld world) {
        user.metadata().set(worldKey, world);
    }

    @Override
    public void enter(@NotNull CommonWBUser oldUser) {
        var game = oldUser.game();
        var rawUser = oldUser.user();
        var user = new CommonWBUser(woolbattle, rawUser, null);
        woolbattle.woolbattle().player(user, woolbattle.woolbattle().player(oldUser));
        var player = woolbattle.woolbattle().player(user);
        enterSetupMode(user, (instance, pos) -> {
            var lock = player.getAcquirable().lock();
            player.setInstance(instance, pos).join();
            lock.unlock();
            if (game != null) {
                game.playerQuit(oldUser);
            }
        });
    }

    @Override
    public void leave(@NotNull CommonWBUser user) {
        var woolbattle = this.woolbattle.woolbattle();
        var player = woolbattle.player(user);
        player.kick("Left SetupMode");
        player.remove();
        woolbattle.setupMode().playerQuit(user);
    }

    public void enterSetupMode(@NotNull CommonWBUser user, @NotNull BiConsumer<Instance, Pos> teleportConsumer) {
        var woolbattle = this.woolbattle.woolbattle();
        woolbattle.setupMode().playerJoined(user);
        var world = user.metadata().<MinestomWorld>remove(worldKey());
        var spawnPoint = woolbattle.setupMode().spawnPoint();
        var pos = new Pos(spawnPoint.x(), spawnPoint.y(), spawnPoint.z(), spawnPoint.yaw(), spawnPoint.pitch());
        if (world != null) {
            user.user().sendMessage(Messages.ENTERED_SETUP_MODE);
            var instance = world.instance();
            teleportConsumer.accept(instance, pos);
        } else {
            var player = woolbattle.player(user);
            player.kick("Failed to enter SetupMode");
        }
    }
}
