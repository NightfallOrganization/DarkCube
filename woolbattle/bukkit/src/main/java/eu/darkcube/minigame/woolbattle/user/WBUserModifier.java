/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.perks.other.DoubleJumpPerk;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserModifier;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;

import java.util.logging.Logger;

public class WBUserModifier implements UserModifier {

    static Key USER;
    private final Logger logger = Logger.getLogger("WBUserModifier");
    private final Key DATA_VERSION;
    private final WoolBattleBukkit woolbattle;

    public WBUserModifier(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.DATA_VERSION = new Key(woolbattle, "data_version");
        if (USER == null) USER = new Key(woolbattle, "user");
    }

    @SuppressWarnings("DataFlowIssue") @Override public void onLoad(User user) {
        user.getPersistentDataStorage().clearCache();
        DefaultWBUser u = new DefaultWBUser(woolbattle, user);
        int oldDataVersion = 0;
        int dataVersion = 0;
        if (user.getPersistentDataStorage().has(DATA_VERSION)) {
            oldDataVersion = dataVersion = user.getPersistentDataStorage().get(DATA_VERSION, PersistentDataTypes.INTEGER);
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
            user.getPersistentDataStorage().set(DATA_VERSION, PersistentDataTypes.INTEGER, dataVersion);
        }
        user.getMetaDataStorage().set(USER, u);
        u.perks().reloadFromStorage();
    }

    @Override public void onUnload(User user) {
        user.getMetaDataStorage().remove(USER);
        user.getPersistentDataStorage().clearCache();
    }

    private void migrateFrom1To2(DefaultWBUser user) {
        logger.info("[WoolBattle] Migrating user " + user.user().getName() + " from version 1 to 2");
        PlayerPerks perks = user.perksStorage();
        perks.perk(ActivationType.DOUBLE_JUMP, 0, DoubleJumpPerk.DOUBLE_JUMP);
        perks.perkInvSlot(ActivationType.DOUBLE_JUMP, 0, -1);
        user.perksStorage(perks);
    }
}
