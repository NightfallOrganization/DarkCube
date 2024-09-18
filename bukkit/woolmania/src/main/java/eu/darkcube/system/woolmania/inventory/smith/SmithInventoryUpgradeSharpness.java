/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.smith;

import static eu.darkcube.system.woolmania.enums.InventoryItems.*;
import static eu.darkcube.system.woolmania.enums.Names.VARKAS;
import static eu.darkcube.system.woolmania.enums.Sounds.BUY;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.util.Objects;
import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.container.Container;
import eu.darkcube.system.server.inventory.container.ContainerListener;
import eu.darkcube.system.server.inventory.listener.ClickData;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryUpgradeSharpness implements TemplateInventoryListener {

    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public SmithInventoryUpgradeSharpness() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "smith_upgrade_sharpness"), 36);
        inventoryTemplate.title(VARKAS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_4);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SMITH_UPGRADE_SHARPNESS);
        inventoryTemplate.pagination().pageSlots(23);
        inventoryTemplate.addListener(this);
    }

    private static class ContainerSpecificListener implements TemplateInventoryListener {
        private final Container container;

        public ContainerSpecificListener(Container container) {
            this.container = container;
        }

        @Override
        public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
            String clickedInventoryItem = InventoryItems.getItemID(item);
            Player player = Bukkit.getPlayer(user.uniqueId());
            if (clickedInventoryItem == null) return;
            ItemBuilder itemBuilder = container.getAt(0);
            if (itemBuilder == null) return;

            upgradeItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_UPGRADE_SHARPNESS_ICON, user, player);
            upgradeItem(itemBuilder, clickedInventoryItem, INVENTORY_ICON_HEAD_NONE_100, user, player);

            inventory.pagedController().publishUpdatePage();
        }

        private void upgradeItem(ItemBuilder itemBuilder, String clickedItem, InventoryItems inventoryItems, User user, Player player) {
            if (!clickedItem.equals(inventoryItems.itemID())) return;

            WoolManiaPlayer woolManiaPlayer = new WoolManiaPlayer(player);
            itemBuilder = itemBuilder.clone();
            CustomItem customItem = new CustomItem(itemBuilder);

            if (!customItem.hasSharpness() || customItem.isFood()) {
                NO.playSound(player);
                return;
            }

            var cost = getCost(customItem);
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            customItem.setSharpness(customItem.getSharpness() + 1);
            customItem.updateSharpness();
            customItem.updateItemLore();
            woolManiaPlayer.removeMoney(cost, player);
            BUY.playSound(player);
            // user.sendMessage(ITEM_REPAIR, durability, cost);
            container.setAt(0, itemBuilder);
        }
    }

    private boolean showSkulls(Container container) {
        var item = container.getAt(0);
        if (item == null) return false;
        CustomItem customItem = new CustomItem(item);
        return customItem.hasSharpness() && !customItem.isFood() && customItem.getSharpness() != -1;
    }

    private ItemBuilder skullItem(Container container, User user) {
        var containerItem = Objects.requireNonNull(container.getAt(0));
        var customItem = new CustomItem(containerItem);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = new WoolManiaPlayer(player);
        int cost = getCost(customItem);
        int sharpness = customItem.getSharpness();
        int sharpnessUpgrade = customItem.getSharpness() + 1;

        ItemBuilder item = INVENTORY_SMITH_UPGRADE_SHARPNESS_ICON.getItem(user, sharpness, sharpnessUpgrade);

        if (woolManiaPlayer.getMoney() < cost) {
            return item.lore(SEPARATION.getMessage(user)).lore(COST_NOT_ENOUGH.getMessage(user, cost));
        } else {
            return item.lore(SEPARATION.getMessage(user)).lore(COST_ENOUGH.getMessage(user, cost));
        }
    }

    @Override
    public void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
        var container = Container.simple(1);
        var content = inventory.pagedController().staticContent();

        content.addItem((Function<User, Object>) u -> {
            return showSkulls(container) ? skullItem(container, u) : INVENTORY_ICON_HEAD_NONE_100;
        });

        container.addListener(new ContainerListener() {

            @Override
            public void onItemAdded(int slot, @NotNull ItemBuilder item, int amountAdded) {
                inventory.pagedController().publishUpdatePage();
            }

            @Override
            public void onItemRemoved(int slot, @NotNull ItemBuilder previousItem, int amountRemoved) {
                inventory.pagedController().publishUpdatePage();
            }

            @Override
            public void onItemChanged(int slot, @NotNull ItemBuilder previousItem, @NotNull ItemBuilder newItem) {
                inventory.pagedController().publishUpdatePage();
            }
        });

        inventory.addContainer(1, container, (u, view) -> {
            view.slots(21);
            view.dropItemsOnClose(true);
        });
        inventory.addListener(new ContainerSpecificListener(container));
    }

    private static int getCost(@NotNull CustomItem customItem) {
        int sharpness = customItem.getSharpness();
        return (sharpness + 1) * 10000;
    }
}
