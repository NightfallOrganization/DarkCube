package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserGetMaxWoolSizeEvent extends UserEvent.Event {
    private final int initialMaxWoolSize;
    private int maxWoolSize;

    public UserGetMaxWoolSizeEvent(@NotNull WBUser user, int maxWoolSize) {
        super(user);
        this.initialMaxWoolSize = maxWoolSize;
        this.maxWoolSize = maxWoolSize;
    }

    public int initialMaxWoolSize() {
        return initialMaxWoolSize;
    }

    public int maxWoolSize() {
        return maxWoolSize;
    }

    public void maxWoolSize(int maxWoolSize) {
        this.maxWoolSize = maxWoolSize;
    }
}
