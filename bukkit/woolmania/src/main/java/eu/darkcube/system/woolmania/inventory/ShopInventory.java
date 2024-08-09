/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopInventory {
    public void openInventory(Player player) {
        InventoryTemplate inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop"), 27);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.setItem(1, 11, ItemBuilder.item(Material.COOKED_BEEF).displayname(text("§aEssen")));
        inventoryTemplate.setItem(1, 13, ItemBuilder.item(Material.SHEARS).displayname(text("§6Scheren")));
        inventoryTemplate.setItem(1, 15, ItemBuilder.item(Material.MUSIC_DISC_PIGSTEP).displayname(text("§cSounds")));
        inventoryTemplate.open(player);
    }

}
