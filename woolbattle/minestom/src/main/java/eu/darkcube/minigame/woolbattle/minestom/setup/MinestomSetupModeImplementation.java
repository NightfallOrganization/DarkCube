package eu.darkcube.minigame.woolbattle.minestom.setup;

import java.util.function.BiConsumer;

import eu.darkcube.minigame.woolbattle.common.setup.SetupMode;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class MinestomSetupModeImplementation implements SetupMode.Implementation {
    private final @NotNull MinestomWoolBattleApi woolbattle;
    private final @NotNull Key worldKey;

    public MinestomSetupModeImplementation(@NotNull MinestomWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        this.worldKey = new Key(woolbattle, "join_world");
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
        if (game != null) {
            game.playerQuit(oldUser);
        }
        var rawUser = oldUser.user();
        var user = new CommonWBUser(woolbattle, rawUser, null);
        woolbattle.woolbattle().player(user, woolbattle.woolbattle().player(oldUser));
        var player = woolbattle.woolbattle().player(user);
        enterSetupMode(user, (instance, pos) -> {
            player.getAcquirable().sync(p -> {
                p.setInstance(p.getInstance(), pos).thenRun(() -> {
                    System.out.println("Set instance done");
                }).join();
            });
        });
    }

    @Override
    public void leave(@NotNull CommonWBUser user) {
        var woolbattle = this.woolbattle.woolbattle();
        woolbattle.setupMode().playerQuit(user);
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
            user.user().sendMessage(Messages.ENTERED_SETUP_MODE);
            var instance = world.instance();
            teleportConsumer.accept(instance, pos);
        } else {
            var player = woolbattle.player(user);
            player.kick("Failed to enter SetupMode");
        }
    }
}
