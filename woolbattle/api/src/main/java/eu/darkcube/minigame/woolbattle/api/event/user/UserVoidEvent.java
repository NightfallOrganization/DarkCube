/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserVoidEvent extends UserEvent.Cancellable {
    private boolean countAsDeath;

    public UserVoidEvent(@NotNull WBUser user, boolean countAsDeath) {
        super(user);
        this.countAsDeath = countAsDeath;
    }

    public boolean countAsDeath() {
        return countAsDeath;
    }

    public void countAsDeath(boolean countAsDeath) {
        this.countAsDeath = countAsDeath;
    }
}
