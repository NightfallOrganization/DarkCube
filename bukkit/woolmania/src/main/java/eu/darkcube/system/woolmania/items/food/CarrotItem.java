/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items.food;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.FOOD_CARROT;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.items.CustomItem;
import org.bukkit.Material;

public class CarrotItem extends CustomItem {

    public CarrotItem(User user) {
        super(ItemBuilder.item(Material.CARROT));

        setDisplayName(FOOD_CARROT.getMessage(user));
        setItemID("Carrot");
        setAmount(64);
        setTier(Tiers.TIER1);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
