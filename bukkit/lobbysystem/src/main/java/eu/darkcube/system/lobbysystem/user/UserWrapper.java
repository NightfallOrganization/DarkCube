/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.user;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.Key;

import java.util.Collection;
import java.util.UUID;

public class UserWrapper implements UserModifier {
    public static final Key key = new Key(Lobby.getInstance(), "user");

    public static LobbyUser fromUser(User user) {
        return user.metadata().get(key);
    }

    public void beginMigration() {
        DatabaseProvider databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        if (databaseProvider.containsDatabase("lobbysystem_userdata")) {
            Lobby.getInstance().getLogger().info("Starting migration of lobbysystem_userdata");
            Database db = databaseProvider.database("lobbysystem_userdata");
            Collection<String> keys = db.keys();
            for (String key : keys) {
                long time1 = System.currentTimeMillis();
                UUID uuid = UUID.fromString(key);
                User user = UserAPI.instance().user(uuid);
                Lobby.getInstance().getLogger().info("Migrating lobbydata: " + user.name() + "(" + uuid + ")");
                long time2 = System.currentTimeMillis();
                UserData data = new UserData(uuid, db.get(key));
                LobbyUser u = fromUser(user);
                u.setGadget(data.getGadget());
                u.setSounds(data.isSounds());
                u.setAnimations(data.isAnimations());
                u.setLastDailyReward(data.getLastDailyReward());
                u.setRewardSlotsUsed(data.getRewardSlotsUsed());
                long time3 = System.currentTimeMillis();
                if (System.currentTimeMillis() - time1 > 100) {
                    Lobby
                            .getInstance()
                            .getLogger()
                            .info("Migration of lobbydata took very long: " + (System.currentTimeMillis() - time1) + " | " + (System.currentTimeMillis() - time2) + " | " + (System.currentTimeMillis() - time3));
                }
            }
            databaseProvider.deleteDatabase("lobbysystem_userdata");
        }
    }

    @Override public void onLoad(User user) {
        user.metadata().set(key, new LobbyUser(user));
    }

    @Override public void onUnload(User user) {
        user.metadata().remove(key);
    }
}
