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
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.PServerUtil;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.bukkit.event.PServerStartEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerStopEvent;
import eu.darkcube.system.pserver.bukkit.event.PServerUpdateEvent;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerExecutor.AccessLevel;
import eu.darkcube.system.pserver.common.PServerExecutor.State;
import eu.darkcube.system.pserver.common.PServerExecutor.Type;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;

public class InventoryPServer extends LobbyAsyncPagedInventory {

    public static final String ITEMID = "lobbysystem.pserver.publiclist";

    public static final Key META_KEY_PSERVER = new Key(Lobby.getInstance(), "lobbysystem.pserver.id");

    private static final InventoryType type_pserver = InventoryType.of("pserver");
    private static final Set<UUID> connecting = new CopyOnWriteArraySet<>();

    private Listener listener;

    public InventoryPServer(LobbyUser user) {
        super(InventoryPServer.type_pserver, Item.PSERVER_PUBLIC.getDisplayName(user.getUser()), user);
        // super(Item.PSERVER_MAIN_ITEM.getDisplayName(user), InventoryPServer.type_pserver);
        this.listener = new Listener();
        this.listener.register();
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String itemid = Item.getItemId(event.item());
        if (itemid == null) return;
        if (!event.item().persistentDataStorage().has(META_KEY_PSERVER)) return;
        UniqueId id = new UniqueId(event.item().persistentDataStorage().get(META_KEY_PSERVER, PersistentDataTypes.STRING));
        PServerProvider.instance().pserver(id).thenAcceptAsync(ps -> {
            try {
                if (!PServerUtil.mayJoin(UserWrapper.fromUser(event.user()), ps).get()) {
                    event.user().sendMessage(Message.PSERVER_NOT_PUBLIC);
                }
                if (!connecting.add(event.user().getUniqueId())) {
                    return;
                }
                ps.connectPlayer(event.user().getUniqueId()).thenAccept(suc -> {
                    if (!suc) recalculate();
                    connecting.remove(event.user().getUniqueId());
                });
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        try {
            SortedMap<Long, ItemStack> sitems = new TreeMap<>();

            for (PServerExecutor ps : PServerProvider.instance().pservers().get()) {
                State state = ps.state().get();
                if (state != State.RUNNING) continue;
                if (!PServerUtil.mayJoin(user, ps).get()) continue;
                AccessLevel accessLevel = ps.accessLevel().get();
                boolean publicServer = accessLevel == AccessLevel.PUBLIC;
                int online = ps.onlinePlayers().get();
                long ontime = ps.ontime().get();
                UUID owner = ps.owners().get().stream().findAny().orElse(null);

                ItemBuilder b = null;

                if (ps.type().get() == Type.GAMEMODE) {
                    b = PServerDataManager.getDisplayItemGamemode(this.user.getUser(), ps.taskName().get());
                }
                if (b == null) {
                    if (owner == null) {
                        b = ItemBuilder.item(Material.BARRIER);
                    } else {
                        b = ItemBuilder.item(Material.SKULL_ITEM).damage(SkullType.PLAYER.ordinal());
                        ItemStack item = b.build();
                        item.setItemMeta(SkullCache.getCachedItem(owner).getItemMeta());
                        b = ItemBuilder.item(item);
                    }
                }
                b.amount(online);
                b.displayname(Message.PSERVER_ITEM_TITLE.getMessage(this.user.getUser(), ps.serverName().get()));
                b.lore(publicServer ? Message.CLICK_TO_JOIN.getMessage(this.user.getUser()) : Message.PSERVER_NOT_PUBLIC.getMessage(this.user.getUser()));
                Item.setItemId(b, InventoryPServer.ITEMID);
                b.persistentDataStorage().set(InventoryPServer.META_KEY_PSERVER, PersistentDataTypes.STRING, ps.id().toString());
                sitems.put(ontime, b.build());
            }

            int slot = 0;
            for (long ontime : sitems.keySet()) {
                items.put(slot, sitems.get(ontime));
                slot++;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override protected void insertFallbackItems() {
        this.fallbackItems.put(IInventory.slot(1, 3), Item.LIME_GLASS_PANE.getItem(this.user.getUser()));
        this.fallbackItems.put(IInventory.slot(1, 4), Item.INVENTORY_PSERVER_PUBLIC.getItem(this.user.getUser()));
        this.fallbackItems.put(IInventory.slot(1, 5), Item.LIME_GLASS_PANE.getItem(this.user.getUser()));
        this.fallbackItems.put(IInventory.slot(1, 6), Item.INVENTORY_PSERVER_PRIVATE.getItem(this.user.getUser()));
        super.insertFallbackItems();
    }

    @Override protected void destroy() {
        super.destroy();
        this.listener.unregister();
    }

    public class Listener {

        public void register() {
            InjectionLayer.boot().instance(EventManager.class).registerListener(this);
        }

        public void unregister() {
            InjectionLayer.boot().instance(EventManager.class).unregisterListener(this);
        }

        @EventListener public void handle(PServerUpdateEvent event) {
            InventoryPServer.this.recalculate();
        }

        @EventListener public void handle(PServerStartEvent event) {
            recalculate();
        }

        @EventListener public void handle(PServerStopEvent event) {
            recalculate();
        }

    }

}
