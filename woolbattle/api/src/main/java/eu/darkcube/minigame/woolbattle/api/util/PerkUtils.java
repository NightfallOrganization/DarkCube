/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import java.util.function.Consumer;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;

public final class PerkUtils {
    private static final Implementation IMPLEMENTATION = WoolBattleProvider.PROVIDER.service(Implementation.class);

    private PerkUtils() {
    }

    public static void playSoundNotEnoughWool(WBUser user) {
        IMPLEMENTATION.playSoundNotEnoughWool(user);
    }

    public static void payForPerk(UserPerk perk) {
        payForPerk(perk.owner(), perk.perk());
    }

    public static void payForPerk(WBUser user, Perk perk) {
        user.removeWool(perk.cost());
    }

    public static boolean checkUsable(WBUser user, ItemBuilder usedItem, Perk perk, Consumer<UserPerk> itemMatchRunnable, Game game) {
        var keyPerkId = PerkItem.perkId(game);
        if (!usedItem.persistentDataStorage().has(keyPerkId)) {
            return false;
        }
        var perkId = usedItem.persistentDataStorage().get(keyPerkId, PersistentDataTypes.INTEGER);
        if (perkId == null) {
            return false;
        }
        var userPerk = user.perks().perk(perkId);
        if (!userPerk.perk().equals(perk)) {
            return false;
        }
        if (itemMatchRunnable != null) {
            itemMatchRunnable.accept(userPerk);
        }
        return checkUsable(userPerk);
    }

    public static boolean checkUsable(UserPerk perk) {
        var user = perk.owner();
        return user.woolCount() >= perk.perk().cost() && perk.cooldown() == 0;
    }

    public interface Implementation {
        void playSoundNotEnoughWool(WBUser user);
    }
}
