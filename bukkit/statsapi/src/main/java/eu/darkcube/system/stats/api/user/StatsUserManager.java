/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.user;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsUserManager {

    public static List<User> getOnlineUsers() {
        return new ArrayList<>(InjectionLayer
                .boot()
                .instance(PlayerManager.class)
                .onlinePlayers()
                .players()
                .stream()
                .map(s -> new User(s.name(), s.uniqueId()))
                .toList());
    }

    public static User getOfflineUser(UUID uuid) {
        CloudOfflinePlayer op = InjectionLayer.boot().instance(PlayerManager.class).offlinePlayer(uuid);
        return new User(op.name(), op.uniqueId());
    }

    public static List<User> getOfflineUser(String name) {
        return new ArrayList<>(InjectionLayer
                .boot()
                .instance(PlayerManager.class)
                .offlinePlayers(name)
                .stream()
                .map(s -> new User(s.name(), s.uniqueId()))
                .toList());
    }

}
