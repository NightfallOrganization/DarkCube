package eu.darkcube.minigame.woolbattle.api.event.setup;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserLeaveSetupModeEvent extends UserEvent.Event {
    public UserLeaveSetupModeEvent(@NotNull WBUser user) {
        super(user);
    }
}
