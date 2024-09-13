/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import org.bukkit.Material;

public class HeadProvider {
    public static ItemBuilder headItem(String name) {
        var item = ItemBuilder.item(Material.PLAYER_HEAD);
        item.set(ItemComponent.ITEM_NAME, Component.text(name, NamedTextColor.GOLD));
        return item;
    }
}
