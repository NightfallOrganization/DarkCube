package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserGetWoolBreakAmountEvent extends UserEvent.Event {
    private final int initialWoolBreakAmount;
    private int woolBreakAmount;

    public UserGetWoolBreakAmountEvent(@NotNull WBUser user, int woolBreakAmount) {
        super(user);
        this.initialWoolBreakAmount = woolBreakAmount;
        this.woolBreakAmount = woolBreakAmount;
    }

    public int initialWoolBreakAmount() {
        return initialWoolBreakAmount;
    }

    public int woolBreakAmount() {
        return woolBreakAmount;
    }

    public void woolBreakAmount(int woolBreakAmount) {
        this.woolBreakAmount = woolBreakAmount;
    }
}
