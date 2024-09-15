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
import static eu.darkcube.system.woolmania.util.message.Message.NO_MONEY;

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
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryUpgradeTier implements TemplateInventoryListener {

    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public SmithInventoryUpgradeTier() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "smith_upgrade_tier"), 36);
        inventoryTemplate.title(VARKAS.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_4);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_SMITH_UPGRADE_TIER);
        inventoryTemplate.pagination().pageSlots(23);
        inventoryTemplate.addListener(this);
    }

    private static class ContainerSpecificListenerUpgradeTier implements TemplateInventoryListener {
        private final Container container;

        public ContainerSpecificListenerUpgradeTier(Container container) {
            this.container = container;
        }

        @Override
        public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
            String clickedInventoryItem = InventoryItems.getItemID(item);
            Player player = Bukkit.getPlayer(user.uniqueId());
            if (clickedInventoryItem == null) return;
            ItemBuilder itemBuilder = container.getAt(0);
            if (itemBuilder == null) return;

            upgradeItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_UPGRADE_TIER_ICON, user, player);
            upgradeItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_UPGRADE_TIER_ICON_NONE, user, player);

            inventory.pagedController().publishUpdatePage();
        }

        private void upgradeItem(ItemBuilder itemBuilder, String clickedItem, InventoryItems inventoryItems, User user, Player player) {
            if (!clickedItem.equals(inventoryItems.itemID())) return;

            WoolManiaPlayer woolManiaPlayer = new WoolManiaPlayer(player);
            itemBuilder = itemBuilder.clone();
            CustomItem customItem = new CustomItem(itemBuilder);

            if (!customItem.hasMaxDurability()) {
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

            customItem.setLevel(customItem.getTier().getPlayerLevel() + 1);
            customItem.setTier(Tiers.getTierByID(customItem.getTierID() + 1));
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
        return customItem.hasMaxDurability();
    }

    private ItemBuilder skullItem(InventoryItems items, Container container, User user) {
        var containerItem = Objects.requireNonNull(container.getAt(0));
        var customItem = new CustomItem(containerItem);
        var cost = getCost(customItem);
        return items.getItem(user, cost);
    }

    @Override
    public void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
        var container = Container.simple(1);
        var content = inventory.pagedController().staticContent();

        content.addItem((Function<User, Object>) u -> {
            return showSkulls(container) ? skullItem(INVENTORY_SMITH_UPGRADE_TIER_ICON, container, u) : INVENTORY_SMITH_UPGRADE_TIER_ICON_NONE;
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
        inventory.addListener(new ContainerSpecificListenerUpgradeTier(container));
    }

    private static int getCost(@NotNull CustomItem customItem) {
        int tier = customItem.getTierID();
        return tier * 10000;
    }
}
