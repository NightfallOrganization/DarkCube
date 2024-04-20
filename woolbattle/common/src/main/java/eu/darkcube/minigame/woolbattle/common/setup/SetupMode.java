package eu.darkcube.minigame.woolbattle.common.setup;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.darkcube.minigame.woolbattle.api.event.setup.UserEnterSetupEvent;
import eu.darkcube.minigame.woolbattle.api.event.setup.UserLeaveSetupModeEvent;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.util.data.Key;

public class SetupMode {
    private final @NotNull CommonWoolBattle woolbattle;
    private final @NotNull List<@NotNull CommonWBUser> users = new CopyOnWriteArrayList<>();
    private @Nullable Position.Directed spawnPoint;
    private @Nullable CommonWorld world;

    public SetupMode(@NotNull CommonWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    private @NotNull Implementation implementation() {
        return woolbattle.setupModeImplementation();
    }

    public synchronized void playerJoined(@NotNull CommonWBUser user) {
        woolbattle.logger().info("Player " + user.playerName() + " entered SetupMode");
        woolbattle.api().eventManager().call(new UserEnterSetupEvent(user));
        var world = getOrCreateWorld();
        var implementation = implementation();
        implementation.joinWorld(user, world);
        users.add(user);
    }

    public synchronized void playerQuit(@NotNull CommonWBUser user) {
        woolbattle.logger().info("Player " + user.playerName() + " left SetupMode");
        woolbattle.api().eventManager().call(new UserLeaveSetupModeEvent(user));
        users.remove(user);
        if (users.isEmpty()) {
            woolbattle.api().worldHandler().unloadWorld(world);
            world = null;
        }
    }

    public void enter(@NotNull CommonWBUser user) {
        implementation().enter(user);
    }

    public void leave(@NotNull CommonWBUser user) {
        implementation().leave(user);
    }

    public @Unmodifiable @NotNull Collection<@NotNull CommonWBUser> users() {
        return List.copyOf(users);
    }

    private synchronized @NotNull CommonWorld getOrCreateWorld() {
        if (world != null) return world;
        var worldHandler = woolbattle.api().worldHandler();
        world = worldHandler.loadSetupWorld();
        return world;
    }

    public synchronized @NotNull Position.Directed spawnPoint() {
        if (spawnPoint == null) {
            spawnPoint = woolbattle.api().persistentDataStorage().get(new Key(woolbattle.api(), "setup_respawn_point"), Position.Directed.TYPE, () -> new Position.Directed.Simple(0.5, 100, 0.5, 0, 0));
        }
        return spawnPoint;
    }

    public synchronized void spawnPoint(@NotNull Position.Directed spawnPoint) {
        this.spawnPoint = spawnPoint;
        woolbattle.api().persistentDataStorage().set(new Key(woolbattle.api(), "setup_respawn_point"), Position.Directed.TYPE, spawnPoint.clone());
    }

    public interface Implementation {
        void joinWorld(@NotNull CommonWBUser user, @NotNull CommonWorld world);

        void enter(@NotNull CommonWBUser user);

        void leave(@NotNull CommonWBUser user);
    }
}
