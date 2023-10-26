/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.skylandclasssystem;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Player;

public class SkylandPlayerModifier implements UserModifier {
    private static final Key SKYLAND_PLAYER = new Key(Skyland.getInstance(), "user");

    public static SkylandPlayer getSkylandPlayer(User user) {
        return user.metadata().get(SKYLAND_PLAYER);
    }

    public static SkylandPlayer getSkylandPlayer(Player player) {
        return getSkylandPlayer(UserAPI.instance().user(player.getUniqueId()));
    }

    @Override public void onLoad(User user) {
        System.out.println(user.name() + " was looded with modifier!");
        user.metadata().set(SKYLAND_PLAYER, new SkylandPlayer(user));
    }

    @Override public void onUnload(User user) {
        user.metadata().remove(SKYLAND_PLAYER);
    }

}
