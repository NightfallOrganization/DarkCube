/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.ITEM_SHEARS;

import java.util.List;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import org.bukkit.Material;

public class ShearItem extends CustomItem {

    public static final String ITEM_ID = "shear";

    public ShearItem(User user) {
        super(ItemBuilder.item(Material.SHEARS).set(ItemComponent.TOOL, new Tool(List.of(new Tool.Rule(new BlockTypeFilter.Tag("wool"), 3f, true)), 0, 1)));

        setDisplayName(ITEM_SHEARS.getMessage(user));
        setAmount(1);
        setTier(Tiers.TIER1);
        setItemID(ITEM_ID);
        setLevel(1);
        setWholeDurability(100);
        setUnbreakableHidden();
        updateItemLore();
    }
}
