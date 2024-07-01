/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import java.util.logging.Logger;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.perks.other.DoubleJumpPerk;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class WBUserModifier implements UserModifier {

    static Key USER;
    private final Logger logger = Logger.getLogger("WBUserModifier");
    private final Key DATA_VERSION;
    private final WoolBattleBukkit woolbattle;

    public WBUserModifier(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.DATA_VERSION = Key.key(woolbattle, "data_version");
        if (USER == null) USER = Key.key(woolbattle, "user");
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onLoad(User user) {
        user.persistentData().clearCache();
        var u = new DefaultWBUser(woolbattle, user);
        var oldDataVersion = 0;
        var dataVersion = 0;
        if (user.persistentData().has(DATA_VERSION)) {
            oldDataVersion = dataVersion = user.persistentData().get(DATA_VERSION, PersistentDataTypes.INTEGER);
        }
        if (dataVersion == 0) {
            // Removed: We would load mysql here, but we don't use it anymore
            dataVersion = 1;
        }
        if (dataVersion == 1) {
            migrateFrom1To2(u);
            dataVersion = 2;
        }
        if (oldDataVersion != dataVersion) {
            user.persistentData().set(DATA_VERSION, PersistentDataTypes.INTEGER, dataVersion);
        }
        user.metadata().set(USER, u);
        u.perks().reloadFromStorage();
    }

    @Override
    public void onUnload(User user) {
        user.metadata().remove(USER);
        user.persistentData().clearCache();
    }

    private void migrateFrom1To2(DefaultWBUser user) {
        logger.info("[WoolBattle] Migrating user " + user.user().name() + " from version 1 to 2");
        var perks = user.perksStorage();
        perks.perk(ActivationType.DOUBLE_JUMP, 0, DoubleJumpPerk.DOUBLE_JUMP);
        perks.perkInvSlot(ActivationType.DOUBLE_JUMP, 0, -1);
        user.perksStorage(perks);
    }
}
