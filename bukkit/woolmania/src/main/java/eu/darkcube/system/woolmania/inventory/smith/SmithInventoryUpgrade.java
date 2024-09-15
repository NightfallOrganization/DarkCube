/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.smith;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
import static eu.darkcube.system.woolmania.enums.Names.VARKAS;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryUpgrade implements TemplateInventoryListener {
    private final InventoryTemplate inventoryTemplate;

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public SmithInventoryUpgrade() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "smith_upgrade"), 27);
        inventoryTemplate.title(VARKAS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        DarkCubeInventoryTemplates.Paged.configure3x9(inventoryTemplate, INVENTORY_SMITH_UPGRADE);

        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_3);
        inventoryTemplate.setItem(1, 11, INVENTORY_SMITH_UPGRADE_SHARPNESS);
        inventoryTemplate.setItem(1, 13, INVENTORY_SMITH_UPGRADE_DURABILITY);
        inventoryTemplate.setItem(1, 15, INVENTORY_SMITH_UPGRADE_TIER);

        inventoryTemplate.addListener(this);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
        String itemId = InventoryItems.getItemID(item);
        Player player = Bukkit.getPlayer(user.uniqueId());

        if (itemId == null) return;

        if (itemId.equals(INVENTORY_SMITH_UPGRADE_SHARPNESS.itemID())) {
            NO.playSound(player);
        } else if (itemId.equals(INVENTORY_SMITH_UPGRADE_DURABILITY.itemID())) {
            WoolMania.getInstance().getSmithInventoryUpgradeDurability().openInventory(player);
        } else if (itemId.equals(INVENTORY_SMITH_UPGRADE_TIER.itemID())) {
            WoolMania.getInstance().getSmithInventoryUpgradeTier().openInventory(player);
        }
    }
}
