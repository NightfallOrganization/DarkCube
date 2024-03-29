package eu.darkcube.minigame.woolbattle.api.event.game;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserJoinGameEvent extends GameEvent.User.Event {
    public UserJoinGameEvent(@NotNull WBUser user, @NotNull Game game) {
        super(user, game);
    }
}
