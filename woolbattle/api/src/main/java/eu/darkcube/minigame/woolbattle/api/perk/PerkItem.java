/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class PerkItem {
    public static final Implementation IMPLEMENTATION = WoolBattleProvider.PROVIDER.service(Implementation.class);

    private final Key perkId;
    private final Supplier<Item> itemSupplier;
    private final UserPerk perk;

    public PerkItem(Supplier<Item> itemSupplier, UserPerk perk) {
        this.itemSupplier = itemSupplier;
        this.perk = perk;
        this.perkId = perkId(perk.game());
    }

    public UserPerk perk() {
        return perk;
    }

    /**
     * Sets the item in the perk's owner's inventory
     */
    public void setItem() {
        var team = perk.owner().team();
        if (team == null) return;
        if (!team.canPlay()) return;
        IMPLEMENTATION.setItem(this);
    }

    public @Nullable ItemBuilder calculateItem() {
        var item = itemSupplier.get();
        if (item == null) return null;
        var b = item.getItem(perk.owner());
        var amt = itemAmount();
        if (amt > 0) {
            b.amount(Math.min(amt, 64));
        } else if (amt == 0) {
            b.glow(true);
        }
        b.persistentDataStorage().set(perkId, PersistentDataTypes.INTEGER, perk.id());
        modify(b);
        return b;
    }

    protected int itemAmount() {
        return perk.perk().cooldown().unit().itemCount(perk.cooldown());
    }

    protected void modify(ItemBuilder item) {
    }

    public static Key perkId(Game game) {
        return Key.key(game.api(), "perk_id");
    }

    public interface Implementation {
        void setItem(PerkItem item);
    }
}
