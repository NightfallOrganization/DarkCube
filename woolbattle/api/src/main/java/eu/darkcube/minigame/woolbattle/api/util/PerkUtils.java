/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;

public final class PerkUtils {
    private static final Implementation IMPLEMENTATION = WoolBattleProvider.PROVIDER.service(Implementation.class);

    private PerkUtils() {
    }

    public static void playSoundNotEnoughWool(@NotNull WBUser user) {
        IMPLEMENTATION.playSoundNotEnoughWool(user);
    }

    public static void payForPerk(@NotNull UserPerk perk) {
        payForPerk(perk.owner(), perk.perk());
    }

    public static void payForPerk(@NotNull WBUser user, @NotNull Perk perk) {
        user.removeWool(perk.cost());
    }

    public static @NotNull CheckResult checkUsable(@NotNull WBUser user, @NotNull ItemBuilder usedItem, @NotNull Perk perk, Game game) {
        var keyPerkId = PerkItem.perkId(game);
        if (!usedItem.persistentDataStorage().has(keyPerkId)) {
            return CheckResult.WRONG_ITEM;
        }
        var perkId = usedItem.persistentDataStorage().get(keyPerkId, PersistentDataTypes.INTEGER);
        if (perkId == null) {
            return CheckResult.WRONG_ITEM;
        }
        var userPerk = user.perks().perk(perkId);
        if (!userPerk.perk().equals(perk)) {
            return CheckResult.WRONG_ITEM;
        }
        return new CheckResult(checkUsable(userPerk), userPerk);
    }

    public record CheckResult(@NotNull Usability usability, @Nullable UserPerk userPerk) {
        public static final CheckResult WRONG_ITEM = new CheckResult(Usability.WRONG_ITEM, null);
    }

    public static @NotNull Usability checkUsable(UserPerk perk) {
        var user = perk.owner();
        if (user.woolCount() < perk.perk().cost()) {
            return Usability.NOT_ENOUGH_WOOL;
        }
        if (perk.cooldown() != 0) {
            return Usability.ON_COOLDOWN;
        }
        return Usability.USABLE;
    }

    public enum Usability {
        USABLE(true),
        WRONG_ITEM(false),
        ON_COOLDOWN(false),
        NOT_ENOUGH_WOOL(false);
        private final boolean state;

        Usability(boolean state) {
            this.state = state;
        }

        public boolean booleanValue() {
            return state;
        }
    }

    public interface Implementation {
        void playSoundNotEnoughWool(WBUser user);
    }
}
