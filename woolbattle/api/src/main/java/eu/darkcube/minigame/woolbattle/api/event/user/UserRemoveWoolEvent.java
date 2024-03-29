package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserRemoveWoolEvent extends UserEvent.Cancellable {
    private int amount;

    public UserRemoveWoolEvent(@NotNull WBUser user, int amount) {
        super(user);
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }

    public void amount(int amount) {
        this.amount = amount;
    }
}
