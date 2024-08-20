/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.shop;

import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_SHOP_GADGETS;
import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_SHOP_GADGETS_GRENADE;
import static eu.darkcube.system.woolmania.enums.Names.ZINUS;
import static eu.darkcube.system.woolmania.enums.Sounds.BUY;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryMask;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContent;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.gadgets.WoolGrenadeItem;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShopInventoryGadgets implements TemplateInventoryListener {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;
    private final InventoryTemplate inventoryTemplate;
    private final Integer stack = 64;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public ShopInventoryGadgets() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop_gadgets"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SHOP_GADGETS);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_GADGETS_GRENADE, 15));

        inventoryTemplate.addListener(this);
    }

    private Function<User, Object> getDisplayItem(InventoryItems item, int amount) {
        return user -> {
            return item.getItem(user, amount, ITEM_BUY_COST.getMessage(user, item.getCost()));
        };
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedInventoryItem = InventoryItems.getItemID(item);
        if (clickedInventoryItem == null) return;
        Player player = Bukkit.getPlayer(user.uniqueId());

        buyItems(clickedInventoryItem, new WoolGrenadeItem(user), INVENTORY_SHOP_GADGETS_GRENADE, user, player);

        inventory.pagedController().publishUpdatePage();
    }

    private void buyItems(String clickedItem, CustomItem customItem, InventoryItems inventoryItems, User user, Player player) {
        if (clickedItem.equals(inventoryItems.itemID())) {
            int cost = inventoryItems.getCost();
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            Hall hall = woolManiaPlayer.getHall();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            if (clickedItem.equals(inventoryItems.itemID())) {
                player.sendMessage("§7Richtige ID");
                customItem.setTier(hall.getTier());
                customItem.setLevel(hall.getHallValue().getValue());
                customItem.updateItemLore();
            }

            WoolMania.getStaticPlayer(player).removeMoney(cost, player);
            user.sendMessage(ITEM_BUYED, inventoryItems.getItem(user, customItem.getAmount(), "").displayname(), cost);
            BUY.playSound(player);
            player.getInventory().addItem(customItem.getItemStack());
        }
    }

}
