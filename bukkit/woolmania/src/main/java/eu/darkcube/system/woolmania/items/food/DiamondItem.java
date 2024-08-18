/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items.food;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.FOOD_DIAMOND;

import java.util.List;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.Food;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.items.CustomItem;
import org.bukkit.Material;

public class DiamondItem extends CustomItem {

    public DiamondItem(User user) {
        super(ItemBuilder.item(Material.DIAMOND).set(ItemComponent.FOOD, new Food(100, 100, false, 1.61f, null, List.of())));

        setDisplayName(FOOD_DIAMOND.getMessage(user));
        setItemID("Diamond");
        setAmount(64);
        setTier(Tiers.TIER10);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
