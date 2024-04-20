package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.event.game.GameEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class SetupUserGameEvent extends GameEvent.User.Event {
    public SetupUserGameEvent(@NotNull WBUser user, @NotNull Game game) {
        super(user, game);
    }
}
