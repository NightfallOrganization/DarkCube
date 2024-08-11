/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.PersistentDataValue;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomItem {
    Plugin woolMania = WoolMania.getInstance();
    PersistentDataValue<Tiers> woolTier;
    PersistentDataValue<String> name;

    public CustomItem(ItemStack itemStack) {
        woolTier = new PersistentDataValue<>(new NamespacedKey(woolMania, "tier"), Tiers.class, itemStack.getItemMeta().getPersistentDataContainer(), Tiers.TIER1);
    }

    public CustomItem(ItemStack itemStack, Tiers tiers) {
        name = new PersistentDataValue<>(new NamespacedKey(woolMania, "name"), String.class, itemStack.getItemMeta().getPersistentDataContainer(), "§7CustomItem");
        woolTier = new PersistentDataValue<>(new NamespacedKey(woolMania, "tier"), Tiers.class, itemStack.getItemMeta().getPersistentDataContainer(), Tiers.TIER1);
        woolTier.set(tiers);
    }

    public void setWoolTier(PersistentDataValue<Tiers> woolTier) {
        this.woolTier = woolTier;
    }

}
