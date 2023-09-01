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
import eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.listener.ListenerPServer;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.userapi.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class InventoryPServerConfiguration extends LobbyAsyncPagedInventory implements Listener {

    private static final InventoryType type_pserver_configuration = InventoryType.of("type_pserver_configuration");
    public final UniqueId pserverId;
    private final ListenerPServer listenerPServer = InjectionLayer.ext().instance(ListenerPServer.class);
    private boolean done;

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
        var player = user.asPlayer();
        if (player == null) return;
        if (itemid.equals(Item.PSERVER_DELETE.getItemId())) {
            user.setOpenInventory(new InventoryConfirm(getTitle(), user.user(), () -> {
                PServerExecutor ps = PServerProvider.instance().pserver(pserverId).join();
                ps.removeOwner(user.user().uniqueId()).join();
                player.closeInventory();
            }, () -> user.setOpenInventory(new InventoryPServerConfiguration(user.user(), pserverId))));
        } else if (itemid.equals(Item.START_PSERVER.getItemId())) {
            player.closeInventory();
            startPServer();
        } else if (itemid.equals(Item.STOP_PSERVER.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenApply(PServerExecutor::stop);
        } else if (itemid.equals(Item.PSERVER_PUBLIC.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenAccept(ps -> ps.accessLevel(AccessLevel.PRIVATE));
        } else if (itemid.equals(Item.PSERVER_PRIVATE.getItemId())) {
            PServerProvider.instance().pserver(pserverId).thenAccept(ps -> ps.accessLevel(AccessLevel.PUBLIC));
        }
    }

    private void connectPlayer(PServerExecutor pserver) {
        pserver.type().thenAccept(type -> listenerPServer.connectPlayer(user.user(), pserver)).whenComplete((unused, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                listenerPServer.failed(user.user());
            }
        });
    }

    private void startPServer() {

        PServerProvider.instance().pservers(user.user().uniqueId()).thenCompose(pserverIds -> {
            CompletableFuture<Boolean>[] futs = new CompletableFuture[pserverIds.size()];
            int i = 0;
            for (var id : pserverIds) {
                futs[i++] = PServerProvider.instance().pserverExists(id);
            }
            return CompletableFuture.allOf(futs).thenApply(ignored -> {
                for (CompletableFuture<Boolean> fut : futs) {
                    if (fut.join()) {
                        return false;
                    }
                }
                return true;
            });
        }).thenAccept(mayStart -> {
            if (!mayStart) {
                user.user().sendMessage(Message.MAY_NOT_START_MULTIPLE_PSERVERS);
                return;
            }
            BukkitTask task = new BukkitRunnable() {
                @Override public void run() {
                    user.user().sendActionBar(Message.CONNECTING_TO_PSERVER_AS_SOON_AS_ONLINE);
                }
            }.runTaskTimer(Lobby.getInstance(), 1, 1);
            if (!listenerPServer.setStarted(user.user(), pserverId, fail -> task.cancel())) return;
            PServerProvider.instance().pserver(pserverId).thenCompose(ex -> ex.start().whenComplete((started, throwable) -> {
                if (throwable == null && started) {
                    connectPlayer(ex);
                }
            })).whenComplete((started, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    listenerPServer.failed(user.user());
                } else if (!started) {
                    listenerPServer.failed(user.user());
                }
            });
        });
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

    @EventListener public void handle(PServerUpdateEvent event) {
        if (!event.pserver().id().equals(pserverId)) return;
        this.recalculate();
    }
}
