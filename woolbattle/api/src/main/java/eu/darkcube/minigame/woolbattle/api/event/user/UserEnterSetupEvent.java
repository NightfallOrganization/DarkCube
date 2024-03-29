package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserEnterSetupEvent extends UserEvent.Event {
    public UserEnterSetupEvent(@NotNull WBUser user) {
        super(user);
    }
}
