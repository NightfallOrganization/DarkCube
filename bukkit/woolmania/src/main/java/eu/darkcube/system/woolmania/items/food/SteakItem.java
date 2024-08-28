/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items.food;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.FOOD_STEAK;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.items.CustomItem;
import org.bukkit.Material;

public class SteakItem extends CustomItem {

    public SteakItem(User user) {
        super(ItemBuilder.item(Material.COOKED_BEEF));

        setDisplayName(FOOD_STEAK.getMessage(user));
        setItemID("Steak");
        setFood(true);
        setAmount(64);
        setTier(Tiers.TIER10);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
