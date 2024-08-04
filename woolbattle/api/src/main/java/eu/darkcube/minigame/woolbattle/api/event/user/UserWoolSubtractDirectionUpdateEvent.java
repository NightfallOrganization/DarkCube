package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserWoolSubtractDirectionUpdateEvent extends UserEvent.Event {
    private final @NotNull WoolSubtractDirection newDirection;

    public UserWoolSubtractDirectionUpdateEvent(@NotNull WBUser user, @NotNull WoolSubtractDirection newDirection) {
        super(user);
        this.newDirection = newDirection;
    }

    public @NotNull WoolSubtractDirection newDirection() {
        return newDirection;
    }
}
