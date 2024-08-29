/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.shop;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
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
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.food.CarrotItem;
import eu.darkcube.system.woolmania.items.food.DiamondItem;
import eu.darkcube.system.woolmania.items.food.MelonItem;
import eu.darkcube.system.woolmania.items.food.SteakItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShopInventoryFood implements TemplateInventoryListener {

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

    public ShopInventoryFood() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "shop_food"), 45);
        inventoryTemplate.title(ZINUS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SHOP_FOOD);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));
        PagedInventoryContent content = pagination.content();

        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_FOOD_CARROT, stack));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_FOOD_MELON, stack));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_FOOD_STEAK, stack));
        content.addStaticItem(getDisplayItem(INVENTORY_SHOP_FOOD_DIAMOND, stack));

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

        buyFood(clickedInventoryItem, new CarrotItem(user), INVENTORY_SHOP_FOOD_CARROT, user, player);
        buyFood(clickedInventoryItem, new MelonItem(user), INVENTORY_SHOP_FOOD_MELON, user, player);
        buyFood(clickedInventoryItem, new SteakItem(user), INVENTORY_SHOP_FOOD_STEAK, user, player);
        buyFood(clickedInventoryItem, new DiamondItem(user), INVENTORY_SHOP_FOOD_DIAMOND, user, player);

        inventory.pagedController().publishUpdatePage();
    }

    private void buyFood(String clickedItem, CustomItem customItem, InventoryItems inventoryItems, User user, Player player) {
        if (clickedItem.equals(inventoryItems.itemID())) {
            int cost = inventoryItems.getCost();
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            WoolMania.getStaticPlayer(player).removeMoney(cost, player);
            user.sendMessage(ITEM_BUYED, inventoryItems.getItem(user, customItem.getAmount(), "").displayname(), cost);
            BUY.playSound(player);
            player.getInventory().addItem(customItem.getItemStack());
        }
    }

}
