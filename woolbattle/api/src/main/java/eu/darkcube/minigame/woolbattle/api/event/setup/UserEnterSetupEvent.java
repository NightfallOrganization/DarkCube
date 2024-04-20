package eu.darkcube.minigame.woolbattle.api.event.setup;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserEnterSetupEvent extends UserEvent.Event {
    public UserEnterSetupEvent(@NotNull WBUser user) {
        super(user);
    }
}
