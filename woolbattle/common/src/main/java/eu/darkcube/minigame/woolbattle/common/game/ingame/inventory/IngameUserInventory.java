/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.inventory;

import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.user.UserPlatformAccess;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public class IngameUserInventory {

    private static final Function<WoolBattleApi, Key> KEY = woolbattle -> Key.key(woolbattle, "ingame_user_inventory");

    private final CommonWBUser user;
    private final UserPlatformAccess access;

    public IngameUserInventory(CommonWBUser user) {
        this.user = user;
        this.access = user.platformAccess();
    }

    public static IngameUserInventory get(WBUser user) {
        return user.metadata().get(KEY.apply(user.api()));
    }

    public static IngameUserInventory create(CommonWBUser user) {
        var inventory = new IngameUserInventory(user);
        user.metadata().set(KEY.apply(user.api()), inventory);
        return inventory;
    }

    public static void destroy(WBUser user) {
        user.metadata().remove(KEY.apply(user.api()));
    }

    public void setAllItems() {
        var team = user.team();
        if (team == null) return;
        if (!team.canPlay()) {
            setSpectatorItems();
        } else {
            setPlayerItems();
        }
    }

    private void setPlayerItems() {
        for (var perk : user.perks().perks()) {
            perk.currentPerkItem().setItem();
        }
    }

    private void setSpectatorItems() {

    }
}
