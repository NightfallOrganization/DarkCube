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
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopInventory implements TemplateInventoryListener {
    public void openInventory(Player player) {
        InventoryTemplate inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop"), 27);
        inventoryTemplate.title(ZINUS.getName());

        inventoryTemplate.animation().calculateManifold(4, 1);

        var disc = Items.INVENTORY_SHOP_SOUNDS.createItem(player).<ItemStack>build();
        disc.editMeta(meta -> {
            var jukebox = meta.getJukeboxPlayable();
            jukebox.setShowInTooltip(false);
            meta.setJukeboxPlayable(jukebox);
            // meta.setHideTooltip(true);
        });

        DarkCubeInventoryTemplates.Paged.configure3x9(inventoryTemplate);

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        inventoryTemplate.setItem(1, 11, Items.INVENTORY_SHOP_STEAK);
        inventoryTemplate.setItem(1, 13, Items.INVENTORY_SHOP_SHEARS);
        inventoryTemplate.setItem(1, 15, disc);

        inventoryTemplate.addListener(this);
        inventoryTemplate.open(player);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String itemId = Items.getItemID(item);
        if (itemId == null) return;

        if (itemId.equals(Items.INVENTORY_SHOP_SOUNDS.itemID())) {
            WoolMania.getInstance().getShopInventorySound().openInventory(user);
        }
    }
}
