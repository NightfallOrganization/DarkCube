/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items.gadgets;

import static eu.darkcube.system.woolmania.util.message.CustomItemNames.GADGET_WOOLGRENADE;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.items.CustomItem;
import org.bukkit.Material;

public class WoolGrenadeItem extends CustomItem {

    public static final String ITEM_ID = "woolgrenade";

    public WoolGrenadeItem(User user) {
        super(ItemBuilder.item(Material.SNOWBALL));

        setDisplayName(GADGET_WOOLGRENADE.getMessage(user));
        setItemID(ITEM_ID);
        setAmount(15);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
