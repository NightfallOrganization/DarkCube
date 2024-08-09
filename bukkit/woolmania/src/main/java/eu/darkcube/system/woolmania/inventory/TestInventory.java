/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.PreparedInventory;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PageButton;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestInventory {

    static {
        Player player = null;
        User user = null;
        PreparedInventory preparedInventory = Inventory.prepare(InventoryType.of(org.bukkit.event.inventory.InventoryType.ANVIL), Component.text("Titel"));
        preparedInventory.setItem(1, ItemBuilder.item(Material.STONE));
        preparedInventory.open(player);
        Inventory inventory = preparedInventory.open(user);
        inventory.addListener(new InventoryListener() {
            @Override
            public void onClick(@NotNull Inventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {

            }
        });






        ItemBuilder gray = ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE);
        ItemBuilder black = ItemBuilder.item(Material.BLACK_STAINED_GLASS_PANE);

        ItemTemplate background = ItemTemplate.create();
        background.setItem(black, 2, 3, 4, 5, 6);


        InventoryTemplate inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "test"), 45);
        inventoryTemplate.title("§6Test");
        inventoryTemplate.title(Message.ZENUM_NOT_ENOUGH);
        inventoryTemplate.setItems(-1, background);

        inventoryTemplate.setItem(0, 4, gray);
        inventoryTemplate.setItem(1, 4, black);

        PageButton previousButton = inventoryTemplate.pagination().previousButton();
        // previousButton.setItem(user2 -> {
        //     Player player2 = Bukkit.getPlayer(user2.uniqueId());
        //     if(player2.getGameMode()== GameMode.CREATIVE) {
        //         return black;
        //     }
        //     return gray;
        // });
        // previousButton.setItem(Items.PREV_BUTTON);
        previousButton.setItem(gray);
        inventoryTemplate.pagination().pageSlots(
                10, 11, 12, 13, 14, 15, 16
        );

        PagedInventoryContent content = inventoryTemplate.pagination().content();
        for(int i = 0; i < 100; i++) {
            ItemBuilder item = ItemBuilder.item(Material.GRASS_BLOCK).displayname(Component.text("Item Nr. " + i));
            content.addStaticItem(item);
        }



        inventoryTemplate.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                inventory.pagedController().publishUpdateAll();
            }
        });

        inventoryTemplate.open(player);
    }

}
