/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.message.CustomItemNames;
import org.bukkit.Material;

public class WoolItem extends CustomItem {

    public static final String ITEM_ID = "wool";

    public WoolItem(User user, Material material, Tiers tier, CustomItemNames name) {
        super(ItemBuilder.item(material));

        setDisplayName(name.getMessage(user));
        setAmount(1);
        setTier(tier);
        setItemID(ITEM_ID);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
