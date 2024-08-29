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
import static eu.darkcube.system.woolmania.util.message.Message.NO_MONEY;

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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryRepair implements TemplateInventoryListener {

    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public SmithInventoryRepair() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "smith_repair"), 36);
        inventoryTemplate.title(VARKAS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_4);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SMITH_REPAIR);

        inventoryTemplate.setItem(1, 19, INVENTORY_SMITH_REPAIR_25_NONE);
        inventoryTemplate.setItem(1, 20, INVENTORY_SMITH_REPAIR_50_NONE);
        inventoryTemplate.setItem(1, 22, INVENTORY_SMITH_REPAIR_AIR);
        inventoryTemplate.setItem(1, 24, INVENTORY_SMITH_REPAIR_75_NONE);
        inventoryTemplate.setItem(1, 25, INVENTORY_SMITH_REPAIR_100_NONE);

        inventoryTemplate.addListener(this);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedInventoryItem = InventoryItems.getItemID(item);
        if (clickedInventoryItem == null) return;
        Player player = Bukkit.getPlayer(user.uniqueId());

        // buyItem(clickedInventoryItem, INVENTORY_SMITH_REPAIR_25_NONE, user, player);
        // buyItem(clickedInventoryItem, INVENTORY_SMITH_REPAIR_50_NONE, user, player);
        // buyItem(clickedInventoryItem, INVENTORY_SMITH_REPAIR_75_NONE, user, player);
        // buyItem(clickedInventoryItem, INVENTORY_SMITH_REPAIR_100_NONE, user, player);

        inventory.pagedController().publishUpdatePage();
    }

    private void buyItem(String clickedItem, InventoryItems inventoryItems, User user, Player player) {
        if (clickedItem.equals(inventoryItems.itemID())) {
            int cost = inventoryItems.getCost();
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            // user.sendMessage(ITEM_BUYED, inventoryItems.getItem(user, customItem.getAmount(), "").displayname(), cost);
            // WoolMania.getStaticPlayer(player).removeMoney(cost, player);
            // BUY.playSound(player);
            // player.getInventory().addItem(customItem.getItemStack());
        }
    }

}
