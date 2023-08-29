/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserManager {

    private static UserManager instance;
    private final Key userKey = new Key("PServerPlugin", "pserver_user");
    private final UserModifier userModifier = new UserModifier() {
        @Override public void onLoad(eu.darkcube.system.userapi.User user) {
            user.metadata().set(userKey, new PServerUser(user));
        }

        @Override public void onUnload(eu.darkcube.system.userapi.User user) {
            user.metadata().remove(userKey);
        }
    };

    private UserManager() {
        UserAPI.instance().addModifier(userModifier);
    }

    public static void register() {
        instance = new UserManager();
    }

    public static void unregister() {
        instance.close();
        instance = null;
    }

    public static UserManager getInstance() {
        return instance;
    }

    public User getUser(Player p) {
        return getUser(p.getUniqueId());
    }

    public User getUser(UUID uuid) {
        return UserAPI.instance().user(uuid).metadata().get(userKey);
    }

    private void close() {
        UserAPI.instance().removeModifier(userModifier);
    }
}
