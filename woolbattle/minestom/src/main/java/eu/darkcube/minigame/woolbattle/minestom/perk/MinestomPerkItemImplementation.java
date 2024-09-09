/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.perk;

import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.Slot;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import net.minestom.server.item.ItemStack;

public class MinestomPerkItemImplementation implements PerkItem.Implementation {
    private final MinestomWoolBattle woolbattle;

    public MinestomPerkItemImplementation(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public void setItem(PerkItem perkItem) {
        var builder = perkItem.calculateItem();
        var itemStack = builder == null ? ItemStack.AIR : builder.<ItemStack>build();
        var perk = perkItem.perk();
        var user = (CommonWBUser) perk.owner();
        var player = woolbattle.player(user);
        var slot = perk.slot();

        if (slot == Slot.Cursor.SLOT) {
            player.getInventory().setCursorItem(itemStack);
        } else {
            player.getInventory().setItemStack(Slot.mappings().platformSlot(slot), itemStack);
        }
    }
}
