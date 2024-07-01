/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerConfiguration;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.gameregistry.RegistryEntry;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryGameServerSelection extends LobbyAsyncPagedInventory {

    public static final String ITEMID = "pserver_gameserver";
    public static final Key SLOT = Key.key(Lobby.getInstance(), "pserver.gameserver.slot");
    public static final Key SERVICE = Key.key(Lobby.getInstance(), "pserver.gameserver.service");
    public static final PersistentDataType<RegistryEntry> SERVICE_TYPE = RegistryEntry.TYPE;
    protected final int[] itemSort;
    private final Item item;
    private final Supplier<Collection<RegistryEntry>> supplier;
    private final BiFunction<User, RegistryEntry, ItemBuilder> toItemFunction;
    private final User auser;
    private boolean done;

    public InventoryGameServerSelection(User user, Item item, InventoryType type, Supplier<Collection<RegistryEntry>> supplier, BiFunction<User, RegistryEntry, ItemBuilder> toItemFunction) {
        super(type, item.getDisplayName(user), UserWrapper.fromUser(user));
        auser = user;
        this.supplier = supplier;
        this.toItemFunction = toItemFunction;
        this.item = item;
        //@formatter:off
        this.itemSort = new int[]{
            10, 9, 11, 8, 12, 3, 17,
            16, 18, 2, 4, 7, 13, 15,
            19, 1, 5, 14, 20, 0, 6
        };
        //@formatter:on
        this.done = true;
        this.complete();
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        var itemid = Item.getItemId(event.item());
        if (itemid == null) return;
        if (itemid.equals(ITEMID)) {
            user.setOpenInventory(new InventoryLoading(getTitle(), user.user(), lobbyUser -> {
                var registryEntry = event.item().persistentDataStorage().get(SERVICE, SERVICE_TYPE);
                if (registryEntry == null) return InventoryGameServerSelection.this;
                var ps = new PServerBuilder().type(Type.GAMEMODE).taskName(registryEntry.taskName()).accessLevel(AccessLevel.PUBLIC).create().join();
                ps.storage().setAsync(SERVICE, SERVICE_TYPE, registryEntry).join();
                ps.addOwner(user.user().uniqueId()).join();
                return new InventoryPServerConfiguration(user.user(), ps.id());
            }));
        }
    }

    @Override
    protected boolean done() {
        return super.done() && this.done;
    }

    @Override
    protected void fillItems(Map<Integer, ItemStack> items) {
        int slot = 0;
        var serviceTasks = this.supplier.get();
        for (var entry : serviceTasks) {
            var b = this.toItemFunction.apply(this.auser, entry);

            b.persistentDataStorage().set(SERVICE, SERVICE_TYPE, entry);
            b.persistentDataStorage().set(SLOT, PersistentDataTypes.INTEGER, slot);
            Item.setItemId(b, InventoryGameServerSelection.ITEMID);
            items.put(this.itemSort[slot % this.itemSort.length] + this.itemSort.length * (slot / this.itemSort.length), b.build());
            slot++;
        }
    }

    @Override
    protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), this.item.getItem(this.auser));
        super.insertFallbackItems();
    }
}
