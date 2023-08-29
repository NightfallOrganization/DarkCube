/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.packets.PacketWNQueryUser;
import eu.darkcube.system.util.Language;

import java.util.UUID;

public abstract class CommonUser implements User, UserData.Forwarding {
    private final CommonUserData userData;

    /**
     * Creates a {@link CommonUser} with remote capabilities
     */
    public CommonUser(UUID uniqueId) {
        PacketWNQueryUser.Result result = new PacketWNQueryUser(uniqueId).sendQuery(PacketWNQueryUser.Result.class);
        CommonUserRemotePersistentDataStorage persistentData = new CommonUserRemotePersistentDataStorage(uniqueId, result.getData());
        this.userData = new CommonUserData(uniqueId, result.getName(), persistentData);
    }

    public CommonUser(CommonUserData userData) {
        this.userData = userData;
    }

    /**
     * @return the audience corresponding to this user
     */
    @Override public abstract @NotNull Audience audience();

    @Override public CommonUserData userData() {
        return userData;
    }

    @Override public @NotNull Language language() {
        return Forwarding.super.language();
    }

    @Override public void language(@NotNull Language language) {
        Forwarding.super.language(language);
    }
}
