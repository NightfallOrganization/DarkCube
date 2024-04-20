/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory;

import java.util.Comparator;
import java.util.Map;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryLobbySwitcher extends LobbyAsyncPagedInventory {
    public static final Key server = new Key(Lobby.getInstance(), "server");
    private static final InventoryType type_lobby_switcher = InventoryType.of("lobby_switcher");
    // @formatter:off
    private static final int[] SLOTS = new int[] {
        31, 30, 32, 29, 33, 28, 34,
        22, 21, 23, 20, 24, 19, 25,
        40, 39, 41, 38, 42, 37, 43
    };
    // @formatter:on
    public InventoryLobbySwitcher(User user) {
        super(InventoryLobbySwitcher.type_lobby_switcher, Item.INVENTORY_LOBBY_SWITCHER.getDisplayName(user), user);
        System.arraycopy(InventoryLobbySwitcher.SLOTS, 0, this.pageSlots, 0, InventoryLobbySwitcher.SLOTS.length);
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        super.fillItems(items);
        var serviceInfo = InjectionLayer.boot().instance(ServiceInfoHolder.class).serviceInfo();
        var service = serviceInfo.serviceId();
        var taskName = service.taskName();
        var lobbies = InjectionLayer.boot().instance(CloudServiceProvider.class).servicesByTask(taskName).stream().filter(ServiceInfoSnapshot::connected).sorted(Comparator.comparingInt(o -> o.serviceId().taskServiceId())).toList();

        if (lobbies.isEmpty()) {
            var item = new ItemStack(Material.BARRIER);
            var meta = item.getItemMeta();
            meta.setDisplayName("§cUnable to load lobbies!");
            items.put(0, item);
            return;
        }
        final var lobbyCount = lobbies.size();

        for (var i = 0; i < Math.min(InventoryLobbySwitcher.SLOTS.length, lobbyCount); i++) {
            var s = lobbies.get(i);
            var item = Item.INVENTORY_LOBBY_SWITCHER_OTHER.getItem(this.user.user());
            var meta = item.getItemMeta();
            var id = s.serviceId().name();
            if (!id.isEmpty()) {
                id = Character.toUpperCase(id.charAt(0)) + id.substring(1).replace("-", " ");
            }
            if (s.serviceId().equals(serviceInfo.serviceId())) {
                item = Item.INVENTORY_LOBBY_SWITCHER_CURRENT.getItem(this.user.user());
                meta.setDisplayName("§c" + id);
            } else {
                meta.setDisplayName("§a" + id);
            }
            item.setItemMeta(meta);
            item = ItemBuilder.item(item).persistentDataStorage().iset(server, PersistentDataTypes.STRING, s.serviceId().name()).builder().build();
            items.put(i, item);
        }
    }

    @Override
    protected void insertArrowItems() {
        super.insertArrowItems();
    }

    @Override
    protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), Item.INVENTORY_LOBBY_SWITCHER.getItem(this.user.user()));
        super.insertFallbackItems();
    }
}
