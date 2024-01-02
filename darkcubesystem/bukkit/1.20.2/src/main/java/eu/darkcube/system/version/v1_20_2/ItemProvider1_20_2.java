/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_20_2;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.ItemProvider;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemProvider1_20_2 implements ItemProvider {
    @Override public ItemBuilder item(Material material) {
        return new ItemBuilder1_20_2().material(material);
    }

    @Override public ItemBuilder item(ItemStack item) {
        return new ItemBuilder1_20_2(item);
    }

    @Override public ItemBuilder item(JsonElement json) {
        return ItemBuilder1_20_2.deserialize(json);
    }

    @Override public ItemBuilder spawner() {
        return item(Material.SPAWNER);
    }
}
