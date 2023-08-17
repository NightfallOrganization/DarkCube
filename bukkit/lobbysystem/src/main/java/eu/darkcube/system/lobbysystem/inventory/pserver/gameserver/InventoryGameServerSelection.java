/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerConfiguration;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class InventoryGameServerSelection extends LobbyAsyncPagedInventory {

    public static final String ITEMID = "pserver_gameserver";
    public static final Key SLOT = new Key(Lobby.getInstance(), "pserver.gameserver.slot");
    public static final Key SERVICETASK = new Key(Lobby.getInstance(), "pserver.gameserver.serviceTask");
    protected final int[] itemSort;
    private final Item item;
    private final Supplier<Collection<ServiceTask>> supplier;
    private final BiFunction<User, ServiceTask, ItemBuilder> toItemFunction;
    private final User auser;
    private boolean done = false;

    public InventoryGameServerSelection(User user, Item item, InventoryType type, Supplier<Collection<ServiceTask>> supplier, BiFunction<User, ServiceTask, ItemBuilder> toItemFunction) {
        super(type, item.getDisplayName(user), UserWrapper.fromUser(user));
        auser = user;
        this.supplier = supplier;
        this.toItemFunction = toItemFunction;
        this.item = item;
        this.itemSort = new int[]{
                //@formatter:off
				10, 9, 11, 8, 12, 3, 17, 
				16, 18, 2, 4, 7, 13, 15,
				19, 1, 5, 14, 20, 0, 6
				//@formatter:on
        };
        this.done = true;
        this.complete();
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String itemid = Item.getItemId(event.item());
        if (itemid == null) return;
        if (itemid.equals(ITEMID)) {
            user.setOpenInventory(new InventoryLoading(getTitle(), user.getUser(), lobbyUser -> {
                ServiceTask serviceTask = InjectionLayer
                        .boot()
                        .instance(ServiceTaskProvider.class)
                        .serviceTask(event.item().persistentDataStorage().get(SERVICETASK, PersistentDataTypes.STRING));
                if (serviceTask == null) return InventoryGameServerSelection.this;
                try {
                    PServerExecutor ps = new PServerBuilder()
                            .type(Type.GAMEMODE)
                            .taskName(serviceTask.name())
                            .accessLevel(AccessLevel.PUBLIC)
                            .create()
                            .get();
                    ps.addOwner(user.getUser().getUniqueId()).get();
                    Thread.sleep(1000);
                    return new InventoryPServerConfiguration(user.getUser(), ps.id());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }

    @Override protected boolean done() {
        return super.done() && this.done;
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        int slot = 0;
        Collection<ServiceTask> serviceTasks = this.supplier.get();
        for (ServiceTask serviceTask : serviceTasks) {
            ItemBuilder b = this.toItemFunction.apply(this.auser, serviceTask);
            b.persistentDataStorage().set(SERVICETASK, PersistentDataTypes.STRING, serviceTask.name());
            b.persistentDataStorage().set(SLOT, PersistentDataTypes.INTEGER, slot);
            Item.setItemId(b, InventoryGameServerSelection.ITEMID);
            items.put(this.itemSort[slot % this.itemSort.length] + this.itemSort.length * (slot / this.itemSort.length), b.build());
            slot++;
        }
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), this.item.getItem(this.auser));
        super.insertFallbackItems();
    }
}
