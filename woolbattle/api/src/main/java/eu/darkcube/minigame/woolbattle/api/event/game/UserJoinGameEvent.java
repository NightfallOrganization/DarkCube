package eu.darkcube.minigame.woolbattle.api.event.game;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class UserJoinGameEvent extends GameEvent.User.Event {
    private @NotNull Result result;
    private @Nullable Location spawnLocation;

    public UserJoinGameEvent(@NotNull WBUser user, @NotNull Game game, @NotNull Result result, @Nullable Location spawnLocation) {
        super(user, game);
        this.result = result;
        this.spawnLocation = spawnLocation;
    }

    public @NotNull Result result() {
        return result;
    }

    public void result(@NotNull Result result) {
        this.result = result;
    }

    public @Nullable Location spawnLocation() {
        return spawnLocation;
    }

    public void spawnLocation(@Nullable Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public enum Result {
        USER_PLAYING,
        USER_SPECTATING,
        CANNOT_JOIN
    }
}
