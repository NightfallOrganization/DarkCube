/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.registry.WoolRegistry;

public class WoolItem extends CustomItem {

    public static final String ITEM_ID = "wool";

    public WoolItem(User user, WoolRegistry.Entry entry) {
        super(entry.handler().createItem());

        setDisplayName(entry.name().getMessage(user));
        setAmount(1);
        setTier(entry.tier());
        setItemID(ITEM_ID);
        setDurability(-2);
        setUnbreakableHidden();
        updateItemLore();
    }

}
