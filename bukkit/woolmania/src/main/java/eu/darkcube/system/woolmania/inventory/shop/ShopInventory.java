/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.shop;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
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
import eu.darkcube.system.woolmania.enums.InventoryItems;
import org.bukkit.entity.Player;

public class ShopInventory implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;

    public ShopInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop"), 27);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        DarkCubeInventoryTemplates.Paged.configure3x9(inventoryTemplate);

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        inventoryTemplate.setItem(1, 11, INVENTORY_SHOP_FOOD);
        inventoryTemplate.setItem(1, 13, INVENTORY_SHOP_GADGETS);
        inventoryTemplate.setItem(1, 15, INVENTORY_SHOP_SOUNDS);

        inventoryTemplate.addListener(this);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String itemId = InventoryItems.getItemID(item);
        if (itemId == null) return;

        if (itemId.equals(INVENTORY_SHOP_SOUNDS.itemID())) {
            WoolMania.getInstance().getShopInventorySound().openInventory(user);
        } else if (itemId.equals(INVENTORY_SHOP_FOOD.itemID())) {
            WoolMania.getInstance().getShopInventoryFood().openInventory(user);
        } else if (itemId.equals(INVENTORY_SHOP_GADGETS.itemID())) {
            WoolMania.getInstance().getShopInventoryGadgets().openInventory(user);
        }
    }
}
