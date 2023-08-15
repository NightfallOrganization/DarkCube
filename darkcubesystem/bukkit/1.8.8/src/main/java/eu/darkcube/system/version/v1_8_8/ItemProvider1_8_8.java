/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_8_8;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemProvider1_8_8 implements eu.darkcube.system.inventoryapi.item.ItemProvider {
    @Override public eu.darkcube.system.inventoryapi.item.ItemBuilder item(Material material) {
        return new ItemBuilder1_8_8().material(material);
    }

    @Override public eu.darkcube.system.inventoryapi.item.ItemBuilder item(ItemStack item) {
        return new ItemBuilder1_8_8(item);
    }

    @Override public ItemBuilder item(JsonElement json) {
        return ItemBuilder1_8_8.deserialize(json);
    }

    @Override public eu.darkcube.system.inventoryapi.item.ItemBuilder spawner() {
        return item(Material.MOB_SPAWNER);
    }
}
