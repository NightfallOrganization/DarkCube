/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserAddWoolEvent extends UserEvent.Cancellable {
    private int amount;
    private boolean dropRemaining;

    public UserAddWoolEvent(@NotNull WBUser user, int amount, boolean dropRemaining) {
        super(user);
        this.amount = amount;
        this.dropRemaining = dropRemaining;
    }

    public int amount() {
        return amount;
    }

    public void amount(int amount) {
        this.amount = amount;
    }

    public boolean dropRemaining() {
        return dropRemaining;
    }

    public void dropRemaining(boolean dropRemaining) {
        this.dropRemaining = dropRemaining;
    }
}
