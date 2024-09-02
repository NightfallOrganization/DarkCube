/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.smith;

import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_SMITH_SHOP_ITEMS;
import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_SMITH_SHOP_ITEMS_SHEARS;
import static eu.darkcube.system.woolmania.enums.Names.VARKAS;
import static eu.darkcube.system.woolmania.enums.Sounds.BUY;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.time.Duration;
import java.time.Instant;
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
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.ShearItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryShopItems implements TemplateInventoryListener {

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

    public SmithInventoryShopItems() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "smith_shop_items"), 45);
        inventoryTemplate.title(VARKAS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SMITH_SHOP_ITEMS);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(getDisplayItem(INVENTORY_SMITH_SHOP_ITEMS_SHEARS, 1));

        inventoryTemplate.addListener(this);
    }

    private Function<User, Object> getDisplayItem(InventoryItems item, int amount) {
        return user -> {

            if (item.getCost() == 0) {
                return item.getItem(user, amount, ITEM_BUY_FREE.getMessage(user));
            } else {
                return item.getItem(user, amount, ITEM_BUY_COST.getMessage(user, item.getCost()));
            }
        };
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        String clickedInventoryItem = InventoryItems.getItemID(item);
        if (clickedInventoryItem == null) return;
        Player player = Bukkit.getPlayer(user.uniqueId());

        buyItem(clickedInventoryItem, new ShearItem(user), INVENTORY_SMITH_SHOP_ITEMS_SHEARS, user, player);

        inventory.pagedController().publishUpdatePage();
    }

    private void buyItem(String clickedItem, CustomItem customItem, InventoryItems inventoryItems, User user, Player player) {
        if (clickedItem.equals(inventoryItems.itemID())) {
            int cost = inventoryItems.getCost();
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();
            Instant lastBuyTime = Instant.now();
            user.metadata().set();

            Duration addTime = Duration.ofMinutes(5);
            Instant compareTime = lastBuyTime.plus(addTime);

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            if (cost == 0 && Instant.now().compareTo(compareTime) > 0) {
                user.sendMessage(ITEM_BUYED_FREE, inventoryItems.getItem(user, customItem.getAmount(), "").displayname());
            } else if (cost > 0 && Instant.now().compareTo(compareTime) > 0) {
                user.sendMessage(ITEM_BUYED, inventoryItems.getItem(user, customItem.getAmount(), "").displayname(), cost);
            } else {
                user.sendMessage(ITEM_COOLDOWN, compareTime);
            }

            WoolMania.getStaticPlayer(player).removeMoney(cost, player);
            BUY.playSound(player);

            if (Instant.now().compareTo(compareTime) > 0) {
                player.getInventory().addItem(customItem.getItemStack());
            }

        }
    }

}
