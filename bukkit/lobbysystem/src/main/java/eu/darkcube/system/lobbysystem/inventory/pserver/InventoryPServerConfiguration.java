/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.inventory.pserver;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class InventoryPServerConfiguration extends LobbyAsyncPagedInventory implements Listener {

    private static final InventoryType type_pserver_configuration = InventoryType.of("type_pserver_configuration");

    public final UniqueId pserverId;
    private boolean done;
    private volatile UUID connecting = null;

    public InventoryPServerConfiguration(User user, UniqueId pserverId) {
        super(type_pserver_configuration, getDisplayName(user, pserverId), user);
        this.pserverId = pserverId;
        InjectionLayer.boot().instance(EventManager.class).registerListener(this);
        Bukkit.getPluginManager().registerEvents(this, Lobby.getInstance());
        this.done = true;
        this.complete();
    }

    private static Component getDisplayName(User user, UniqueId pserverId) {
        ItemBuilder item = PServerDataManager.getDisplayItem(user, pserverId);
        return item == null ? null : item.displayname();
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String itemid = Item.getItemId(event.item());
        if (itemid == null) return;
        if (itemid.equals(Item.PSERVER_DELETE.getItemId())) {
            user.setOpenInventory(new InventoryConfirm(getTitle(), user.user(), () -> {
                PServerExecutor ps = PServerProvider.instance().pserver(pserverId).join();
                ps.removeOwner(user.user().uniqueId()).join();
                user.asPlayer().closeInventory();
            }, () -> user.setOpenInventory(new InventoryPServerConfiguration(user.user(), pserverId))));
        } else if (itemid.equals(Item.START_PSERVER.getItemId())) {
            user.asPlayer().closeInventory();
            AsyncExecutor.service().submit(() -> {
                if (connecting != null) return;
                try {
                    // TODO: Forbid player to start multiple pservers
                    PServerExecutor ex = PServerProvider.instance().pserver(pserverId).get();
                    connecting = user.user().uniqueId();
                    new BukkitRunnable() {
                        @Override public void run() {
                            if (connecting == null) {
                                cancel();
                                return;
                            }
                            user.user().sendActionBar(Message.CONNECTING_TO_PSERVER_AS_SOON_AS_ONLINE.getMessage(user.user()));
                        }
                    }.runTaskTimer(Lobby.getInstance(), 0, 10);
                    ex.start().thenRun(() -> {
                        ex.connectPlayer(user.user().uniqueId()).thenRun(() -> {
                            connecting = null;
                        });
                    }).exceptionally(e -> {
                        e.printStackTrace();
                        connecting = null;
                        return null;
                    });
                    //					Lobby.getInstance().getPServerJoinOnStart().register(user, ex);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        } else if (itemid.equals(Item.STOP_PSERVER.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenApply(PServerExecutor::stop);
        } else if (itemid.equals(Item.PSERVER_PUBLIC.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenAccept(ps -> ps.accessLevel(AccessLevel.PRIVATE));
        } else if (itemid.equals(Item.PSERVER_PRIVATE.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenAccept(ps -> ps.accessLevel(AccessLevel.PUBLIC));
        }
    }

    @Override protected boolean done() {
        return super.done() && this.done;
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        super.fillItems(items);
        items.put(8, Item.PSERVER_DELETE.getItem(this.user.user()));
        try {
            PServerExecutor ps = PServerProvider.instance().pserver(pserverId).get();
            State state = ps.state().get();
            AccessLevel accessLevel = ps.accessLevel().get();
            if (state == State.OFFLINE) {
                items.put(12, Item.START_PSERVER.getItem(this.user.user()));
            } else {
                items.put(12, Item.STOP_PSERVER.getItem(this.user.user()));
            }
            if (accessLevel == AccessLevel.PRIVATE) {
                items.put(10, Item.PSERVER_PRIVATE.getItem(user.user()));
            } else if (accessLevel == AccessLevel.PUBLIC) {
                items.put(10, Item.PSERVER_PUBLIC.getItem(user.user()));
            } else {
                items.put(10, new ItemStack(Material.BARRIER));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 5), PServerDataManager.getDisplayItem(this.user.user(), pserverId).build());
        super.insertFallbackItems();
    }

    @Override protected void destroy() {
        super.destroy();
        HandlerList.unregisterAll(this);
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(this);
    }

    @EventListener public void handle(PServerStopEvent event) {
        if (event.pserver().id().equals(pserverId)) {
            connecting = null;
        }
    }

    @EventHandler public void handle(PlayerQuitEvent event) {
        connecting = null;
    }

    @EventListener public void handle(PServerUpdateEvent event) {
        if (!event.pserver().id().equals(pserverId)) {
            return;
        }
        this.recalculate();
    }
}
