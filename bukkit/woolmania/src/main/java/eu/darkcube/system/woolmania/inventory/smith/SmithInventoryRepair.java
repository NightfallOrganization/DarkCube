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
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SmithInventoryRepair implements TemplateInventoryListener {

    private static final DataKey<Double> REPAIR_PERCENT = DataKey.of(Key.key(WoolMania.getInstance(), "repair_percent"), PersistentDataTypes.DOUBLE);
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

        inventoryTemplate.pagination().pageSlots(19, 20, 24, 25);
        inventoryTemplate.addListener(this);
    }

    private static class ContainerSpecificListenerRepair implements TemplateInventoryListener {
        private final Container container;

        public ContainerSpecificListenerRepair(Container container) {
            this.container = container;
        }

        @Override
        public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item, @NotNull ClickData clickData) {
            String clickedInventoryItem = InventoryItems.getItemID(item);
            Player player = Bukkit.getPlayer(user.uniqueId());
            if (clickedInventoryItem == null) return;
            ItemBuilder itemBuilder = container.getAt(0);
            if (itemBuilder == null) return;

            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_REPAIR_25, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_REPAIR_50, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_REPAIR_75, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_SMITH_REPAIR_100, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_ICON_HEAD_NONE_25, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_ICON_HEAD_NONE_50, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_ICON_HEAD_NONE_75, user, player, item);
            repairItem(itemBuilder, clickedInventoryItem, INVENTORY_ICON_HEAD_NONE_100, user, player, item);

            inventory.pagedController().publishUpdatePage();
        }

        private void repairItem(ItemBuilder itemBuilder, String clickedItem, InventoryItems inventoryItems, User user, Player player, ItemBuilder item) {
            if (!clickedItem.equals(inventoryItems.itemID())) return;

            WoolManiaPlayer woolManiaPlayer = new WoolManiaPlayer(player);
            itemBuilder = itemBuilder.clone();
            CustomItem customItem = new CustomItem(itemBuilder);

            if (!item.persistentDataStorage().has(REPAIR_PERCENT)) {
                NO.playSound(player);
                return;
            }

            double percent = Objects.requireNonNull(item.persistentDataStorage().get(REPAIR_PERCENT));
            var cost = getCost(customItem, percent);
            int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

            if (cost > playerMoney) {
                user.sendMessage(NO_MONEY);
                NO.playSound(player);
                return;
            }

            int missingDurability = customItem.getMaxDurability() - customItem.getDurability();
            int repairValue = (int) Math.ceil(missingDurability * percent);
            int newDurability = customItem.getDurability() + repairValue;
            customItem.setDurability(newDurability);
            customItem.updateItemLore();
            woolManiaPlayer.removeMoney(cost, player);
            BUY.playSound(player);
            int finalPercent = (int) (percent * 100);
            user.sendMessage(ITEM_REPAIR, finalPercent, cost);
            container.setAt(0, itemBuilder);
        }
    }

    private boolean showSkulls(Container container) {
        var item = container.getAt(0);
        if (item == null) return false;
        CustomItem customItem = new CustomItem(item);

        if (customItem.getMaxDurability() == customItem.getDurability()) {
            return false;
        }

        return customItem.hasMaxDurability();
    }

    private ItemBuilder skullItem(InventoryItems items, Container container, User user, double percent) {
        var containerItem = Objects.requireNonNull(container.getAt(0));
        var customItem = new CustomItem(containerItem);
        int cost = getCost(customItem, percent);
        var item = items.getItem(user, cost);
        item.persistentDataStorage().set(REPAIR_PERCENT, percent);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = new WoolManiaPlayer(player);

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
            return showSkulls(container) ? skullItem(INVENTORY_SMITH_REPAIR_25, container, u, 0.25) : INVENTORY_ICON_HEAD_NONE_25;
        });
        content.addItem((Function<User, Object>) u -> {
            return showSkulls(container) ? skullItem(INVENTORY_SMITH_REPAIR_50, container, u, 0.5) : INVENTORY_ICON_HEAD_NONE_50;
        });
        content.addItem((Function<User, Object>) u -> {
            return showSkulls(container) ? skullItem(INVENTORY_SMITH_REPAIR_75, container, u, 0.75) : INVENTORY_ICON_HEAD_NONE_75;
        });
        content.addItem((Function<User, Object>) u -> {
            return showSkulls(container) ? skullItem(INVENTORY_SMITH_REPAIR_100, container, u, 1.0) : INVENTORY_ICON_HEAD_NONE_100;
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
            view.slots(22);
            view.dropItemsOnClose(true);
        });
        inventory.addListener(new ContainerSpecificListenerRepair(container));
    }

    private static int getCost(@NotNull CustomItem customItem, double percent) {
        var missingDurability = customItem.getMaxDurability() - customItem.getDurability();
        return (int) Math.ceil(missingDurability * 3 * percent);
    }

}
